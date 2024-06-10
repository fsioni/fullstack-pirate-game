import { RequestWithUser } from './getUserMiddleware';
import { NextFunction, Response } from 'express';

const adminOnlyMiddleware = (
	req: RequestWithUser,
	res: Response,
	next: NextFunction,
) => {
	if (req.user && req.user.species === 'ADMIN') {
		next();
	} else {
		res.status(403).json({
			error: 'Forbidden',
			message: 'Access is restricted to administrators only.',
		});
	}
};

export default adminOnlyMiddleware;
