import { useEffect, useState, useRef } from 'react'
import { getCurrentUser, logout } from './api/auth'
import { LoginScreen } from './components/LoginScreen'
import { PhaserGame } from './game/PhaserGame'
import { FloatingButtons } from './components/FloatingButtons'
import { OurFeelingOverlay } from './components/OurFeelingOverlay'
import { OurTimeOverlay } from './components/OurTimeOverlay'
import { RoomBanner } from './components/RoomBanner'
import { RoomModal } from './components/RoomModal'
import { useRealtime } from './hooks/useRealtime'
import { useGameStore } from './store/useGameStore'
import './App.css'

const AUTH_URL   = import.meta.env.VITE_AUTH_URL   || 'http://localhost:8081'
const COUPLE_URL = import.meta.env.VITE_COUPLE_URL || 'http://localhost:8083'

// Always refresh token on load so we get the latest coupleId embedded
async function doTokenRefresh(): Promise<boolean> {
  const refreshToken = localStorage.getItem('refreshToken')
  if (!refreshToken) return false
  try {
    const res = await fetch(`${AUTH_URL}/api/auth/refresh`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ refreshToken }),
    })
    if (!res.ok) { localStorage.clear(); return false }
    const data = await res.json()
    localStorage.setItem('accessToken', data.accessToken)
    localStorage.setItem('refreshToken', data.refreshToken)
    // Extract coupleId from new token
    const payload = JSON.parse(atob(data.accessToken.split('.')[1]))
    if (payload.coupleId) {
      localStorage.setItem('coupleId', payload.coupleId)
    }
    return true
  } catch {
    return false
  }
}

// Check couple status directly from couple-service (ground truth)
async function fetchCoupleStatus(): Promise<string | null> {
  const token = localStorage.getItem('accessToken')
  if (!token) return null
  try {
    const res = await fetch(`${COUPLE_URL}/api/couples/me`, {
      headers: { 'Authorization': `Bearer ${token}` },
    })
    if (!res.ok) return null
    const data = await res.json()
    const coupleId = data.coupleId as string
    if (coupleId) localStorage.setItem('coupleId', coupleId)
    return coupleId || null
  } catch {
    return null
  }
}

export function getCoupleId(): string | null {
  // Read from localStorage cache (set during pairing or on load)
  return localStorage.getItem('coupleId')
}

type AppState = 'loading' | 'login' | 'pairing' | 'world'

export default function App() {
  const [state, setState] = useState<AppState>('loading')

  useEffect(() => {
    (async () => {
      const hasTokens = !!localStorage.getItem('refreshToken')
      if (!hasTokens) { setState('login'); return }

      // Refresh token first to get latest coupleId
      const ok = await doTokenRefresh()
      if (!ok) { setState('login'); return }

      // Check if we have a coupleId in the token
      let coupleId = localStorage.getItem('coupleId')

      // If not in token, ask couple-service directly
      if (!coupleId) {
        coupleId = await fetchCoupleStatus()
      }

      setState(coupleId ? 'world' : 'pairing')
    })()
  }, [])

  const handleAuthenticated = async () => {
    // After login, refresh token and check couple status
    await doTokenRefresh()
    const coupleId = await fetchCoupleStatus()
    setState(coupleId ? 'world' : 'pairing')
  }

  const handlePaired = () => setState('world')

  const handleLogout = () => {
    localStorage.clear()
    setState('login')
  }

  if (state === 'loading') {
    return (
      <div style={{
        display: 'flex', height: '100%', alignItems: 'center',
        justifyContent: 'center', fontFamily: 'sans-serif', color: '#b4b2a9',
        fontSize: 14, background: '#f5f3ee',
      }}>
        Loading...
      </div>
    )
  }

  if (state === 'login')   return <LoginScreen onAuthenticated={handleAuthenticated} />
  if (state === 'pairing') return <PairingScreen onPaired={handlePaired} onLogout={handleLogout} />
  return <World onLogout={handleLogout} />
}

// ── World ─────────────────────────────────────────────────────
function World({ onLogout }: { onLogout: () => void }) {
  const { nickname } = getCurrentUser()
  const activeRoom = useGameStore((s) => s.activeRoom)
  useRealtime({}, activeRoom || 'world')

  const me      = { emoji: '🐱', name: nickname || 'Me', color: 0x7f77dd }
  const partner = { emoji: '🐻', name: 'Partner',        color: 0xd4537e }

  return (
    <div className="world-wrap">
      <div className="world-header">
        <div className="world-title">Our World 💜</div>
        <button className="logout-btn" onClick={onLogout}>Log out</button>
      </div>
      <div className="world-canvas-wrap">
        <RoomBanner />
        <PhaserGame me={me} partner={partner} />
      </div>
      <FloatingButtons />
      <OurFeelingOverlay />
      <OurTimeOverlay />
      <RoomModal />
    </div>
  )
}

// ── Pairing screen ────────────────────────────────────────────
function PairingScreen({
  onPaired,
  onLogout,
}: {
  onPaired: () => void
  onLogout: () => void
}) {
  const [code, setCode]       = useState('')
  const [myCode, setMyCode]   = useState<string | null>(null)
  const [error, setError]     = useState<string | null>(null)
  const [loading, setLoading] = useState(false)
  const pollRef = useRef<ReturnType<typeof setInterval> | null>(null)

  // Poll every 3s after generating code — detect when partner joins
  useEffect(() => {
    if (!myCode) return
    pollRef.current = setInterval(async () => {
      const coupleId = await fetchCoupleStatus()
      if (coupleId) {
        clearInterval(pollRef.current!)
        onPaired()
      }
    }, 3000)
    return () => { if (pollRef.current) clearInterval(pollRef.current) }
  }, [myCode, onPaired])

  const generateCode = async () => {
    setLoading(true)
    setError(null)
    try {
      const token = localStorage.getItem('accessToken')
      const res = await fetch(`${COUPLE_URL}/api/couples/invite`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      })
      if (!res.ok) {
        const data = await res.json().catch(() => ({}))
        throw new Error(data.message || `Error ${res.status}`)
      }
      const data = await res.json()
      setMyCode(data.inviteCode)
    } catch (e: any) {
      setError(e.message || 'Failed to generate code')
    } finally {
      setLoading(false)
    }
  }

  const join = async () => {
    setLoading(true)
    setError(null)
    try {
      const token = localStorage.getItem('accessToken')
      const res = await fetch(`${COUPLE_URL}/api/couples/join`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ inviteCode: code.toUpperCase() }),
      })
      if (!res.ok) {
        const data = await res.json().catch(() => ({}))
        throw new Error(data.message || `Error ${res.status}`)
      }
      // Refresh token then go to world
      await doTokenRefresh()
      const coupleId = await fetchCoupleStatus()
      if (coupleId) {
        onPaired()
      } else {
        setError('Joined but could not confirm couple. Please log out and back in.')
      }
    } catch (e: any) {
      setError(e.message || 'Failed to join')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-wrap">
      <div className="login-card">
        <div className="login-logo">💕</div>
        <h1 className="login-title">Connect with your partner</h1>
        <p className="login-subtitle">Share a code or enter theirs to pair up</p>

        {myCode ? (
          <div className="pairing-code-display">
            <div className="pairing-code">{myCode}</div>
            <div className="pairing-hint">Share this with your partner</div>
            <div style={{ marginTop: 8, fontSize: 11, color: '#b4b2a9' }}>
              Waiting for them to join...
            </div>
          </div>
        ) : (
          <button className="overlay-primary-btn" onClick={generateCode} disabled={loading}>
            {loading ? 'Generating...' : 'Generate my invite code'}
          </button>
        )}

        <div className="overlay-divider" />

        <div className="login-form">
          <input
            className="overlay-input"
            placeholder="Enter partner's code"
            value={code}
            onChange={(e) => setCode(e.target.value.toUpperCase())}
            maxLength={6}
          />
          {error && <div className="login-error">{error}</div>}
          <button
            className="overlay-primary-btn"
            onClick={join}
            disabled={loading || code.length !== 6}
          >
            {loading ? 'Connecting...' : 'Connect'}
          </button>
        </div>

        <button className="login-switch" onClick={onLogout} style={{ marginTop: 20 }}>
          Log out
        </button>
      </div>
    </div>
  )
}
