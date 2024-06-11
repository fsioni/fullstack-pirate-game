// initialisation de la map
const lat = 45.782,
    lng = 4.8656,
    zoom = 19;

let mymap = L.map('map', {
    center: [lat, lng],
    zoom: zoom,
});

// Initialisation de la map
function initMap() {
    // Création d'un "tile layer" (permet l'affichage sur la carte)
    L.tileLayer(
        'https://api.mapbox.com/v4/mapbox.satellite/{z}/{x}/{y}@2x.jpg90?access_token=pk.eyJ1IjoieGFkZXMxMDExNCIsImEiOiJjbGZoZTFvbTYwM29sM3ByMGo3Z3Mya3dhIn0.df9VnZ0zo7sdcqGNbfrAzQ',
        {
            maxZoom: 21,
            minZoom: 1,
            attribution:
                'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, ' +
                '<a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' +
                'Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
            id: 'mapbox/streets-v11',
            tileSize: 512,
            zoomOffset: -1,
            accessToken:
                'pk.eyJ1IjoibTFpZjEzIiwiYSI6ImNqczBubmhyajFnMnY0YWx4c2FwMmRtbm4ifQ.O6W7HeTW3UvOVgjCiPrdsA',
        },
    ).addTo(mymap);

    // Ajout d'un marker
    L.marker([45.78207, 4.86559])
        .addTo(mymap)
        .bindPopup('Entrée du bâtiment<br>Nautibus.')
        .openPopup();

    // Clic sur la carte
    mymap.on('click', e => {
        updateMap([e.latlng.lat, e.latlng.lng], mymap.getZoom());
    });

    mymap.on('zoomend', e => {
        updateMap([e.target.getCenter().lat, e.target.getCenter().lng], mymap.getZoom());
    });
    var markersList = [];
    const { pirateIcon, villageoisIcon, flaskIcon } = getIcons(L)
    setInterval(async () => {
        if (!localStorage.getItem('auth')) return;
        // Récupération des ressources
        const baseUrl = window.location.origin;
        const rep = await fetch('/game/api/resources', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${localStorage.getItem('auth')}`,
            }
        });

        if (rep.status === 200) {
            // Suppression des anciens markers
            for (const marker of markersList) {
                marker.remove();
            }
            markersList = [];

            const resources = await rep.json();
            console.log(resources)
            for (const resource of resources) {
                console.log(resource)
                const icon = resource.role === 'PIRATE' ? pirateIcon : resource.role === 'VILLAGEOIS' ? villageoisIcon : flaskIcon;
                const marker = L.marker([resource.position.x, resource.position.y], { icon }).addTo(mymap)
                marker.bindPopup(resource.role)

                markersList.push(marker);
            }
        }
    }, 1000);

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

import pirateIconUrl from './img/pirate.png';
import villageoisIconUrl from './img/villageois.png';
import flaskIconUrl from './img/flask.png';

function getIcons(L) {
    const pirateIcon = L.icon({
        iconUrl: pirateIconUrl,
        iconSize: [64, 64],
        iconAnchor: [16, 32],
        popupAnchor: [0, -32]
    })

    const villageoisIcon = L.icon({
        iconUrl: villageoisIconUrl,
        iconSize: [64, 64],
        iconAnchor: [16, 32],
        popupAnchor: [0, -32]
    })

    const flaskIcon = L.icon({
        iconUrl: flaskIconUrl,
        iconSize: [64, 64],
        iconAnchor: [16, 32],
        popupAnchor: [0, -32]
    })

    return { pirateIcon, villageoisIcon, flaskIcon }
}