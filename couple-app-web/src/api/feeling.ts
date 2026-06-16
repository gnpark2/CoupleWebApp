import { feelingApi } from './client'

export interface FeelingResponse {
  id: string
  userId: string
  coupleId: string
  moodEmoji: string
  moodLabel: string
  comment?: string
  createdAt: string
}

export interface LatestFeelings {
  myFeeling: FeelingResponse | null
  partnerFeeling: FeelingResponse | null
}

export async function shareFeeling(moodEmoji: string, moodLabel: string, comment?: string) {
  const { data } = await feelingApi.post<FeelingResponse>('/api/feelings', { moodEmoji, moodLabel, comment })
  return data
}

export async function getTodayFeelings(): Promise<LatestFeelings> {
  const { data } = await feelingApi.get<LatestFeelings>('/api/feelings/today')
  return data
}
