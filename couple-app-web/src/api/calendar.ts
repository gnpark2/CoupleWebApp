import { calendarApi } from './client'

export interface AnniversaryResponse {
  startDate: string
  daysTogether: number
  nextAnniversary: string
  daysUntilNextAnniversary: number
}

export interface CalendarEventResponse {
  id: string
  title: string
  description?: string
  eventDate: string
  eventType: string
  recurring: boolean
  daysUntil: number
}

export async function getAnniversary(): Promise<AnniversaryResponse | null> {
  try {
    const { data } = await calendarApi.get<AnniversaryResponse>('/api/calendar/anniversary')
    return data
  } catch {
    return null
  }
}

export async function getUpcomingEvents(): Promise<CalendarEventResponse[]> {
  const { data } = await calendarApi.get<CalendarEventResponse[]>('/api/calendar/events/upcoming')
  return data
}
