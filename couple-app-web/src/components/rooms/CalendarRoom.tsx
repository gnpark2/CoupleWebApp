import { useEffect, useState } from 'react'
import { getAnniversary, getUpcomingEvents, AnniversaryResponse, CalendarEventResponse } from '../../api/calendar'
import './rooms.css'

export function CalendarRoom() {
  const [anniversary, setAnniversary] = useState<AnniversaryResponse | null>(null)
  const [events, setEvents] = useState<CalendarEventResponse[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    Promise.all([getAnniversary(), getUpcomingEvents()])
      .then(([ann, evs]) => {
        setAnniversary(ann)
        setEvents(evs)
      })
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <div className="room-placeholder">Loading...</div>

  return (
    <div className="calendar-room">
      {anniversary && (
        <div className="anniversary-card">
          <div className="anniversary-icon">💜</div>
          <div>
            <div className="anniversary-title">{anniversary.daysTogether} days together</div>
            <div className="anniversary-sub">
              Next anniversary in {anniversary.daysUntilNextAnniversary} days
            </div>
          </div>
        </div>
      )}

      <div className="overlay-section-label" style={{ marginTop: 16 }}>Upcoming</div>
      {events.length === 0 ? (
        <div className="room-placeholder">No upcoming events yet.</div>
      ) : (
        <div className="event-list">
          {events.map((e) => (
            <div key={e.id} className="event-row">
              <div className="event-info">
                <div className="event-title">{e.title}</div>
                <div className="event-date">{e.eventDate}</div>
              </div>
              <div className="event-days">{e.daysUntil}d</div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
