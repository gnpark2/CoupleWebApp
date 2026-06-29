import Phaser from 'phaser'
import { ROOMS, WORLD_WIDTH, WORLD_HEIGHT, PROXIMITY_RADIUS, RoomDef } from '../rooms'

export interface PlayerCharacter {
  emoji: string
  name: string
  color: number
}

interface WorldSceneConfig {
  me: PlayerCharacter
  partner: PlayerCharacter
  onRoomEnter: (roomId: string | null) => void
  onPositionChange: (x: number, y: number) => void
}

export class WorldScene extends Phaser.Scene {
  private cfg!: WorldSceneConfig
  private myChar!: Phaser.GameObjects.Container
  private partnerChar!: Phaser.GameObjects.Container
  private cursors!: Phaser.Types.Input.Keyboard.CursorKeys
  private wasd!: { [key: string]: Phaser.Input.Keyboard.Key }
  private currentRoom: string | null = null
  private speed = 180
  private partnerTarget = { x: 700, y: 500 }
  private roomHighlights: Phaser.GameObjects.Graphics[] = []

  constructor() { super('world') }

  init(config: WorldSceneConfig) { this.cfg = config }

  create() {
    this.cameras.main.setBackgroundColor('#f5f3ee')

    // Draw paths / corridors
    const paths = this.add.graphics()
    paths.fillStyle(0xe8e4db, 1)
    paths.fillRect(0, 260, WORLD_WIDTH, 28)   // horizontal corridor
    paths.fillRect(258, 0, 28, WORLD_HEIGHT)  // vertical corridor

    // Draw rooms
    ROOMS.forEach((room) => this.drawRoom(room))

    // World border
    const border = this.add.graphics()
    border.lineStyle(1.5, 0xd0ccc4, 1)
    border.strokeRect(1, 1, WORLD_WIDTH - 2, WORLD_HEIGHT - 2)

    // My character
    this.myChar = this.createCharacter(380, 300, this.cfg.me, true)

    // Partner character
    this.partnerChar = this.createCharacter(700, 500, this.cfg.partner, false)

    // Input
    this.cursors = this.input.keyboard!.createCursorKeys()
    this.wasd = {
      W: this.input.keyboard!.addKey(Phaser.Input.Keyboard.KeyCodes.W),
      A: this.input.keyboard!.addKey(Phaser.Input.Keyboard.KeyCodes.A),
      S: this.input.keyboard!.addKey(Phaser.Input.Keyboard.KeyCodes.S),
      D: this.input.keyboard!.addKey(Phaser.Input.Keyboard.KeyCodes.D),
    }

    // Click anywhere on the game to give it keyboard focus
    this.input.on('pointerdown', () => {
      this.input.keyboard!.enabled = true
    })
  }

  update(_time: number, delta: number) {
    const dt = delta / 1000
    let vx = 0
    let vy = 0

    if (this.cursors.left.isDown  || this.wasd.A.isDown) vx -= 1
    if (this.cursors.right.isDown || this.wasd.D.isDown) vx += 1
    if (this.cursors.up.isDown    || this.wasd.W.isDown) vy -= 1
    if (this.cursors.down.isDown  || this.wasd.S.isDown) vy += 1

    if (vx !== 0 || vy !== 0) {
      const len = Math.hypot(vx, vy)
      vx = (vx / len) * this.speed * dt
      vy = (vy / len) * this.speed * dt

      const newX = Phaser.Math.Clamp(this.myChar.x + vx, 20, WORLD_WIDTH  - 20)
      const newY = Phaser.Math.Clamp(this.myChar.y + vy, 20, WORLD_HEIGHT - 20)
      this.myChar.setPosition(newX, newY)

      this.cfg.onPositionChange(Math.round(newX), Math.round(newY))
      this.checkRoomProximity(newX, newY)
    }

    // Smoothly lerp partner toward target
    if (this.partnerChar) {
      const px = Phaser.Math.Linear(this.partnerChar.x, this.partnerTarget.x, 0.08)
      const py = Phaser.Math.Linear(this.partnerChar.y, this.partnerTarget.y, 0.08)
      this.partnerChar.setPosition(px, py)
    }
  }

  setPartnerPosition(x: number, y: number) {
    this.partnerTarget = { x, y }
  }

  setPartnerOnline(online: boolean) {
    if (!this.partnerChar) return
    const sprite = this.partnerChar.getByName('body') as Phaser.GameObjects.Arc
    if (!sprite) return
    sprite.setAlpha(online ? 1 : 0.3)
  }

  // ── private ──────────────────────────────────────────────

  private drawRoom(room: RoomDef) {
    const g = this.add.graphics()
    g.fillStyle(room.color, 1)
    g.lineStyle(2, room.borderColor, 1)
    g.fillRoundedRect(room.x, room.y, room.width, room.height, 10)
    g.strokeRoundedRect(room.x, room.y, room.width, room.height, 10)

    const cx = room.x + room.width  / 2
    const cy = room.y + room.height / 2

    this.add.text(cx, cy - 14, room.icon,  { fontSize: '24px' }).setOrigin(0.5)
    this.add.text(cx, cy + 14, room.name, {
      fontSize: '11px', color: '#444441', fontFamily: 'sans-serif', fontStyle: 'bold',
    }).setOrigin(0.5)

    // Make room clickable — clicking enters the room directly
    const hitArea = this.add.zone(room.x, room.y, room.width, room.height)
      .setOrigin(0).setInteractive()
    hitArea.on('pointerdown', () => {
      this.cfg.onRoomEnter(room.id)
      // Also fire the Enter key event to open the modal
      window.dispatchEvent(new KeyboardEvent('keydown', { key: 'Enter', bubbles: true }))
    })
    hitArea.on('pointerover', () => {
      g.clear()
      g.fillStyle(room.color, 1)
      g.lineStyle(3, room.borderColor, 1)
      g.fillRoundedRect(room.x, room.y, room.width, room.height, 10)
      g.strokeRoundedRect(room.x, room.y, room.width, room.height, 10)
    })
    hitArea.on('pointerout', () => {
      g.clear()
      g.fillStyle(room.color, 1)
      g.lineStyle(2, room.borderColor, 1)
      g.fillRoundedRect(room.x, room.y, room.width, room.height, 10)
      g.strokeRoundedRect(room.x, room.y, room.width, room.height, 10)
    })
  }

  private createCharacter(x: number, y: number, char: PlayerCharacter, isMe: boolean) {
    const container = this.add.container(x, y)

    const body = this.add.circle(0, 0, 18, char.color)
    body.setStrokeStyle(2.5, 0xffffff)
    body.setName('body')

    const emoji = this.add.text(0, 0, char.emoji, { fontSize: '18px' }).setOrigin(0.5)

    const label = this.add.text(0, 30, char.name, {
      fontSize: '10px',
      color: '#ffffff',
      backgroundColor: 'rgba(0,0,0,0.6)',
      padding: { x: 5, y: 2 },
    }).setOrigin(0.5)

    container.add([body, emoji, label])

    // if (isMe) {
    //   this.tweens.add({
    //     targets: container,
    //     y: y - 5,
    //     duration: 1400,
    //     yoyo: true,
    //     repeat: -1,
    //     ease: 'Sine.easeInOut',
    //   })
    // }

    return container
  }

  private checkRoomProximity(x: number, y: number) {
    let found: string | null = null
    for (const room of ROOMS) {
      // Check if character center is inside the room rectangle (with small padding)
      const inX = x >= room.x - 30 && x <= room.x + room.width  + 30
      const inY = y >= room.y - 30 && y <= room.y + room.height + 30
      if (inX && inY) { found = room.id; break }
    }
    if (found !== this.currentRoom) {
      this.currentRoom = found
      this.cfg.onRoomEnter(found)
    }
  }
}
