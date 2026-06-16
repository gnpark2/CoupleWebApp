import { useEffect, useRef } from 'react'
import Phaser from 'phaser'
import { WorldScene, PlayerCharacter } from './scenes/WorldScene'
import { WORLD_WIDTH, WORLD_HEIGHT } from './rooms'
import { useGameStore } from '../store/useGameStore'

interface Props {
  me: PlayerCharacter
  partner: PlayerCharacter
}

export function PhaserGame({ me, partner }: Props) {
  const containerRef = useRef<HTMLDivElement>(null)
  const gameRef = useRef<Phaser.Game | null>(null)
  const sceneRef = useRef<WorldScene | null>(null)

  const setActiveRoom = useGameStore((s) => s.setActiveRoom)
  const setMyPosition = useGameStore((s) => s.setMyPosition)
  const partnerState = useGameStore((s) => s.partner)

  useEffect(() => {
    if (!containerRef.current) return

    const scene = new WorldScene()
    sceneRef.current = scene

    const game = new Phaser.Game({
      type: Phaser.AUTO,
      width: WORLD_WIDTH,
      height: WORLD_HEIGHT,
      parent: containerRef.current,
      backgroundColor: '#f5f3ee',
      scene: scene,
      physics: { default: 'arcade' },
    })

    game.scene.start('world', {
      me,
      partner,
      onRoomEnter: (roomId: string | null) => setActiveRoom(roomId as any),
      onPositionChange: (x: number, y: number) => setMyPosition(x, y),
    })

    gameRef.current = game

    return () => {
      game.destroy(true)
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  // Reflect partner online/offline + position into the scene
  useEffect(() => {
    const scene = sceneRef.current
    if (!scene) return
    scene.setPartnerOnline(partnerState.online)
    if (partnerState.x && partnerState.y) {
      scene.setPartnerPosition(partnerState.x, partnerState.y)
    }
  }, [partnerState])

  return (
    <div
      ref={containerRef}
      style={{
        width: WORLD_WIDTH,
        height: WORLD_HEIGHT,
        borderRadius: 16,
        overflow: 'hidden',
        boxShadow: '0 1px 3px rgba(0,0,0,0.08)',
      }}
    />
  )
}
