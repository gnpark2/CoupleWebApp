import { useEffect, useState } from 'react'
import { getCharacter, createCharacter, interact, CharacterResponse } from '../../api/character'
import './rooms.css'

const ACTIONS: { key: 'feed' | 'play' | 'pat' | 'rest'; label: string; icon: string }[] = [
  { key: 'feed', label: 'Feed', icon: '🍎' },
  { key: 'play', label: 'Play', icon: '⚽' },
  { key: 'pat',  label: 'Pat',  icon: '✋' },
  { key: 'rest', label: 'Rest', icon: '😴' },
]

export function CharacterRoom() {
  const [character, setCharacter] = useState<CharacterResponse | null>(null)
  const [loading, setLoading] = useState(true)
  const [bubble, setBubble] = useState<string | null>(null)
  const [creating, setCreating] = useState(false)

  useEffect(() => {
    getCharacter().then((c) => {
      setCharacter(c)
      setLoading(false)
    })
  }, [])

  const handleCreate = async () => {
    setCreating(true)
    try {
      const c = await createCharacter('Nabi', '🐱')
      setCharacter(c)
    } finally {
      setCreating(false)
    }
  }

  const handleAction = async (action: 'feed' | 'play' | 'pat' | 'rest') => {
    const updated = await interact(action)
    setCharacter(updated)
    setBubble(messageFor(action))
    setTimeout(() => setBubble(null), 2500)
  }

  if (loading) return <div className="room-placeholder">Loading...</div>

  if (!character) {
    return (
      <div className="char-room">
        <div className="room-placeholder">
          You don't have a shared character yet.
        </div>
        <button className="overlay-primary-btn" onClick={handleCreate} disabled={creating}>
          {creating ? 'Creating...' : 'Create Nabi 🐱'}
        </button>
      </div>
    )
  }

  return (
    <div className="char-room">
      <div className="char-display">
        <div className="char-avatar">{character.avatarEmoji}</div>
        {bubble && <div className="char-bubble">{bubble}</div>}
        <div className="char-name">{character.name} · Lv. {character.level}</div>
        <div className="char-status">{character.statusMessage}</div>
      </div>

      <div className="char-xp">
        <div className="char-xp-label">
          <span>XP to Lv. {character.level + 1}</span>
          <span>{1000 - character.xpToNextLevel} / 1000</span>
        </div>
        <div className="char-xp-track">
          <div className="char-xp-fill" style={{ width: `${((1000 - character.xpToNextLevel) / 1000) * 100}%` }} />
        </div>
      </div>

      <div className="char-stats">
        <StatBar label="Happiness" value={character.happiness} color="#d4537e" />
        <StatBar label="Hunger" value={character.hunger} color="#ef9f27" />
        <StatBar label="Energy" value={character.energy} color="#1d9e75" />
      </div>

      <div className="char-actions">
        {ACTIONS.map((a) => (
          <button key={a.key} className="char-action-btn" onClick={() => handleAction(a.key)}>
            <span style={{ fontSize: 20 }}>{a.icon}</span>
            <span>{a.label}</span>
          </button>
        ))}
      </div>
    </div>
  )
}

function StatBar({ label, value, color }: { label: string; value: number; color: string }) {
  return (
    <div className="stat-row">
      <span className="stat-label">{label}</span>
      <div className="stat-track">
        <div className="stat-fill" style={{ width: `${value}%`, background: color }} />
      </div>
    </div>
  )
}

function messageFor(action: string): string {
  const msgs: Record<string, string[]> = {
    feed: ['yummy! 😋', 'thank you! 🍎', 'so full now 🐾'],
    play: ['wheee! 🎉', "let's go! ⚽", 'this is fun! ✨'],
    pat: ['purring... 😻', 'so warm 🌸', 'I love you both 💜'],
    rest: ['zzz... 😴', 'good night 🌙', 'dreaming of you 💭'],
  }
  const arr = msgs[action] || ['😊']
  return arr[Math.floor(Math.random() * arr.length)]
}
