import { PlayerRequestParam } from './interfaces';
import {
	GameResource,
	PlayerResource,
	Position,
} from '../../models/GameResource';

const DEFAULT_PLAYER_ROLE = 'VILLAGEOIS';
const MAX_DISTANCE_TO_INTERACT = 10;

function getRessources(
	player: GameResource,
	resources: { [key: string]: GameResource },
) {
	const res = [];
	for (const r in resources) {
		// Copy the resource to avoid modifying the original resource
		let ressToAdd = { ...resources[r] };

		if (
			resources[r].role === 'FLASK' ||
			resources[r].role === player.role ||
			player.role === 'ADMIN'
		) {
			//only the list of potions + only the other players of his/her team.
			// if ressource id is not the player id we don't do the nearbyResources
			if (resources[r].id === player.id) {
				// Get list of nearby resources of this resource if it's a player
				if (resources[r].role !== 'FLASK') {
					ressToAdd = { ...ressToAdd, nearbyResources: [] } as PlayerResource;
					ressToAdd.nearbyResources = getNearbyResources(
						resources,
						resources[r],
					);
				}
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
		if (
			resources[r].id !== resource.id &&
			isNearby(resource.position, resources[r].position)
		) {
			// On met des copies des ressources pour éviter de modifier les ressources originales
			nearbyResources.push({ ...resources[r], nearbyResources: [] });
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
		isDead: false,
	};
}

function grabPotionFlask(
	player: GameResource,
	resource: GameResource,
	resources: { [key: string]: GameResource },
) {
	if (
		(player.role === 'VILLAGEOIS' || player.role === 'PIRATE') &&
		resource.role === 'FLASK'
	) {
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
		// Vérifie si le villageois a une potion
		const villagerHasPotion = villager.flasks.some(
			flask => flask.role === 'FLASK',
		);

		if (villagerHasPotion) {
			console.log(
				'Le villageois ne peut pas être transformé car il a une potion.',
			);
			return; // Ne pas transformer le villageois
		}

		// Si le villageois n'a pas de potion, le pirate utilise une de ses propres potions
		const flaskIndex = player.flasks.findIndex(flask => flask.role === 'FLASK');
		if (flaskIndex > -1) {
			player.flasks.splice(flaskIndex, 1); // Utiliser une potion
			villager.role = 'PIRATE';
			player.statistics.villagersTurned++;
		} else {
			console.log(
				"Le pirate n'a pas de potion pour transformer le villageois.",
			);
		}
	}
}

function terminatePirate(
	player: GameResource,
	pirate: GameResource,
	resources: { [key: string]: GameResource },
) {
	if (player.role === 'VILLAGEOIS' && pirate.role === 'PIRATE') {
		pirate.isDead = true;
		player.statistics.piratesTerminated++;
	}
}

function isNearby(position1: Position, position2: Position): boolean {
	const RADIUS_OF_EARTH = 6371e3; // Rayon de la Terre en mètres

	const lat1 = position1.y * (Math.PI / 180);
	const lon1 = position1.x * (Math.PI / 180);
	const lat2 = position2.y * (Math.PI / 180);
	const lon2 = position2.x * (Math.PI / 180);

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
	getPlayerByLogin,
	getRessources,
	grabPotionFlask,
	turnVillagerIntoPirate,
	terminatePirate,
	isNearby,
};
