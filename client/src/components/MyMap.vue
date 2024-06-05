<template>
  <section>
    <h2>Carte</h2>
    <p class="content">
      <strong>TODO :</strong> mettre à jour les positions des différents objets sur la carte.
    </p>
    <div id="map" ref="map" class="map"></div>
  </section>
</template>

<script>
import 'leaflet/dist/leaflet.css'
import { flasks, localPlayer, players, zrr } from '@/mocks/mockPositions.ts'
import pirateIconUrl from '@/assets/images/pirate.png'
import villageoisIconUrl from '@/assets/images/villageois.png'
import flaskIconUrl from '@/assets/images/flask.png'

let lat = 45.782,
  lng = 4.8656,
  zoom = 19
let myMap = {}

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

function addResourcesToMap(L) {
  const { pirateIcon, villageoisIcon, flaskIcon } = getIcons(L)

  players.forEach((player) => {
    const icon = player.role === 'PIRATE' ? pirateIcon : villageoisIcon
    L.marker(player.position, { icon })
      .addTo(myMap)
      .bindPopup(`Joueur ${player.id} : ${player.role}`)
  })

  flasks.forEach((flask) => {
    L.marker(flask.position, {
      icon: flaskIcon
    })
      .addTo(myMap)
      .bindPopup(`Flacon ${flask.id}`)
  })

  L.marker(localPlayer.position, { icon: villageoisIcon })
    .addTo(myMap)
    .bindPopup(`Local Player : ${localPlayer.role}`)

  L.rectangle(zrr, { color: 'blue', weight: 1 }).addTo(myMap)
}

async function initMap() {
  const L = await import('leaflet')

  myMap = L.map('map', {
    center: [lat, lng],
    zoom: zoom
  })

  L.tileLayer(
    'https://api.mapbox.com/v4/mapbox.satellite/{z}/{x}/{y}@2x.jpg90?access_token=pk.eyJ1IjoieGFkZXMxMDExNCIsImEiOiJjbGZoZTFvbTYwM29sM3ByMGo3Z3Mya3dhIn0.df9VnZ0zo7sdcqGNbfrAzQ',
    {
      maxZoom: 22,
      minZoom: 1,
      attribution:
        'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, ' +
        '<a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' +
        'Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
      id: 'mapbox/streets-v11',
      tileSize: 512,
      zoomOffset: -1,
      accessToken:
        'pk.eyJ1IjoieGFkZXMxMDExNCIsImEiOiJjbGZoZTFvbTYwM29sM3ByMGo3Z3Mya3dhIn0.df9VnZ0zo7sdcqGNbfrAzQ'
    }
  ).addTo(myMap)
  return L
}

export default {
  name: 'MyMap',
  methods: {
    updateMap: function () {
      myMap.setView([lat, lng], zoom)
      return false
    }
  },
  async beforeMount() {
    console.log('Loading Leaflet...')
    const L = await initMap()

    addResourcesToMap(L)

    myMap.on('click', (e) => {
      lat = e.latlng.lat
      lng = e.latlng.lng
      this.updateMap()
    })
  }
}
</script>

<style scoped>
.map {
  height: 400px;
  width: 100%;
  border: 1px solid;
}
</style>
