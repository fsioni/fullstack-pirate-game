<script lang="ts" setup>
import { RouterLink, RouterView } from 'vue-router'
import HelloWorld from './components/HelloWorld.vue'

import { onMounted, reactive, toRefs } from 'vue'
import Login from '@/components/Login.vue'

const state = reactive({
  isLogged: false,
  loginError: ''
})
const { isLogged, loginError } = toRefs(state)

function logout() {
  isLogged.value = false
  loginError.value = ''
  localStorage.removeItem('token')
  localStorage.removeItem('login')
  window.location.href = '/'
}

function checkLoginStatus() {
  const token = localStorage.getItem('token')
  if (token) {
    const apiUrl =
      import.meta.env.VITE_AUTH_API_URL +
      '/user/authenticate?jwt=' +
      token +
      '&origin=' +
      window.location.origin
    fetch(apiUrl)
      .then((response) => {
        if (response.ok) {
          isLogged.value = true
        } else {
          localStorage.removeItem('token')
          localStorage.removeItem('login')
        }
      })
      .catch(() => {
        localStorage.removeItem('token')
        localStorage.removeItem('login')
      })
  }
}

onMounted(checkLoginStatus)
</script>

<template>
  <header>
    <img alt="Vue logo" class="logo" height="125" src="@/assets/logo.jpg" width="125" style="border-radius: 20px;"/>

    <div class="wrapper">
      <div v-if="isLogged">
        <button @click="logout">Logout</button>
        <HelloWorld msg="You're logged in!" />
      </div>
      <div v-else>
        <HelloWorld msg="You're not logged in!" />
        {{ loginError }}
        <Login
          :error-message="loginError"
          @login-successful="isLogged = true"
          @login-error="loginError = $event"
        />
      </div>

      <nav>
        <RouterLink to="/">Home</RouterLink>
        <RouterLink v-if="isLogged" to="/profile">My profile</RouterLink>
        <RouterLink v-if="isLogged" to="/map">Map</RouterLink>
      </nav>
    </div>
  </header>

  <RouterView />
</template>

<style scoped>
header {
  line-height: 1.5;
  max-height: 100vh;
}

.logo {
  display: block;
  margin: 0 auto 2rem;
}

nav {
  width: 100%;
  font-size: 12px;
  text-align: center;
  margin-top: 2rem;
}

nav a.router-link-exact-active {
  color: var(--color-text);
}

nav a.router-link-exact-active:hover {
  background-color: transparent;
}

nav a {
  display: inline-block;
  padding: 0 1rem;
  border-left: 1px solid var(--color-border);
}

nav a:first-of-type {
  border: 0;
}

@media (min-width: 1024px) {
  header {
    display: flex;
    place-items: center;
    padding-right: calc(var(--section-gap) / 2);
  }

  .logo {
    margin: 0 2rem 0 0;
  }

  header .wrapper {
    display: flex;
    place-items: flex-start;
    flex-wrap: wrap;
  }

  nav {
    text-align: left;
    margin-left: -1rem;
    font-size: 1rem;

    padding: 1rem 0;
    margin-top: 1rem;
  }
}
</style>
