import { useEffect, useState } from 'react'
import { useGameStore } from '../store/useGameStore'
import { ROOMS } from '../game/rooms'
import { CharacterRoom } from './rooms/CharacterRoom'
import { DiaryRoom } from './rooms/DiaryRoom'
import { CalendarRoom } from './rooms/CalendarRoom'
import { GiftRoom } from './rooms/GiftRoom'
import './RoomModal.css'

export function RoomModal() {
  const activeRoom = useGameStore((s) => s.activeRoom)
  const setActiveRoom = useGameStore((s) => s.setActiveRoom)
  const [open, setOpen] = useState(false)

  useEffect(() => {
    const handler = (e: KeyboardEvent) => {
      if (e.key === 'Enter' && activeRoom) setOpen(true)
      if (e.key === 'Escape') setOpen(false)
    }
    window.addEventListener('keydown', handler)
    return () => window.removeEventListener('keydown', handler)
  }, [activeRoom])

  // Close modal automatically if the player walks away from the room
  useEffect(() => {
    if (!activeRoom) setOpen(false)
  }, [activeRoom])

  if (!open || !activeRoom) return null

  const room = ROOMS.find((r) => r.id === activeRoom)
  if (!room) return null

  return (
    <div className="room-modal-backdrop" onClick={() => setOpen(false)}>
      <div className="room-modal" onClick={(e) => e.stopPropagation()}>
        <div className="room-modal-header">
          <div className="room-modal-title">
            <span style={{ fontSize: 22 }}>{room.icon}</span> {room.name}
          </div>
          <button className="room-modal-close" onClick={() => setOpen(false)}>✕</button>
        </div>
        <div className="room-modal-body">
          <RoomContent roomId={activeRoom} />
        </div>
      </div>
    </div>
  )
}

function RoomContent({ roomId }: { roomId: string }) {
  switch (roomId) {
    case 'character': return <CharacterRoom />
    case 'diary':
    case 'setlog': return <DiaryRoom mode={roomId} />
    case 'calendar': return <CalendarRoom />
    case 'gift': return <GiftRoom />
    case 'dating': return <PlaceholderRoom text="Screen share, voice, and watch-together coming soon." />
    case 'games': return <PlaceholderRoom text="Mini-games coming soon." />
    case 'garden': return <PlaceholderRoom text="A quiet place to relax together. Ambient music coming soon." />
    default: return null
  }
}

function PlaceholderRoom({ text }: { text: string }) {
  return <div className="room-placeholder">{text}</div>
}
