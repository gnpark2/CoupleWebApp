import { characterApi } from './client'

export interface CharacterResponse {
  id: string
  coupleId: string
  name: string
  xp: number
  level: number
  xpToNextLevel: number
  happiness: number
  hunger: number
  energy: number
  avatarEmoji: string
  theme: string
  statusMessage: string
  updatedAt: string
}

export async function getCharacter(): Promise<CharacterResponse | null> {
  try {
    const { data } = await characterApi.get<CharacterResponse>('/api/characters')
    return data
  } catch {
    return null
  }
}

export async function createCharacter(name: string, avatarEmoji = '🐱'): Promise<CharacterResponse> {
  const { data } = await characterApi.post<CharacterResponse>('/api/characters', { name, avatarEmoji })
  return data
}

export async function interact(action: 'feed' | 'play' | 'pat' | 'rest'): Promise<CharacterResponse> {
  const { data } = await characterApi.post<CharacterResponse>('/api/characters/interact', { action })
  return data
}
