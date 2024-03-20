import express from 'express';
import {
	ResourceRequestParam,
	PlayerRequestParam,
	Position,
	GameRessource,
} from './interfaces';

const router = express.Router();

const geolocalizedResources: { [key: string]: GameRessource } = {};

// Get all game resources positions (only who need to be showed)
router.get('/resources', (req, res) => {
	res.json(geolocalizedResources);
});

// Get zrr boundaries
router.get('/zrr', (req, res) => {
	res.send('Game zrr');
});

// Take a ressource
router.post('/resources/:resourceId', (req, res) => {
	const params: ResourceRequestParam = req.params as ResourceRequestParam;
	res.send('Game resource with id: ' + params.resourceId + ' taken');
});

// Update the position of a player
router.put('/resources/:playerLogin/position', (req, res) => {
	const params: PlayerRequestParam = req.params as PlayerRequestParam;
	const position: Position = req.body.position;

	if (geolocalizedResources[params.playerLogin]) {
		geolocalizedResources[params.playerLogin].position = position;
	} else {
		geolocalizedResources[params.playerLogin] = {
			id: params.playerLogin,
			type: 'player',
			position: position,
		};
	}

	res.json(geolocalizedResources[params.playerLogin]);
});

export default router;
