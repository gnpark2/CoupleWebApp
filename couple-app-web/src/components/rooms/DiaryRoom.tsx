import { useEffect, useState } from 'react'
import { diaryApi } from '../../api/client'
import './rooms.css'

interface DiaryEntry {
  id: string
  authorId: string
  entryType: string
  title: string
  content: string
  entryDate: string
  createdAt: string
}

export function DiaryRoom({ mode }: { mode: 'diary' | 'setlog' }) {
  const entryType = mode === 'diary' ? 'DIARY' : 'SETLOG'
  const [entries, setEntries] = useState<DiaryEntry[]>([])
  const [title, setTitle] = useState('')
  const [content, setContent] = useState('')
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)

  const load = () => {
    diaryApi.get<DiaryEntry[]>('/api/diary', { params: { type: entryType } })
      .then((res) => setEntries(res.data))
      .finally(() => setLoading(false))
  }

  useEffect(() => { load() }, [mode])

  const handleSubmit = async () => {
    if (!title.trim() || !content.trim()) return
    setSubmitting(true)
    try {
      await diaryApi.post('/api/diary', {
        entryType,
        title,
        content,
        entryDate: new Date().toISOString().slice(0, 10),
      })
      setTitle('')
      setContent('')
      load()
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="diary-room">
      <div className="diary-form">
        <input
          className="overlay-input"
          placeholder={mode === 'diary' ? 'Title for today...' : 'What happened today?'}
          value={title}
          onChange={(e) => setTitle(e.target.value)}
        />
        <textarea
          className="overlay-input diary-textarea"
          placeholder="Write something for your partner..."
          value={content}
          onChange={(e) => setContent(e.target.value)}
          rows={3}
        />
        <button className="overlay-primary-btn" onClick={handleSubmit} disabled={submitting}>
          {submitting ? 'Saving...' : mode === 'diary' ? 'Save diary entry' : 'Save setlog'}
        </button>
      </div>

      <div className="overlay-divider" />

      {loading ? (
        <div className="room-placeholder">Loading...</div>
      ) : entries.length === 0 ? (
        <div className="room-placeholder">No entries yet. Be the first to write one!</div>
      ) : (
        <div className="diary-list">
          {entries.map((e) => (
            <div key={e.id} className="diary-entry">
              <div className="diary-entry-header">
                <span className="diary-entry-date">{e.entryDate}</span>
              </div>
              <div className="diary-entry-title">{e.title}</div>
              <div className="diary-entry-content">{e.content}</div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
