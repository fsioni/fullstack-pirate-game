import { PlayerRequestParam } from './interfaces';
import { GameResource, Position } from '../../models/GameResource';

const DEFAULT_PLAYER_ROLE = 'VILLAGEOIS';

function getRessources(
	player: GameResource,
	resources: { [key: string]: GameResource },
) {
	const res = [];
	for (const r in resources) {
		//only the list of potions + only the other players of his/her team.
		if (resources[r].role === 'FLASK' || resources[r].role === player.role) {
			res.push(resources[r]);
		}
	}

	return res;
}

function getPlayerByLogin(
	login: string,
	resources: { [key: string]: GameResource },
) {
	return resources[login];
}

function createNewPlayerAtPosition(
	resources: { [key: string]: GameResource },
	params: PlayerRequestParam,
	position: Position,
) {
	resources[params.playerLogin] = {
		id: params.playerLogin,
		role: DEFAULT_PLAYER_ROLE,
		position: position,
		flasks: [],
		statistics: {
			flasksGathered: 0,
			piratesTerminated: 0,
			villagersTurned: 0,
		},
	};
}

function takeResource(
	initiatorId: string,
	resource: GameResource,
	resources: { [key: string]: GameResource },
) {
	/*const initiator = resources[initiatorId];

    if (!initiator || initiator.role === 'fiole') {
        // Si l'initiateur n'est pas un joueur ou est une fiole, annuler l'action
        return;
    }

    switch (resource.role) {
    case 'villageois':
    case 'pirate':
        // Si un villageois tente de prendre un pirate, il l'élimine
        if (initiator.role === 'villageois' && resource.role === 'pirate') {
            delete resources[resource.id]; // Éliminer le pirate
        }
        // Si un pirate tente de prendre un villageois, il le transforme en pirate
        else if (initiator.role === 'pirate' && resource.role === 'villageois') {
            resource.role = 'pirate'; // Transformer le villageois en pirate
        }
        break;
    case 'fiole':
        // Les villageois et les pirates peuvent prendre des fioles
        if (initiator.role === 'villageois' || initiator.role === 'pirate') {
            initiator.fiolesAmount = (initiator.fiolesAmount || 0) + 1; // Ajouter une fiole à l'initiateur
            delete resources[resource.id]; // Retirer la fiole des ressources
        }
        break;
    default:
        break;
    }*/
}

export {
	createNewPlayerAtPosition,
	takeResource,
	getPlayerByLogin,
	getRessources,
};
