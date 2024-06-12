<template>
  <section>
    <h2>Carte</h2>
    <div :id="isDead ? 'map map-dead' : 'map'" ref="map" class="map"></div>
    <div class="button-container">
      <button v-if="showTransformButton" class="enemy-action-button" @click="transformEnemy">
        Utiliser Fiole sur Villageois
      </button>
      <button v-if="showKillButton" class="enemy-action-button" @click="killEnemy">
        Utiliser Fiole pour Tuer Pirate
      </button>
      <button v-if="nearbyFlask" @click="collectFlask(nearbyFlask)">Récupérer Fiole</button>
      <button @click="centerMapOnPlayer">Centrer sur ma position</button>
    </div>
    <div class="flask-count">Potions : {{ flaskCount }}</div>
    <div class="user-role">Rôle : {{ userRole }}</div>
    <div v-if="isDead" class="dead-overlay">
      <div class="text">Vous êtes mort</div>
    </div>
    <div v-if="statistics" class="statistics">
      <h3>Statistiques</h3>
      <p>Fioles récupérées : {{ statistics.flasksGathered }}</p>
      <p>Pirates éliminés : {{ statistics.piratesTerminated }}</p>
      <p>Villageois convertis : {{ statistics.villagersTurned }}</p>
    </div>
  </section>
</template>

<script>
import 'leaflet/dist/leaflet.css'
import pirateIconUrl from '@/assets/images/pirate.png'
import pirateIconTintedUrl from '@/assets/images/pirate-tinted.png'
import villageoisIconUrl from '@/assets/images/villageois.png'
import villageoisIconTintedUrl from '@/assets/images/villageois-tinted.png'
import flaskIconUrl from '@/assets/images/flask.png'
import playerIconUrl from '@/assets/images/player.png'
import deadIconUrl from '@/assets/images/skull.png'

let lat = 45.782,
  lng = 4.8656,
  zoom = 19
let myMap = {}
let localPlayerMarker = null

let L = null
let zrrLayer = null

let pirateMarkers = new Map()
let villageoisMarkers = new Map()
let flaskMarkers = new Map()

let pirateIcon = null
let villageoisIcon = null
let flaskIcon = null
let playerIcon = null
let deadIcon = null

let pirateIconTinted = null
let villageoisIconTinted = null

function initIcons(L) {
  console.log('Initializing icons')

  pirateIcon = L.icon({
    iconUrl: pirateIconUrl,
    iconSize: [64, 64],
    iconAnchor: [32, 32],
    popupAnchor: [0, -32],
    className: 'PIRATE'
  })

  pirateIconTinted = L.icon({
    iconUrl: pirateIconTintedUrl,
    iconSize: [64, 64],
    iconAnchor: [32, 32],
    popupAnchor: [0, -32],
    className: 'PIRATE-TINTED'
  })

  villageoisIcon = L.icon({
    iconUrl: villageoisIconUrl,
    iconSize: [64, 64],
    iconAnchor: [32, 32],
    popupAnchor: [0, -32],
    className: 'VILLAGEOIS'
  })

  villageoisIconTinted = L.icon({
    iconUrl: villageoisIconTintedUrl,
    iconSize: [64, 64],
    iconAnchor: [32, 32],
    popupAnchor: [0, -32],
    className: 'VILLAGEOIS-TINTED'
  })

  flaskIcon = L.icon({
    iconUrl: flaskIconUrl,
    iconSize: [64, 64],
    iconAnchor: [32, 32],
    popupAnchor: [0, -32],
    className: 'FLASK'
  })

  playerIcon = L.icon({
    iconUrl: playerIconUrl,
    iconSize: [64, 64],
    iconAnchor: [32, 32],
    popupAnchor: [0, -32],
    className: 'PLAYER'
  })

  deadIcon = L.icon({
    iconUrl: deadIconUrl,
    iconSize: [64, 64],
    iconAnchor: [32, 32],
    popupAnchor: [0, -32],
    className: 'DEAD'
  })
}

function updateMarkers(data, markersMap, icon, tintedIcon) {
  const existingMarkerIDs = new Set(data.map((resource) => resource.id))

  // Supprimer les marqueurs qui n'existent plus
  for (const [id, marker] of markersMap.entries()) {
    if (!existingMarkerIDs.has(id) && id !== localStorage.getItem('login')) {
      console.log('Removing marker:', id)
      myMap.removeLayer(marker)
      markersMap.delete(id)
    }
  }

  // Ajouter ou mettre à jour les marqueurs existants
  data.forEach((resource) => {
    if (resource.id === localStorage.getItem('login')) {
      return
    }

    let marker = markersMap.get(resource.id)
    let resourceIcon = icon

    if (resource.role === 'VILLAGEOIS' || resource.role === 'PIRATE') {
      const resourceHaveFlask = resource.flasks.length > 0
      resourceIcon = resource.isDead ? deadIcon : resourceHaveFlask ? tintedIcon : icon
    }

    if (marker) {
      // Mise à jour de la position et de l'icône si nécessaire
      marker.setLatLng([resource.position.x, resource.position.y])

      // Mise à jour de l'icône si l'état a changé
      if (marker.options.icon !== resourceIcon) {
        marker.setIcon(resourceIcon)
      }
    } else {
      // Création d'un nouveau marqueur
      marker = L.marker([resource.position.x, resource.position.y], {
        icon: resourceIcon,
        className: resource.role
      }).addTo(myMap)
      marker.bindPopup(resource.role)
      markersMap.set(resource.id, marker)
    }
  })
}

function addResourcesToMap(data) {
  const pirates = data.filter((resource) => resource.role === 'PIRATE')
  const villageois = data.filter((resource) => resource.role === 'VILLAGEOIS')
  const flasks = data.filter((resource) => resource.role === 'FLASK')

  updateMarkers(pirates, pirateMarkers, pirateIcon, pirateIconTinted)
  updateMarkers(villageois, villageoisMarkers, villageoisIcon, villageoisIconTinted)
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
//   const mockX = 45.757122893740245
//   const mockY = 45.757122893740245

//   return [mockX, mockY] // TODO: Remplacer par la vraie position du joueur

  if (lastCoords && lastCoordsTime && Date.now() - lastCoordsTime < 2000) {
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
        localPlayerMarker = L.marker(position, { icon: playerIcon }).addTo(myMap)
      } else {
        localPlayerMarker.setLatLng(position)
      }
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
    async updateResourcesPositions() {
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

        const localPlayer = data.find((resource) => resource.id === localStorage.getItem('login'))
        if (localPlayer) {
          this.checkNearbyResourcesAndUpdateUI(localPlayer)
          this.flaskCount = localPlayer.flasks.length
          this.userRole = localPlayer.role
          this.isDead = localPlayer.isDead
          this.statistics = localPlayer.statistics
        }
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
    },
    checkNearbyResourcesAndUpdateUI(localPlayer) {
      const nearbyFlasks = localPlayer.nearbyResources.filter(
        (resource) => resource.role === 'FLASK'
      )
      const nearbyPlayers = localPlayer.nearbyResources.filter(
        (resource) => resource.role === 'PIRATE' || resource.role === 'VILLAGEOIS'
      )

      if (nearbyFlasks.length > 0) {
        this.nearbyFlask = nearbyFlasks[0]
      } else {
        this.nearbyFlask = null
      }

      this.handleNearbyPlayers(localPlayer, nearbyPlayers)
    },
    showFlaskButton(flask) {
      const flaskButton = document.createElement('button')
      flaskButton.innerText = 'Récupérer Fiole'
      flaskButton.onclick = () => this.collectFlask(flask)
      document.body.appendChild(flaskButton)
    },

    showEnemyButton: function (enemy, actionType) {
      const existingButton = document.querySelector(`.enemy-action-button[data-id="${enemy.id}"]`)
      if (existingButton) return

      const enemyButton = document.createElement('button')
      enemyButton.innerText =
        actionType === 'transform'
          ? 'Utiliser Fiole sur Villageois'
          : 'Utiliser Fiole pour Tuer Pirate'
      enemyButton.classList.add('enemy-action-button')
      enemyButton.dataset.id = enemy.id
      enemyButton.onclick = () => this.useFlaskOnEnemy(enemy, actionType)

      // Ajouter le bouton dans le conteneur de boutons
      document.querySelector('.button-container').appendChild(enemyButton)
    },
    collectFlask(flask) {
      const url = `${import.meta.env.VITE_GAME_API_URL}/resources/${flask.id}`
      fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${window.localStorage.getItem('token')}`
        },
        body: JSON.stringify({ operationType: 'grab potion flask' })
      })
        .then((response) => {
          if (response.ok) {
            console.log('Fiole récupérée')
            this.nearbyFlask = null
          } else {
            return response.json().then((data) => {
              throw new Error(data.message || 'Erreur lors de la récupération de la fiole')
            })
          }
        })
        .catch((error) => console.error('Error:', error))
    },
    useFlaskOnEnemy: function (enemy, actionType) {
      const operationType =
        actionType === 'transform' ? 'turn villager into pirate' : 'terminate pirate'
      const url = `${import.meta.env.VITE_GAME_API_URL}/resources/${enemy.id}`
      fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${window.localStorage.getItem('token')}`
        },
        body: JSON.stringify({ operationType })
      })
        .then((response) => response.json())
        .then((data) => {
          console.log("Action effectuée sur l'ennemi:", data)
        })
        .catch((error) => console.error('Error:', error))
    },
    centerMapOnPlayer() {
      myMap.setView(localPlayerMarker.getLatLng(), zoom)
    },
    handleNearbyPlayers: function (localPlayer, nearbyPlayers) {
      this.showTransformButton = false
      this.showKillButton = false
      this.targetEnemy = null

      nearbyPlayers = nearbyPlayers.filter((player) => !player.isDead)

      nearbyPlayers.forEach((player) => {
        if (localPlayer.role === 'PIRATE' && this.flaskCount > 0 && player.role === 'VILLAGEOIS') {
          this.showTransformButton = true
          this.targetEnemy = player
        } else if (
          localPlayer.role === 'VILLAGEOIS' &&
          this.flaskCount > 0 &&
          player.role === 'PIRATE'
        ) {
          this.showKillButton = true
          this.targetEnemy = player
        }
      })
    },
    transformEnemy() {
      if (this.targetEnemy) {
        this.useFlaskOnEnemy(this.targetEnemy, 'transform')
      }
    },
    killEnemy() {
      if (this.targetEnemy) {
        this.useFlaskOnEnemy(this.targetEnemy, 'kill')
      }
    },
    clearEnemyButtons: function () {
      const buttons = document.querySelectorAll('.enemy-action-button')
      buttons.forEach((button) => button.remove())
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
    await initIcons(L)
    await this.updateResourcesPositions()
    await this.updateZrr()
    await this.updateLocalPlayerPosition()

    myMap.on('click', (e) => {
      lat = e.latlng.lat
      lng = e.latlng.lng
      this.updateMap()
    })
  },
  data() {
    return {
      nearbyFlask: null,
      flaskCount: 0,
      userRole: null,
      isDead: false,
      statistics: null,
      showTransformButton: false,
      showKillButton: false,
      targetEnemy: null
    }
  }
}
</script>

<style scoped>
section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10px;
  overflow: auto;
  max-height: 100vh;
}

.map {
  height: 400px;
  width: 100%;
  border: 1px solid;
}

.button-container {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

button {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 15px 20px;
  font-size: 16px;
  border-radius: 5px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
  cursor: pointer;
  width: 80%;
  max-width: 300px;
  text-align: center;
}

button:hover {
  background-color: #0056b3;
}

button:active {
  background-color: #004494;
}

.info-container {
  width: 100%;
  padding: 10px;
  background-color: white;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
  border-radius: 5px;
  margin-bottom: 20px;
}

.flask-count,
.user-role {
  color: #ffffff;
  font-size: 18px;
  margin-bottom: 10px;
}

.statistics {
  text-align: center;
}

.dead-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.8);
  color: white;
  z-index: 1000;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  pointer-events: all;
}

.dead-overlay .text {
  font-size: 24px;
  margin-bottom: 20px;
}

.map-dead {
  pointer-events: none;
}

.enemy-action-button {
  background-color: #ff0000;
  color: white;
  border: none;
  padding: 15px 20px;
  font-size: 16px;
  border-radius: 5px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
  cursor: pointer;
  width: 80%;
  max-width: 300px;
  text-align: center;
  margin-top: 10px;
}

.enemy-action-button:hover {
  background-color: #cc0000;
}

.enemy-action-button:active {
  background-color: #990000;
}
</style>
