import { Position, GameResource } from '../../models/GameResource';
import { zrr } from '../../models/GameState';

function getRandomPositionInZrr(): Position {
	const currZrr = zrr;

	if (!currZrr) {
		throw new Error('Zrr is not defined yet');
	}

	const [topLeft, bottomRight] = currZrr;
	const [topLeftLat, topLeftLng] = topLeft;
	const [bottomRightLat, bottomRightLng] = bottomRight;

	const randomLat = Math.random() * (bottomRightLat - topLeftLat) + topLeftLat;
	const randomLng = Math.random() * (bottomRightLng - topLeftLng) + topLeftLng;

	return { x: randomLat, y: randomLng };
}

function createNewFlaskAtPosition(
    resources: { [key: string]: GameResource },
    position: Position,
) {
    const flaskId = `FLASK_${Date.now()}`;
    resources[flaskId] = {
        id: flaskId,
        role: 'FLASK',
        position: position,
        TTL: 10,
    };
}

export {
    getRandomPositionInZrr ,
    createNewFlaskAtPosition,
};
