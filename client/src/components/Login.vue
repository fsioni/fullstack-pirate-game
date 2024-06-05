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
  data() {
    return {
      login: '',
      password: '',
      message: ''
    }
  },
  methods: {
    async loginAction() {
      console.log('Login cliqué. Login:', this.login, 'Password:', this.password)

      const body = new URLSearchParams()
      body.append('login', this.login)
      body.append('password', this.password)
      try {
        const apiUrl = import.meta.env.VITE_AUTH_API_URL + '/user/login'
        const response = await fetch(apiUrl, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
          },
          body: body.toString()
        })

        if (!response.ok) {
          localStorage.removeItem('token')
          localStorage.removeItem('login')
          let message = `Erreur ${response.status} lors de la connexion.`
          if (response.status === 401) {
            message = 'Login ou mot de passe incorrect.'
          } else if (response.status === 404) {
            message = 'Utilisateur introuvable.'
          }
          this.$emit('login-error', message)
          return
        }

        this.$emit('login-successful')
        const token = response.headers.get('Authentication')
        localStorage.setItem('token', token)
        localStorage.setItem('login', this.login)
      } catch (error) {
        localStorage.removeItem('token')
        localStorage.removeItem('login')
        this.$emit('login-error', 'Erreur lors de la connexion.')
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
