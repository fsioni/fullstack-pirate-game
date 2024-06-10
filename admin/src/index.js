import map from './map.js';
import form from './form.js';
import auth from './auth.js';
import './css/style.css';

// Api User (tomcat)

const mymap = map();

const myform = form(mymap);
const myauth = auth(mymap);