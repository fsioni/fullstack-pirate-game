interface ResourceRequestParam {
	resourceId: string;
}

interface PlayerRequestParam {
	playerLogin: string;
}

type Position = [number, number];

interface GameRessource {
    id: string;
	type: string;
	position: Position;
}

export { ResourceRequestParam, PlayerRequestParam, Position, GameRessource };
