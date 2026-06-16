import { useEffect, useState } from 'react'
import { useGameStore } from '../store/useGameStore'
import { shareFeeling, getTodayFeelings, FeelingResponse } from '../api/feeling'
import './Overlay.css'

const MOODS = [
  { emoji: '🥰', label: 'In love' },
  { emoji: '😊', label: 'Happy' },
  { emoji: '😌', label: 'Calm' },
  { emoji: '🥺', label: 'Missing you' },
  { emoji: '😔', label: 'Sad' },
  { emoji: '😤', label: 'Stressed' },
  { emoji: '🤒', label: 'Tired' },
  { emoji: '😴', label: 'Sleepy' },
]

export function OurFeelingOverlay() {
  const open = useGameStore((s) => s.feelingOpen)
  const close = useGameStore((s) => s.closeOverlays)

  const [selected, setSelected] = useState(MOODS[0])
  const [comment, setComment] = useState('')
  const [partnerFeeling, setPartnerFeeling] = useState<FeelingResponse | null>(null)
  const [loading, setLoading] = useState(false)
  const [sent, setSent] = useState(false)

  useEffect(() => {
    if (!open) return
    setSent(false)
    getTodayFeelings()
      .then((res) => setPartnerFeeling(res.partnerFeeling))
      .catch(() => setPartnerFeeling(null))
  }, [open])

  if (!open) return null

  const handleShare = async () => {
    setLoading(true)
    try {
      await shareFeeling(selected.emoji, selected.label, comment || undefined)
      setSent(true)
    } finally {
      setLoading(false)
    }
  }

  const isFresh = (createdAt: string) => {
    const hours = (Date.now() - new Date(createdAt).getTime()) / 36e5
    return hours < 24
  }

  return (
    <div className="overlay-backdrop" onClick={close}>
      <div className="overlay-sheet" onClick={(e) => e.stopPropagation()}>
        <div className="overlay-handle" />
        <h2 className="overlay-title">Our feeling</h2>

        <div className="overlay-section-label">My feeling right now</div>
        <div className="mood-grid">
          {MOODS.map((m) => (
            <button
              key={m.label}
              className={`mood-opt ${selected.label === m.label ? 'selected' : ''}`}
              onClick={() => setSelected(m)}
            >
              <span className="mood-emoji">{m.emoji}</span>
              <span className="mood-label">{m.label}</span>
            </button>
          ))}
        </div>

        <input
          className="overlay-input"
          type="text"
          placeholder="Add a little note for your partner..."
          value={comment}
          onChange={(e) => setComment(e.target.value)}
          maxLength={200}
        />

        <button className="overlay-primary-btn" onClick={handleShare} disabled={loading}>
          {sent ? 'Shared! 💜' : loading ? 'Sharing...' : 'Share my feeling'}
        </button>

        <div className="overlay-divider" />

        <div className="overlay-section-label">Partner's feeling</div>
        {partnerFeeling && isFresh(partnerFeeling.createdAt) ? (
          <div className="partner-feeling-card">
            <span className="partner-feeling-emoji">{partnerFeeling.moodEmoji}</span>
            <div>
              <div className="partner-feeling-label">{partnerFeeling.moodLabel}</div>
              {partnerFeeling.comment && (
                <div className="partner-feeling-comment">"{partnerFeeling.comment}"</div>
              )}
              <div className="partner-feeling-time">
                {timeAgo(partnerFeeling.createdAt)}
              </div>
            </div>
          </div>
        ) : (
          <div className="partner-feeling-empty">
            Your partner hasn't shared yet today.<br />
            Maybe they're still dreaming of you. 🌙
          </div>
        )}
      </div>
    </div>
  )
}

function timeAgo(iso: string): string {
  const diffMs = Date.now() - new Date(iso).getTime()
  const hours = Math.floor(diffMs / 36e5)
  if (hours < 1) return 'just now'
  if (hours === 1) return '1 hour ago'
  return `${hours} hours ago`
}
