import express from 'express';
import adminOnlyMiddleware from '../../middlewares/adminOnlyMiddleware';
import { Zrr } from '../../models/zrr';
import { setTtl, setZrr, ttl, zrr, resourcesOnMap } from '../../models/GameState';
import { Position } from '../../models/GameResource';
import { getRandomPositionInZrr, createNewFlaskAtPosition } from './service';

const router = express.Router();

router.use(adminOnlyMiddleware);

router.post('/zrr', (req, res) => {
	const newZrr: Zrr = req.body.zrr;
	if (!newZrr) {
		res.status(400).json({ message: 'Zrr is required' });
		return;
	}

	setZrr(newZrr);

	res.status(204).json({ message: 'Zrr updated' });
});

router.post('/ttl', (req, res) => {
	const newTtl: number = req.body.ttl;

	if (!ttl) {
		res.status(400).send('Ttl is required');
		return;
	}

	setTtl(newTtl);

	res.status(204).json({ message: 'Ttl updated' });
});

router.post('/potion', (req, res) => {
	let position: Position = req.body.position;

	if (zrr === null) {
		res.status(400).send('Zrr is not defined yet');
		return;
	}

	if (!position) {
		position = getRandomPositionInZrr();
	}

    createNewFlaskAtPosition(resourcesOnMap, position);

	res.status(201).json({
		position,
		ttl,
	});
});

export default router;
