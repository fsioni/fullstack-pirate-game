import { Position } from '../../models/GameResource';

interface ZrrRequestParams {
	point1: Position;
	point2: Position;
}

interface TtlRequestParams {
	ttl: number;
}

export { ZrrRequestParams, TtlRequestParams };
