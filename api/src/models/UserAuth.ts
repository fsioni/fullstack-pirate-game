enum Species {
	VILLAGEOIS = 'VILLAGEOIS',
	PIRATE = 'PIRATE',
	ADMIN = 'ADMIN',
}

export default interface UserAuth {
	login: string;
	species: Species;
	connected: boolean;
	image: string;
}
