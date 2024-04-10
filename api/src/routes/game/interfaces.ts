import { RequestWithUser } from '../../middlewares/getUserMiddleware';

interface ResourceRequestParam {
	resourceId: string;
}

interface PlayerRequestParam {
	playerLogin: string;
}

interface ResourceOperationRequest extends RequestWithUser {
	body: {
		operationType:
			| 'grab potion flask'
			| 'terminate pirate'
			| 'turn villager into pirate';
	};
}

export { ResourceRequestParam, PlayerRequestParam, ResourceOperationRequest };
