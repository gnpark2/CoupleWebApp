# Our World — Web Metaverse

A 2D web metaverse for couples in long-distance relationships, built with
React + TypeScript + Phaser 3.

## Features

- **2D world map** with 8 rooms: dating room, diary, calendar, games,
  setlog, character room, gift shop, and garden
- **Proximity-based room entry** — walk near a room and press Enter to open it
- **"Our feeling"** floating button — share your mood, see your partner's
  (with a 24h freshness window)
- **"Our time"** floating button — dual clock showing both timezones
- **Our character (Nabi)** — feed, play, pat, rest; shared XP and stats
- **Diary & Setlog rooms** — write and read entries
- **Calendar room** — anniversary countdown, upcoming events
- **Gift shop** — AI-powered gift recommendations
- **Real-time sync** via WebSocket (STOMP/SockJS) — partner presence,
  feeling updates, and character XP push instantly

## Setup

```bash
npm install
cp .env.example .env
npm run dev
```

The dev server runs on http://localhost:5173.

## Backend requirement

This frontend expects the backend services from `couple-app-backend` to be
running locally via Docker Compose (ports 8081-8090). Update `.env` if your
ports differ.

## First run

1. Register two accounts (one per partner) on the login screen
2. After login, one partner generates an invite code (Pairing screen)
3. The other partner enters that code to link the couple
4. Once paired, you'll land in "Our World" — walk around with arrow keys
   or WASD
5. Walk into the Character room and create "Nabi" to start raising your
   shared character

## Controls

- **Arrow keys / WASD** — move your character
- **Enter** — interact with the room you're standing in
- **Escape** — close room modal
