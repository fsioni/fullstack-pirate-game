type Position = [number, number];

interface BaseResource {
	id: string;
	position: Position;
}

interface PlayerStatistics {
	flasksGathered: number;
	piratesTerminated: number;
	villagersTurned: number;
}

interface PlayerResource extends BaseResource {
	role: 'VILLAGEOIS' | 'PIRATE';
	flasks: FlaskResource[];
	statistics: PlayerStatistics;
}

interface FlaskResource extends BaseResource {
	role: 'FLASK';
	TTL: number;
}

type GameResource = PlayerResource | FlaskResource;

export { GameResource, Position };
