import express, { Request, Response } from 'express';
import gameRouter from './routes/game/router';
import adminRouter from './routes/admin/router';
import getUserMiddleware from './middlewares/getUserMiddleware';
import cors from 'cors';

const app = express();
const port = 3376;

// Enable CORS allow all origins
app.use(cors());
app.use(express.json());
app.use('/static', express.static('public'));

app.get('/', (req: Request, res: Response) => {
	res.redirect('/static/index.html');
});

app.use(getUserMiddleware);

app.use('/api', gameRouter);

app.use('/admin', adminRouter);

app.use((req, res) => {
	res.status(404).send("Sorry, that route doesn't exist.");
});

app.listen(port, () => {
	console.log(`Server is running at http://localhost:${port}`);
});
