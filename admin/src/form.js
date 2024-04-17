var mymap = null;
var zoneLayer = null;

function init(map, apiPath) {
    mymap = map;

    // ==================
    // Positionnement
    // ==================

    // mettre à jour les champs au click
    mymap.on('click', function (e) {
        document.getElementById('lat').value = e.latlng.lat;
        document.getElementById('lon').value = e.latlng.lng;
    });

    // mettre à jour le champ au zoom
    mymap.on('zoomend', function () {
        document.getElementById('zoom').value = mymap.getZoom();
        document.getElementById('zoomValue').textContent = mymap.getZoom();
    });

    // mettre à jour les champs au déplacement
    mymap.on('moveend', function () {
        const center = mymap.getCenter();
        document.getElementById('lat').value = center.lat;
        document.getElementById('lon').value = center.lng;
    });

    // mettre à jour la carte au changement des champs
    document.getElementById('lat').addEventListener('change', function () {
        const lat = parseFloat(this.value);
        const lng = parseFloat(document.getElementById('lon').value);
        mymap.setView([lat, lng]);
    });

    document.getElementById('lon').addEventListener('change', function () {
        const lat = parseFloat(document.getElementById('lat').value);
        const lng = parseFloat(this.value);
        mymap.setView([lat, lng]);
    });

    document.getElementById('zoom').addEventListener('input', function () {
        const zoom = parseInt(this.value);
        mymap.setZoom(zoom);
        document.getElementById('zoomValue').textContent = zoom;
    });

    // ==================
    // Zrr
    // ==================

    // SET ZRR
    document
        .getElementById('setZrrButton')
        .addEventListener('click', function () {
            // récupérez les coordonnées des coins
            const lat1 = mymap.getBounds().getNorth();
            const lon1 = mymap.getBounds().getWest();
            const lat2 = mymap.getBounds().getSouth();
            const lon2 = mymap.getBounds().getEast();

            document.getElementById('lat1').value = lat1;
            document.getElementById('lon1').value = lon1;
            document.getElementById('lat2').value = lat2;
            document.getElementById('lon2').value = lon2;

            // créez les limites de la zone à partir des coordonnées des coins
            const bounds = [
                [lat1, lon1],
                [lat2, lon2],
            ];

            // supprimez la zone précédente (si elle existe)
            if (zoneLayer) {
                mymap.removeLayer(zoneLayer);
            }

            // créez une nouvelle zone et ajoutez-la à la carte
            zoneLayer = L.rectangle(bounds, { color: '#0000ff', weight: 1 }).addTo(
                mymap,
            );
        });

    // SEND ZRR
    document
        .getElementById('sendZrrButton')
        .addEventListener('click', async function () {
            // récupérez les coordonnées des coins
            const lat1 = parseFloat(document.getElementById('lat1').value);
            const lon1 = parseFloat(document.getElementById('lon1').value);
            const lat2 = parseFloat(document.getElementById('lat2').value);
            const lon2 = parseFloat(document.getElementById('lon2').value);

            const rep = await fetch('/admin/zrr', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'authorization': 'Bearer ' + localStorage.getItem('auth'),
                },
                body: JSON.stringify({
                    zrr: [
                        [lat1, lon1],
                        [lat2, lon2],
                    ]
                }),
            });

            console.log(rep);
        });

    // SEND TTL
    document.getElementById('setTtlButton').addEventListener('click', async function () {
        const ttl = parseInt(document.getElementById('ttl').value);

        // call api
        const rep = await fetch('/admin/ttl', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'authorization': 'Bearer ' + localStorage.getItem('auth'),
            },
            body: JSON.stringify({
                ttl: ttl,
            }),
        });

        console.log(rep);
    });
}

export default init;
