import { NextFunction, Request, Response } from 'express';
import UserAuth from '../models/UserAuth';
import axios from 'axios';
import https from 'https';
import { resourcesOnMap } from '../models/GameState';

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
        const urlTo = `http://192.168.75.23:8080/users/user/authenticate?jwt=${jwt}&origin=${origin}`;
		const response = await axios.get(urlTo, { httpsAgent });

		if (response.data.user) {
			req.user = response.data.user as UserAuth;
            if (req.user && resourcesOnMap[req.user.login] === undefined) {
                resourcesOnMap[req.user.login] = {
                    id: req.user.login,
                    role: req.user.species,
                    position: {
                        x: 0,
                        y: 0,
                    },
                    flasks: [],
                    statistics: {
                        flasksGathered: 0,
                        piratesTerminated: 0,
                        villagersTurned: 0,
                    },
                    nearbyResources: [],
                };
            }
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
