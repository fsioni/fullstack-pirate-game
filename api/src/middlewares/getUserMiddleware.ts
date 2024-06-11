import { NextFunction, Request, Response } from 'express';
import UserAuth from '../models/UserAuth';
import axios from 'axios';
import https from 'https';
interface RequestWithUser extends Request {
	user?: UserAuth;
}

const httpsAgent = new https.Agent({
    rejectUnauthorized: false
});

const getUserMiddleware = async (
	req: RequestWithUser,
	res: Response,
	next: NextFunction,
) => {
	try {
		const jwt = req.headers['authorization']?.split(' ')[1];
		const origin = req.headers['origin'];

		// const authServerUrl = 'http://localhost:8080';
        // const completeUrl =  `${authServerUrl}/users/user/authenticate?jwt=${jwt}&origin=${origin}`;
        const urlTo = `https://192.168.75.23/api/users/user/authenticate?jwt=${jwt}&origin=${origin}`;
        console.log(urlTo);
		const response = await axios.get(urlTo, { httpsAgent });

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
		} else {
            console.log(error);
			res.status(401).json({
				error: 'Unauthorized',
				message: 'Error when requesting the authentication server.',
			});
		}
	}
};

export { RequestWithUser };
export default getUserMiddleware;
