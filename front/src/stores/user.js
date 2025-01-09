import { defineStore } from 'pinia'
import { login, register, getUserInfo } from '@/api/auth'

export const useUserStore = defineStore('user', {
    state: () => ({
        token: localStorage.getItem('token') || '',
        userInfo: JSON.parse(localStorage.getItem('userInfo')) || null
    }),
    
    actions: {
        async login(data) {
            try {
                const res = await login(data)
                this.token = res.data
                localStorage.setItem('token', res.data)
                await this.fetchUserInfo()
                return Promise.resolve(res)
            } catch (error) {
                this.logout()
                return Promise.reject(error)
            }
        },
        
        async register(data) {
            try {
                const res = await register(data)
                if (res.code === 200) {
                    this.token = res.data
                    localStorage.setItem('token', res.data)
                    await this.fetchUserInfo()
                }
                return Promise.resolve(res)
            } catch (error) {
                this.logout()
                return Promise.reject(error)
            }
        },
        
        async fetchUserInfo() {
            try {
                const res = await getUserInfo()
                this.userInfo = res.data
                localStorage.setItem('userInfo', JSON.stringify(res.data))
                return Promise.resolve(res)
            } catch (error) {
                this.logout()
                return Promise.reject(error)
            }
        },
        
        logout() {
            this.token = ''
            this.userInfo = null
            localStorage.removeItem('token')
            localStorage.removeItem('userInfo')
            sessionStorage.clear()
        }
    }
}) 