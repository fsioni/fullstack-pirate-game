import { NextFunction, Request, Response } from 'express';

const corsMiddleware = (req: Request, res: Response, next: NextFunction) => {
	res.header('Access-Control-Allow-Origin', 'http://localhost:5173'); // Assure-toi d'inclure http://
	res.header(
		'Access-Control-Allow-Headers',
		'Origin, X-Requested-With, Content-Type, Accept, Authorization',
	);
	res.header('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');

	if (req.method === 'OPTIONS') {
		res.status(200).end();
	} else {
		next();
	}
};

export default corsMiddleware;
