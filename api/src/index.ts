import express, { Request, Response } from 'express';
import gameRouter from './routes/game/router';
import adminRouter from './routes/admin/router';

const app = express();
const port = 3376;

app.use(express.json());
app.use('/static', express.static('public'));

app.get('/', (req: Request, res: Response) => {
	res.redirect('/static/index.html');
});

app.use('/api', gameRouter);

app.use('/admin', adminRouter);

app.use((req, res) => {
	res.status(404).send("Sorry, that route doesn't exist.");
});

app.listen(port, () => {
	console.log(`Server is running at http://localhost:${port}`);
});
