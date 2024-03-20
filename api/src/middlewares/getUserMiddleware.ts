import { NextFunction, Request, Response } from 'express';
import UserAuth from '../models/UserAuth';
import axios from 'axios';

interface RequestWithUser extends Request {
	user?: UserAuth;
}

const getUserMiddleware = async (
	req: RequestWithUser,
	res: Response,
	next: NextFunction,
) => {
	try {
		const jwt = req.headers['authorization']?.split(' ')[1];
		const origin = req.headers['origin'];

		const authServerUrl = 'http://localhost:8080';
		const response = await axios.get(
			`${authServerUrl}/user/authenticate?jwt=${jwt}&origin=${origin}`,
		);

		if (response.data.user) {
			req.user = response.data.user;
		} else {
			res.status(401).json({
				error: 'Unauthorized',
				message: 'Authentication failed.',
			});
		}

		next();
	} catch (error) {
		if (
			axios.isAxiosError(error) &&
			error.response &&
			error.response.status === 401
		) {
			res.status(401).json({
				error: 'Unauthorized',
				message: 'Authentication failed.',
			});
		}

		res.status(401).json({
			error: 'Unauthorized',
			message: 'Error when requesting the authentication server.',
		});
	}
};

export { RequestWithUser };
export default getUserMiddleware;
