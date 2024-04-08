// initialisation de la map
const lat = 45.782, lng = 4.8656, zoom = 19;

let mymap = L.map('map', {
	center: [lat, lng],
	zoom: zoom
});

// Initialisation de la map
function initMap() {
	// Création d'un "tile layer" (permet l'affichage sur la carte)
	L.tileLayer('https://api.mapbox.com/v4/mapbox.satellite/{z}/{x}/{y}@2x.jpg90?access_token=pk.eyJ1IjoieGFkZXMxMDExNCIsImEiOiJjbGZoZTFvbTYwM29sM3ByMGo3Z3Mya3dhIn0.df9VnZ0zo7sdcqGNbfrAzQ', {
		maxZoom: 21,
		minZoom: 1,
		attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, ' +
			'<a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' +
			'Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
		id: 'mapbox/streets-v11',
		tileSize: 512,
		zoomOffset: -1,
		accessToken: 'pk.eyJ1IjoibTFpZjEzIiwiYSI6ImNqczBubmhyajFnMnY0YWx4c2FwMmRtbm4ifQ.O6W7HeTW3UvOVgjCiPrdsA'
	}).addTo(mymap);

	// Ajout d'un marker
	L.marker([45.78207, 4.86559]).addTo(mymap).bindPopup('Entrée du bâtiment<br>Nautibus.').openPopup();

	// Clic sur la carte
	mymap.on('click', e => {
		updateMap([e.latlng.lat, e.latlng.lng], mymap.getZoom());
	});

	return mymap;
}

// Mise à jour de la map
function updateMap(latlng, zoom) {
	// Affichage à la nouvelle position
	mymap.setView(latlng, zoom);

	// La fonction de validation du formulaire renvoie false pour bloquer le rechargement de la page.
	return false;
}

export { updateMap };
export default initMap;

// mettre à jour les champs au click
mymap.on('click', function(e) {
	document.getElementById('lat').value = e.latlng.lat;
	document.getElementById('lon').value = e.latlng.lng;
});

// mettre à jour le champ au zoom
mymap.on('zoomend', function() {
	document.getElementById('zoom').value = mymap.getZoom();
	document.getElementById('zoomValue').textContent = mymap.getZoom();
});

// mettre à jour les champs au déplacement
mymap.on('moveend', function() {
	const center = mymap.getCenter();
	document.getElementById('lat').value = center.lat;
	document.getElementById('lon').value = center.lng;
});

// mettre à jour la carte au changement des champs
document.getElementById('lat').addEventListener('change', function() {
	const lat = parseFloat(this.value);
	const lng = parseFloat(document.getElementById('lon').value);
	mymap.setView([lat, lng]);
});

document.getElementById('lon').addEventListener('change', function() {
	const lat = parseFloat(document.getElementById('lat').value);
	const lng = parseFloat(this.value);
	mymap.setView([lat, lng]);
});

document.getElementById('zoom').addEventListener('input', function() {
	const zoom = parseInt(this.value);
	mymap.setZoom(zoom);
	document.getElementById('zoomValue').textContent = zoom;
});