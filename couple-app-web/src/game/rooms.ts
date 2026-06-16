export interface RoomDef {
  id: string
  name: string
  x: number
  y: number
  width: number
  height: number
  color: number
  borderColor: number
  icon: string
}

// World is 880x600. Rooms laid out in a grid similar to the design mockup.
export const ROOMS: RoomDef[] = [
  { id: 'dating',    name: 'Dating room',   x: 30,  y: 30,  width: 220, height: 220, color: 0xfbeaf0, borderColor: 0xed93b1, icon: '📺' },
  { id: 'diary',     name: 'Diary room',    x: 280, y: 30,  width: 160, height: 100, color: 0xeeedfe, borderColor: 0xafa9ec, icon: '📓' },
  { id: 'calendar',  name: 'Calendar',      x: 460, y: 30,  width: 140, height: 100, color: 0xfaeeda, borderColor: 0xef9f27, icon: '📅' },
  { id: 'games',     name: 'Games room',    x: 280, y: 150, width: 160, height: 100, color: 0xe6f1fb, borderColor: 0x85b7eb, icon: '🎮' },
  { id: 'setlog',    name: 'Setlog',        x: 460, y: 150, width: 140, height: 100, color: 0xf1efe8, borderColor: 0xb4b2a9, icon: '🕐' },
  { id: 'character', name: 'Character room',x: 30,  y: 280, width: 140, height: 160, color: 0xe1f5ee, borderColor: 0x5dcaa5, icon: '😊' },
  { id: 'gift',      name: 'Gift shop',     x: 190, y: 280, width: 80,  height: 80,  color: 0xfaeeda, borderColor: 0xef9f27, icon: '🎁' },
  { id: 'garden',    name: 'Our garden',    x: 290, y: 280, width: 310, height: 160, color: 0xeaf3de, borderColor: 0x97c459, icon: '🌿' },
]

export const WORLD_WIDTH = 880
export const WORLD_HEIGHT = 600
export const PROXIMITY_RADIUS = 70
