import { useEffect, useState } from 'react'
import { getCurrentUser, getCoupleId, logout } from './api/auth'
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

export default function App() {
  const [authed, setAuthed] = useState(getCurrentUser().isLoggedIn)
  const [hasCouple, setHasCouple] = useState<boolean | null>(null)

  useEffect(() => {
    if (authed) {
      setHasCouple(!!getCoupleId())
    }
  }, [authed])

  if (!authed) {
    return <LoginScreen onAuthenticated={() => setAuthed(true)} />
  }

  if (hasCouple === false) {
    return <PairingScreen onPaired={() => setHasCouple(true)} />
  }

  return <World />
}

function World() {
  const { nickname } = getCurrentUser()
  const activeRoom = useGameStore((s) => s.activeRoom)

  // Connect to realtime service; location reported = current room or "world"
  useRealtime(
    {
      onFeeling: () => {
        // Could trigger a toast notification here
      },
    },
    activeRoom || 'world'
  )

  const me = { emoji: '🐱', name: nickname || 'Me', color: 0x7f77dd }
  const partner = { emoji: '🐻', name: 'Partner', color: 0xd4537e }

  return (
    <div className="world-wrap">
      <div className="world-header">
        <div className="world-title">Our World</div>
        <button className="logout-btn" onClick={() => { logout(); window.location.reload() }}>
          Log out
        </button>
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

function PairingScreen({ onPaired }: { onPaired: () => void }) {
  const [code, setCode] = useState('')
  const [myCode, setMyCode] = useState<string | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  const generateCode = async () => {
    setLoading(true)
    setError(null)
    try {
      const token = localStorage.getItem('accessToken')
      const res = await fetch('http://localhost:8083/api/couples/invite', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      })
      if (!res.ok) {
        const text = await res.text()
        throw new Error(`${res.status}: ${text}`)
      }
      const data = await res.json()
      setMyCode(data.inviteCode)
    } catch (e: any) {
      setError(e?.message || 'Failed to generate code')
    } finally {
      setLoading(false)
    }
  }

  const join = async () => {
    setLoading(true)
    setError(null)
    try {
      const token = localStorage.getItem('accessToken')

      const res = await fetch('http://localhost:8083/api/couples/join', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ inviteCode: code.toUpperCase() }),
      })
      if (!res.ok) {
        const text = await res.text()
        throw new Error(`${res.status}: ${text}`)
      }

      // Refresh token to get coupleId embedded in JWT
      const refreshToken = localStorage.getItem('refreshToken')
      const refreshRes = await fetch('http://localhost:8081/api/auth/refresh', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken }),
      })
      const refreshData = await refreshRes.json()
      localStorage.setItem('accessToken', refreshData.accessToken)
      localStorage.setItem('refreshToken', refreshData.refreshToken)

      onPaired()
    } catch (e: any) {
      setError(e?.message || 'Failed to join')
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
            <div className="pairing-hint">Share this code with your partner</div>
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
      </div>
    </div>
  )
}