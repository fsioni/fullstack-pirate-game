import express from 'express';
import { Position, ResourceRequestParam } from './interfaces';

const router = express.Router();

router.get('/', (req, res) => {
	res.send('Game resources');
});

router.post('/:resourceId', (req, res) => {
	const params: ResourceRequestParam = req.params as ResourceRequestParam;
	res.send('Game resource created with id: ' + params.resourceId);
});

router.put('/:resourceId/position', (req, res) => {
	const params: ResourceRequestParam = req.params as ResourceRequestParam;
	const position: Position = req.body.position;

	res.send(
		'Game resource position updated with id: ' +
			params.resourceId +
			' to position: ' +
			position,
	);
});

export default router;
