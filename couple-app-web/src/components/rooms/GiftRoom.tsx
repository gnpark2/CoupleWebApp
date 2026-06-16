import { useState } from 'react'
import { aiApi } from '../../api/client'
import './rooms.css'

interface AiResponse {
  feature: string
  items: string[]
  fromStub: boolean
}

export function GiftRoom() {
  const [occasion, setOccasion] = useState('just because')
  const [items, setItems] = useState<string[]>([])
  const [loading, setLoading] = useState(false)

  const fetchRecs = async () => {
    setLoading(true)
    try {
      const { data } = await aiApi.post<AiResponse>('/api/ai/recommend', {
        occasion,
        budget: 'under $50',
        interests: ['coffee', 'music'],
      })
      setItems(data.items || [])
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="gift-room">
      <div className="overlay-section-label">Occasion</div>
      <select className="overlay-input" value={occasion} onChange={(e) => setOccasion(e.target.value)}>
        <option value="just because">Just because</option>
        <option value="anniversary">Anniversary</option>
      </select>

      <button className="overlay-primary-btn" onClick={fetchRecs} disabled={loading}>
        {loading ? 'Thinking...' : 'Get AI gift ideas'}
      </button>

      {items.length > 0 && (
        <div className="gift-list">
          {items.map((item, i) => (
            <div key={i} className="gift-item">
              <span className="gift-bullet">🎁</span> {item}
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
