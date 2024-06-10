import { GameResource } from './GameResource';
import { Zrr } from './zrr';

const resourcesOnMap: { [key: string]: GameResource } = {
	player1: {
		id: 'player1',
		role: 'VILLAGEOIS',
		position: [0, 0],
		flasks: [],
		statistics: {
			flasksGathered: 5,
			piratesTerminated: 1,
			villagersTurned: 0,
		},
	},
	villageois2: {
		id: 'villageois2',
		role: 'VILLAGEOIS',
		position: [0, 1],
		flasks: [],
		statistics: {
			flasksGathered: 2,
			piratesTerminated: 1,
			villagersTurned: 0,
		},
	},
	'2': {
		id: '2',
		role: 'PIRATE',
		position: [0, 1],
		flasks: [],
		statistics: {
			flasksGathered: 0,
			piratesTerminated: 0,
			villagersTurned: 2,
		},
	},
	'3': {
		id: '3',
		role: 'FLASK',
		position: [1, 0],
		TTL: 10,
	},
};

let zrr: Zrr | null = null;

function setZrr(newZrr: Zrr) {
	zrr = newZrr;
}

let ttl = 60;

function setTtl(newTtl: number) {
	ttl = newTtl;
}

export { resourcesOnMap, zrr, setZrr, ttl, setTtl };
