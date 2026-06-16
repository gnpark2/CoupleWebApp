import { useEffect, useRef } from 'react'
import { Client, IMessage } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { REALTIME_WS_URL } from '../api/client'
import { getCoupleId } from '../api/auth'
import { useGameStore } from '../store/useGameStore'

interface FeelingEvent {
  coupleId: string
  userId: string
  moodEmoji: string
  moodLabel: string
  comment?: string
  sharedAt: string
}

interface PresenceEvent {
  userId: string
  online: boolean
  location: string
  timestamp: string
}

interface CharacterXpEvent {
  coupleId: string
  triggeredByUserId: string
  interactionType: string
  xpGained: number
}

type Listeners = {
  onFeeling?: (e: FeelingEvent) => void
  onPresence?: (e: PresenceEvent) => void
  onCharacter?: (e: CharacterXpEvent) => void
}

/**
 * Connects to realtime-service over STOMP/SockJS.
 * Subscribes to the couple's feeling, presence, and character topics.
 * Sends periodic heartbeats with current location for presence tracking.
 */
export function useRealtime(listeners: Listeners, currentLocation: string) {
  const clientRef = useRef<Client | null>(null)
  const setPartner = useGameStore((s) => s.setPartner)

  useEffect(() => {
    const coupleId = getCoupleId()
    const token = localStorage.getItem('accessToken')
    if (!token) return

    const client = new Client({
      webSocketFactory: () => new SockJS(REALTIME_WS_URL) as any,
      connectHeaders: { Authorization: `Bearer ${token}` },
      reconnectDelay: 4000,
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
    })

    client.onConnect = () => {
      if (coupleId) {
        client.subscribe(`/topic/couple.${coupleId}.feeling`, (msg: IMessage) => {
          const event: FeelingEvent = JSON.parse(msg.body)
          listeners.onFeeling?.(event)
        })

        client.subscribe(`/topic/couple.${coupleId}.presence`, (msg: IMessage) => {
          const event: PresenceEvent = JSON.parse(msg.body)
          setPartner({ online: event.online, location: event.location })
          listeners.onPresence?.(event)
        })

        client.subscribe(`/topic/couple.${coupleId}.character`, (msg: IMessage) => {
          const event: CharacterXpEvent = JSON.parse(msg.body)
          listeners.onCharacter?.(event)
        })
      }

      // Send heartbeat every 30s with current location
      const heartbeat = () => {
        client.publish({
          destination: '/app/presence.heartbeat',
          body: JSON.stringify({ location: currentLocation }),
        })
      }
      heartbeat()
      const interval = setInterval(heartbeat, 30000)
      ;(client as any)._heartbeatInterval = interval
    }

    client.activate()
    clientRef.current = client

    return () => {
      if ((client as any)._heartbeatInterval) {
        clearInterval((client as any)._heartbeatInterval)
      }
      client.deactivate()
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentLocation])

  return clientRef
}
