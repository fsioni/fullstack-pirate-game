import type { GameResource } from '@/models/GameResource'
import type { Zrr } from '@/models/Zrr'

const lat = 45.782
const lng = 4.8656

const zrr: Zrr = [
  [lat, lng],
  [lat + 0.01, lng + 0.01]
]

const players: GameResource[] = [
  {
    id: '1',
    position: [lat + 0.0001, lng + 0.0001],
    role: 'VILLAGEOIS',
    flasks: [],
    statistics: { flasksGathered: 2, piratesTerminated: 1, villagersTurned: 0 }
  },
  {
    id: '2',
    position: [lat + 0.0011, lng + 0.0011],
    role: 'PIRATE',
    flasks: [],
    statistics: { flasksGathered: 0, piratesTerminated: 0, villagersTurned: 3 }
  },
  {
    id: '3',
    position: [lat + 0.0021, lng + 0.0021],
    role: 'VILLAGEOIS',
    flasks: [],
    statistics: { flasksGathered: 1, piratesTerminated: 0, villagersTurned: 0 }
  }
]

const flasks: GameResource[] = [
  {
    id: '1',
    position: [lat + 0.0031, lng + 0.0031],
    role: 'FLASK',
    TTL: 300
  },
  {
    id: '2',
    position: [lat + 0.0041, lng + 0.0041],
    role: 'FLASK',
    TTL: 300
  }
]

const localPlayer: GameResource = {
  id: 'local',
  position: [lat + 0.0051, lng + 0.0051],
  role: 'VILLAGEOIS',
  flasks: [],
  statistics: { flasksGathered: 3, piratesTerminated: 2, villagersTurned: 1 }
}

export { players, flasks, localPlayer }
