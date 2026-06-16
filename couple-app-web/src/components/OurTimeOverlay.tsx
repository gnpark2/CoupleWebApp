import { useEffect, useState } from 'react'
import { useGameStore } from '../store/useGameStore'
import './Overlay.css'

// Could be made dynamic via user-service profile (timezone/city)
const MY_TZ = 'Asia/Seoul'
const MY_CITY = 'Seoul'
const PARTNER_TZ = 'Europe/Berlin'
const PARTNER_CITY = 'Berlin'

export function OurTimeOverlay() {
  const open = useGameStore((s) => s.timeOpen)
  const close = useGameStore((s) => s.closeOverlays)
  const [now, setNow] = useState(new Date())

  useEffect(() => {
    if (!open) return
    const interval = setInterval(() => setNow(new Date()), 1000)
    return () => clearInterval(interval)
  }, [open])

  if (!open) return null

  const myTime = now.toLocaleTimeString('en-US', { timeZone: MY_TZ, hour: '2-digit', minute: '2-digit', hour12: false })
  const partnerTime = now.toLocaleTimeString('en-US', { timeZone: PARTNER_TZ, hour: '2-digit', minute: '2-digit', hour12: false })

  const myHour = parseInt(now.toLocaleString('en-US', { timeZone: MY_TZ, hour: 'numeric', hour12: false }))
  const partnerHour = parseInt(now.toLocaleString('en-US', { timeZone: PARTNER_TZ, hour: 'numeric', hour12: false }))

  let diff = myHour - partnerHour
  if (diff > 12) diff -= 24
  if (diff < -12) diff += 24

  const diffLabel = diff === 0
    ? 'Same time as you'
    : diff > 0
      ? `Partner is ${diff}h behind you`
      : `Partner is ${Math.abs(diff)}h ahead of you`

  const partnerHint =
    partnerHour < 6 ? 'probably asleep' :
    partnerHour < 9 ? 'just waking up' :
    partnerHour < 22 ? 'awake' : 'late night'

  return (
    <div className="overlay-backdrop" onClick={close}>
      <div className="overlay-sheet" onClick={(e) => e.stopPropagation()}>
        <div className="overlay-handle" />
        <h2 className="overlay-title">Our time</h2>

        <div className="clock-grid">
          <div className="clock-card">
            <div className="clock-city">{MY_CITY} · me</div>
            <div className="clock-time">{myTime}</div>
            <div className="clock-weather">☀️ 24°C</div>
          </div>
          <div className="clock-card">
            <div className="clock-city">{PARTNER_CITY} · partner</div>
            <div className="clock-time">{partnerTime}</div>
            <div className="clock-weather">🌙 12°C</div>
          </div>
        </div>

        <div className="clock-diff">
          {diffLabel} · {partnerHint}
        </div>
      </div>
    </div>
  )
}
