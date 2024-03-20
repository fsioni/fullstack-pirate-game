import express from 'express';
import { ZrrRequestParams, TtlRequestParams } from './interfaces';

const router = express.Router();

router.post('/zrr', (req, res) => {
	const params: ZrrRequestParams = req.body;
	res.send('Zrr boundaries' + params);
});

router.post('/ttl', (req, res) => {
	const params: TtlRequestParams = req.body;
	res.send('Ttl updated' + params);
});

router.post('/potion', (req, res) => {
	res.send('Potion added');
});

router;

export default router;
