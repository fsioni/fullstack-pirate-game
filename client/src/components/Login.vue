<template>
  <h2>{{ message }}</h2>

  <label for="login">Login :&nbsp;</label>
  <input id="login" v-model="login" type="text" />
  <br />
  <label for="password">Password :&nbsp;</label>
  <input id="password" v-model="password" type="password" />
  <br />
  <button @click="loginAction">Send</button>
</template>

<script>
export default {
  name: 'Login',
  props: {
    message: String
  },
  data() {
    return {
      login: '',
      password: ''
    }
  },
  methods: {
    async loginAction() {
      console.log('Login cliqué. Login:', this.login, 'Password:', this.password)

      try {
        const response = await fetch('localhost:8080/user/login', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            login: this.login,
            password: this.password
          })
        })

        if (!response.ok) {
          throw new Error(`Erreur ${response.status}`)
        }

        const data = await response.json()
        console.log('Réponse du serveur:', data)

        // Gère ici la réponse, comme rediriger l'utilisateur ou sauvegarder le token de session
      } catch (error) {
        console.error('Erreur lors de la connexion:', error)
      }
    }
  }
}
</script>

<style scoped>
input,
input[type='submit'],
select {
  color: grey;
  border: 1px solid;
}
</style>
