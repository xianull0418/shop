<template>
  <router-view />
</template>

<script>
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'

export default {
  name: 'App',
  setup() {
    const userStore = useUserStore()

    onMounted(async () => {
      if (userStore.token) {
        try {
          await userStore.fetchUserInfo()
        } catch (error) {
          console.error('获取用户信息失败:', error)
        }
      }
    })
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  height: 100%;
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}
</style>
