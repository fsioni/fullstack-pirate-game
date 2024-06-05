import type { GameResource } from '@/models/GameResource'
import type { Zrr } from '@/models/Zrr'

const lat = 45.782
const lng = 4.8656

const zrr: Zrr = [
  [lat - 0.001, lng - 0.001],
  [lat + 0.002, lng + 0.002]
]

// Générer des positions aléatoires dans la zone
function getRandomPositionInZrr(zrr: Zrr): [number, number] {
  const minLat = Math.min(zrr[0][0], zrr[1][0])
  const maxLat = Math.max(zrr[0][0], zrr[1][0])
  const minLng = Math.min(zrr[0][1], zrr[1][1])
  const maxLng = Math.max(zrr[0][1], zrr[1][1])
  const randomLat = Math.random() * (maxLat - minLat) + minLat
  const randomLng = Math.random() * (maxLng - minLng) + minLng
  return [randomLat, randomLng]
}

const players: GameResource[] = [
  {
    id: '1',
    position: getRandomPositionInZrr(zrr),
    role: 'VILLAGEOIS',
    flasks: [],
    statistics: { flasksGathered: 2, piratesTerminated: 1, villagersTurned: 0 }
  },
  {
    id: '2',
    position: getRandomPositionInZrr(zrr),
    role: 'PIRATE',
    flasks: [],
    statistics: { flasksGathered: 0, piratesTerminated: 0, villagersTurned: 3 }
  },
  {
    id: '3',
    position: getRandomPositionInZrr(zrr),
    role: 'VILLAGEOIS',
    flasks: [],
    statistics: { flasksGathered: 1, piratesTerminated: 0, villagersTurned: 0 }
  }
]

const flasks: GameResource[] = [
  {
    id: '1',
    position: getRandomPositionInZrr(zrr),
    role: 'FLASK',
    TTL: 300
  },
  {
    id: '2',
    position: getRandomPositionInZrr(zrr),
    role: 'FLASK',
    TTL: 300
  }
]

const localPlayer: GameResource = {
  id: 'local',
  position: getRandomPositionInZrr(zrr),
  role: 'VILLAGEOIS',
  flasks: [],
  statistics: { flasksGathered: 3, piratesTerminated: 2, villagersTurned: 1 }
}

export { players, flasks, localPlayer, zrr }
