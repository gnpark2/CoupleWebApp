import { useGameStore } from '../store/useGameStore'
import './FloatingButtons.css'

export function FloatingButtons() {
  const toggleFeeling = useGameStore((s) => s.toggleFeeling)
  const toggleTime = useGameStore((s) => s.toggleTime)

  return (
    <div className="floating-buttons">
      <button className="fab" onClick={toggleTime} aria-label="Our time">
        <i className="ti ti-clock" />
        <span className="fab-label">our time</span>
      </button>
      <button className="fab fab-feeling" onClick={toggleFeeling} aria-label="Our feeling">
        <i className="ti ti-heart" />
        <span className="fab-label">our feeling</span>
      </button>
    </div>
  )
}
