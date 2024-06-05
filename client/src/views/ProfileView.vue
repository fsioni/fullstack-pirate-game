<template>
  <div class="about">
    <h1>My profile</h1>

    <h2>Edit my password</h2>
    <form @submit.prevent="updatePassword">
      <div class="form-group">
        <label for="password">New password</label>
        <input
          id="password"
          v-model="password"
          class="form-control"
          placeholder="Enter your new password"
          type="password"
        />
      </div>
      <button class="btn btn-primary" type="submit">Update password</button>
    </form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      password: ''
    }
  },
  methods: {
    updatePassword() {
      const login = localStorage.getItem('login')
      const apiUrl = import.meta.env.VITE_AUTH_API_URL + '/users/' + login
      const options = {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ password: this.password })
      }

      fetch(apiUrl, options)
        .then((response) => {
          if (!response.ok) {
            throw new Error('Erreur lors de la mise à jour du mot de passe')
          }
          return response.json()
        })
        .then((data) => {
          console.log('Mot de passe mis à jour avec succès', data)
        })
        .catch((error) => {
          console.error('Erreur:', error)
        })
    }
  },
  beforeRouteEnter(to, from, next) {
    const token = localStorage.getItem('token')
    if (!token) {
      next({ name: '/' }) // Redirige vers la page de connexion si l'utilisateur n'est pas connecté
    } else {
      next() // Continue vers la page de profil si l'utilisateur est connecté
    }
  }
}
</script>

<style>
.about {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 20px;
  box-sizing: border-box;
}

h1,
h2 {
  color: #d7d7d7;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 10px;
}

.form-group input {
  width: 100%;
  padding: 10px;
  box-sizing: border-box;
}

.btn {
  background-color: #007bff;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.btn:hover {
  background-color: #0056b3;
}

@media (min-width: 1024px) {
  .about {
    width: 50%;
    margin: auto;
  }
}
</style>
