import { PlayerRequestParam } from './interfaces';
import { GameResource, Position, PlayerResource } from '../../models/GameResource';

const DEFAULT_PLAYER_ROLE = 'VILLAGEOIS';
const MAX_DISTANCE_TO_INTERACT = 5;

function getRessources(
	player: GameResource,
	resources: { [key: string]: GameResource },
) {
	const res = [];
	for (const r in resources) {
        let ressToAdd = resources[r];
		//only the list of potions + only the other players of his/her team.
		if (resources[r].role === 'FLASK' || resources[r].role === player.role || player.role === 'ADMIN') {
            // Get list of nearby resources of this resource if it's a player
            if (resources[r].role !== 'FLASK') {
                ressToAdd = resources[r] as PlayerResource;
                ressToAdd.nearbyResources = getNearbyResources(resources, resources[r]);
            }
			res.push(ressToAdd);
		}
	}

	return res;
}

function getNearbyResources(
    resources: { [key: string]: GameResource },
    resource: GameResource,
) {
    const nearbyResources = [];

    for (const r in resources) {
        if (resources[r].id !== resource.id && isNearby(resource.position, resources[r].position)) {
            nearbyResources.push(resources[r]);
        }
    }

    return nearbyResources;
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
        nearbyResources: [],
	};
}

function takeResource(
	initiatorId: string,
	resource: GameResource,
	//resources: { [key: string]: GameResource },
) {
	console.log(`initiatorId: ${initiatorId} resource: ${resource}`);
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

function grabPotionFlask(
	player: GameResource,
	resource: GameResource,
	resources: { [key: string]: GameResource },
) {
	if (player.role === 'VILLAGEOIS' && resource.role === 'FLASK') {
		player.flasks.push(resource);
		player.statistics.flasksGathered++;

		const flaskTTLInterval = setInterval(() => {
			resource.TTL -= 1;

			// If the flask's TTL has expired, remove it from the player's flasks
			if (resource.TTL === 0) {
				const flaskIndex = player.flasks.findIndex(
					flask => flask.id === resource.id,
				);
				if (flaskIndex > -1) {
					player.flasks.splice(flaskIndex, 1);
				}
				clearInterval(flaskTTLInterval);
			}
		}, 1000);

		delete resources[resource.id];
	}
}

function turnVillagerIntoPirate(player: GameResource, villager: GameResource) {
	if (player.role === 'PIRATE' && villager.role === 'VILLAGEOIS') {
		villager.role = 'PIRATE';
		player.statistics.villagersTurned++;
	}
}

function terminatePirate(
	player: GameResource,
	pirate: GameResource,
	resources: { [key: string]: GameResource },
) {
	if (player.role === 'VILLAGEOIS' && pirate.role === 'PIRATE') {
		delete resources[pirate.id];
		player.statistics.piratesTerminated++;
	}
}

function isNearby(position1: Position, position2: Position): boolean {
	const RADIUS_OF_EARTH = 6371e3; // Rayon de la Terre en mètres

	const lat1 = position1.x * (Math.PI / 180);
    const lon1 = position1.y * (Math.PI / 180);
    const lat2 = position2.x * (Math.PI / 180);
    const lon2 = position2.y * (Math.PI / 180);

	const deltaLat = lat2 - lat1;
	const deltaLon = lon2 - lon1;

	const a =
		Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
		Math.cos(lat1) *
			Math.cos(lat2) *
			Math.sin(deltaLon / 2) *
			Math.sin(deltaLon / 2);
	const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

	const distance = RADIUS_OF_EARTH * c; // Distance en mètres

	return distance <= MAX_DISTANCE_TO_INTERACT;
}

export {
	createNewPlayerAtPosition,
	takeResource,
	getPlayerByLogin,
	getRessources,
	grabPotionFlask,
	turnVillagerIntoPirate,
	terminatePirate,
	isNearby,
};
