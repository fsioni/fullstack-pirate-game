import express, { Request, Response } from 'express';
import georesourcesRouter from './routes/georesources/router';

const app = express();
const port = 3000;

app.use(express.json());
app.use('/static', express.static('public'));

app.get('/', (req: Request, res: Response) => {
	res.redirect('/static/index.html');
});

app.use('/resources', georesourcesRouter);

app.use((req, res) => {
	res.status(404).send("Sorry, that route doesn't exist.");
});

app.listen(port, () => {
	console.log(`Server is running at http://localhost:${port}`);
});
