import { create } from 'zustand'

export type RoomId =
  | 'dating' | 'diary' | 'calendar' | 'games'
  | 'character' | 'gift' | 'setlog' | 'garden' | null

interface PartnerState {
  online: boolean
  location: string
  x: number
  y: number
}

interface GameStore {
  activeRoom: RoomId
  setActiveRoom: (room: RoomId) => void

  feelingOpen: boolean
  timeOpen: boolean
  toggleFeeling: () => void
  toggleTime: () => void
  closeOverlays: () => void

  partner: PartnerState
  setPartner: (p: Partial<PartnerState>) => void

  myPosition: { x: number; y: number }
  setMyPosition: (x: number, y: number) => void
}

export const useGameStore = create<GameStore>((set) => ({
  activeRoom: null,
  setActiveRoom: (room) => set({ activeRoom: room }),

  feelingOpen: false,
  timeOpen: false,
  toggleFeeling: () => set((s) => ({ feelingOpen: !s.feelingOpen, timeOpen: false })),
  toggleTime: () => set((s) => ({ timeOpen: !s.timeOpen, feelingOpen: false })),
  closeOverlays: () => set({ feelingOpen: false, timeOpen: false }),

  partner: { online: false, location: '', x: 0, y: 0 },
  setPartner: (p) => set((s) => ({ partner: { ...s.partner, ...p } })),

  myPosition: { x: 0, y: 0 },
  setMyPosition: (x, y) => set({ myPosition: { x, y } }),
}))
