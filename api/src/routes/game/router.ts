import express from 'express';
import { PlayerRequestParam, ResourceOperationRequest } from './interfaces';
import { GameResource, Position } from '../../models/GameResource';
import {
	createNewPlayerAtPosition,
	getPlayerByLogin,
	getRessources,
	grabPotionFlask,
	terminatePirate,
	turnVillagerIntoPirate,
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
router.post('/resources/:resourceId', (req: ResourceOperationRequest, res) => {
	const resourceId: string = req.params.resourceId;
	const resource = resourcesOnMap[resourceId];
	if (!resource) {
		res.status(404).json({
			error: 'Not found',
			message: 'Resource not found',
		});
		return;
	}
	if (!req.user) {
		res.status(401).json({
			error: 'Unauthorized',
			message: 'Authentication failed.',
		});
		return;
	}

	const initiator = getPlayerByLogin(req.user.login, resourcesOnMap);
	if (!initiator || initiator.role === 'FLASK') {
		res.status(403).json({
			error: 'Forbidden',
			message: 'Only players can take resources',
		});
		return;
	}

	/*const isNear = isNearby(initiator.position, resource.position);*/
	// TODO: check if the player is near the resource
	const isNear = true;

	if (!isNear) {
		res.status(403).json({
			error: 'Forbidden',
			message: 'Resource is too far',
		});
		return;
	}

	const operationType = req.body.operationType;

	switch (operationType) {
	case 'grab potion flask':
		grabPotionFlask(initiator, resource, resourcesOnMap);
		break;
	case 'turn villager into pirate':
		turnVillagerIntoPirate(initiator, resource);
		break;
	case 'terminate pirate':
		terminatePirate(initiator, resource, resourcesOnMap);
		break;
	default:
		res.status(400).json({
			error: 'Invalid operation',
			message:
					'Invalid operation type or resource is not operable by user at this moment',
		});
		return;
	}

	res.status(204).json();
	return;
});

// Update the position of a player
router.put('/resources/:playerLogin/position', (req, res) => {
	const params: PlayerRequestParam = req.params as PlayerRequestParam;
	const position: Position = req.body.position;
	if (!position) {
		res.status(400).json({
			error: 'Bad request',
			message: 'Position is required',
		});
		return;
	}

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
