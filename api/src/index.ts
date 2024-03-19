import express, { Request, Response } from 'express';

const app = express();
const port = 3000;

app.use('/static', express.static('public'));

app.get('/', (req: Request, res: Response) => {
	res.redirect('/static/index.html');
});

app.use((req, res) => {
	res.status(404).send("Sorry, that route doesn't exist.");
});

app.listen(port, () => {
	console.log(`Server is running at http://localhost:${port}`);
});
