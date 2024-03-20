import express from 'express';
import { PlayerRequestParam, ResourceRequestParam } from './interfaces';
import { GameResource, Position } from '../../models/GameResource';
import {
	createNewPlayerAtPosition,
	getPlayerByLogin,
	getRessources,
} from './service';
import { RequestWithUser } from '../../middlewares/getUserMiddleware';

const router = express.Router();

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

// Get all game resources positions (only who need to be showed)
router.get('/resources', (req: RequestWithUser, res) => {
	if (!req.user) {
		res.status(401).json({
			error: 'Unauthorized',
			message: 'Authentication failed.',
		});
		return;
	}

	const player = getPlayerByLogin(req.user.login, resourcesOnMap);

	if (!player) {
		res.status(401).json({
			error: 'Unauthorized',
			message: 'Authentication failed.',
		});
		return;
	}

	const resources = getRessources(player, resourcesOnMap);

	res.json(resources);
});

// Take a ressource
router.post('/resources/:resourceId', (req, res) => {
	const params: ResourceRequestParam = req.params as ResourceRequestParam;

	const resource = resourcesOnMap[params.resourceId];
	if (resource) {
		//takeResource(initiatorId, resource, resourcesOnMap);
		res.json(resource);
	} else {
		res.status(404).send('Resource not found');
	}
});

// Update the position of a player
router.put('/resources/:playerLogin/position', (req, res) => {
	const params: PlayerRequestParam = req.params as PlayerRequestParam;
	const position: Position = req.body.position;

	if (!resourcesOnMap[params.playerLogin]) {
		createNewPlayerAtPosition(resourcesOnMap, params, position);
	} else {
		resourcesOnMap[params.playerLogin].position = position;
	}

	res.json(resourcesOnMap[params.playerLogin]);
});

// Get zrr boundaries
router.get('/zrr', (req, res) => {
	res.send('Game zrr');
});

export default router;
