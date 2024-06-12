interface Position {
	x: number;
	y: number;
}

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
	role: 'VILLAGEOIS' | 'PIRATE' | 'ADMIN';
	flasks: FlaskResource[];
	statistics: PlayerStatistics;
	nearbyResources: GameResource[];
	isDead: boolean;
}

interface FlaskResource extends BaseResource {
	role: 'FLASK';
	TTL: number;
}

type GameResource = PlayerResource | FlaskResource;

export { GameResource, Position, PlayerResource };
