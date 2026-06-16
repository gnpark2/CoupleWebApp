import { useGameStore } from '../store/useGameStore'
import { ROOMS } from '../game/rooms'
import './RoomBanner.css'

export function RoomBanner() {
  const activeRoom = useGameStore((s) => s.activeRoom)
  if (!activeRoom) return null

  const room = ROOMS.find((r) => r.id === activeRoom)
  if (!room) return null

  return (
    <div className="room-banner">
      <span className="room-banner-icon">{room.icon}</span>
      <div>
        <div className="room-banner-name">{room.name}</div>
        <div className="room-banner-hint">Press Enter to interact</div>
      </div>
    </div>
  )
}
