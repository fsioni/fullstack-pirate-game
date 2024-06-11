<template>
  <section>
    <h2>Carte</h2>
    <div id="map" ref="map" class="map"></div>
  </section>
</template>

<script>
import 'leaflet/dist/leaflet.css'
import pirateIconUrl from '@/assets/images/pirate.png'
import villageoisIconUrl from '@/assets/images/villageois.png'
import flaskIconUrl from '@/assets/images/flask.png'
import playerIconUrl from '@/assets/images/player.png'

let lat = 45.782,
  lng = 4.8656,
  zoom = 19
let myMap = {}
let localPlayerMarker = null

let L = null
let zrrLayer = null

let playerMarkers = new Map()
let flaskMarkers = new Map()

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

  const playerIcon = L.icon({
    iconUrl: playerIconUrl,
    iconSize: [64, 64],
    iconAnchor: [16, 32],
    popupAnchor: [0, -32]
  })

  return { pirateIcon, villageoisIcon, flaskIcon, playerIcon }
}

function updateMarkers(data, markersMap, icon) {
  // Suppression des marqueurs qui ne sont plus présents
  for (const id of markersMap.keys()) {
    if (!data.some((resource) => resource.id === id)) {
      myMap.removeLayer(markersMap.get(id))
      markersMap.delete(id)
    }
  }

  // Ajout ou mise à jour des marqueurs
  data.forEach((resource) => {
    if (resource.id === localStorage.getItem('login')) {
      return
    }

    let marker = markersMap.get(resource.id)
    if (marker) {
      marker.setLatLng([resource.position.x, resource.position.y])
      console.log('update', resource.id)
    } else {
      console.log('add', resource.id)
      marker = L.marker([resource.position.x, resource.position.y], { icon }).addTo(myMap)
      marker.bindPopup(resource.role)
      markersMap.set(resource.id, marker)
    }
  })
}

function addResourcesToMap(data) {
  const { pirateIcon, villageoisIcon, flaskIcon } = getIcons(L)

  const pirates = data.filter((resource) => resource.role === 'PIRATE')
  const villageois = data.filter((resource) => resource.role === 'VILLAGEOIS')
  const flasks = data.filter((resource) => resource.role === 'FLASK')

  updateMarkers(pirates, playerMarkers, pirateIcon)
  updateMarkers(villageois, playerMarkers, villageoisIcon)
  updateMarkers(flasks, flaskMarkers, flaskIcon)
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

let gpsAskedThisSession = false
let lastCoords = null
let lastCoordsTime = null
async function getPlayerPosition() {
    if (lastCoords && lastCoordsTime && Date.now() - lastCoordsTime < 2000) {
        console.log('Returning cached position')
        return lastCoords
    }

    const geoloc = await navigator.permissions.query({ name: 'geolocation' })

    if (geoloc.state === 'denied') {
      throw new Error('Geolocation permission denied')
    }

    if (geoloc.state === 'prompt' && gpsAskedThisSession) {
        return [0, 0]
    }

    if (!gpsAskedThisSession) {
        console.log('Requesting geolocation permission...')
        gpsAskedThisSession = true
    }

    return new Promise((resolve, reject) => {
        navigator.geolocation.getCurrentPosition(
          (position) => {
            lastCoords = [position.coords.latitude, position.coords.longitude]
            lastCoordsTime = Date.now()
            resolve([position.coords.latitude, position.coords.longitude])
          },
          (error) => {
            reject(error)
          }
        )
      })
}

export default {
  name: 'MyMap',
  methods: {
    updateMap: function () {
      myMap.setView([lat, lng], zoom)
      return false
    },
    updateLocalPlayerPosition: async function () {
        const position = await getPlayerPosition()
        if (localPlayerMarker == null) {
            const { playerIcon } = getIcons(L)

            localPlayerMarker = L.marker(position, { icon: playerIcon }).addTo(myMap)
        }

        localPlayerMarker.setLatLng(position)
    },
    sendLocalPlayerPositionToServer: async function () {
      const [latitude, longitude] = await getPlayerPosition()
      const positionData = JSON.stringify({
        position: { x: latitude, y: longitude }
      })

      const playerLogin = window.localStorage.getItem('login')
      const token = window.localStorage.getItem('token')
      const url = import.meta.env.VITE_GAME_API_URL + '/resources/' + playerLogin + '/position'
      fetch(url, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`
        },
        body: positionData
      })
        .then((response) => response.json())
        .catch((error) => {
          console.error('Error:', error)
        })
    },
    updateResourcesPositions: async function () {
      const token = window.localStorage.getItem('token')
      const url = import.meta.env.VITE_GAME_API_URL + '/resources'
      try {
        const response = await fetch(url, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`
          }
        })
        const data = await response.json()
        addResourcesToMap(data)
      } catch (error) {
        console.error('Error:', error)
      }
    },
    updateZrr: async function () {
      const token = window.localStorage.getItem('token')
      const url = import.meta.env.VITE_GAME_API_URL + '/zrr'
      try {
        const response = await fetch(url, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`
          }
        })
        const data = await response.json()

        if (zrrLayer) {
          myMap.removeLayer(zrrLayer)
        }

        zrrLayer = L.rectangle(data, { color: 'blue', weight: 1 }).addTo(myMap)
      } catch (error) {
        console.error('Error:', error)
      }
    }
  },
  beforeRouteEnter(to, from, next) {
    const token = localStorage.getItem('token')
    if (!token) {
      next({ name: '/' }) // Redirige vers la page de connexion si l'utilisateur n'est pas connecté
    } else {
      next() // Continue vers la page de map si l'utilisateur est connecté
    }
  },
  mounted() {
    setInterval(this.sendLocalPlayerPositionToServer, 1000)
    setInterval(this.updateLocalPlayerPosition, 1000)
    setInterval(this.updateResourcesPositions, 1000)
    setInterval(this.updateZrr, 10000)
  },
  async beforeMount() {
    console.log('Loading Leaflet...')
    L = await initMap()
    await this.updateResourcesPositions()
    await this.updateZrr()
    await this.updateLocalPlayerPosition()

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
