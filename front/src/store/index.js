import { createStore } from 'vuex'

export default createStore({
    state: {
        token: localStorage.getItem('token') || '',
        userInfo: null
    },
    mutations: {
        SET_TOKEN(state, token) {
            state.token = token
            localStorage.setItem('token', token)
        },
        SET_USER_INFO(state, userInfo) {
            state.userInfo = userInfo
        },
        CLEAR_USER(state) {
            state.token = ''
            state.userInfo = null
            localStorage.removeItem('token')
        }
    },
    actions: {
        login({ commit }, token) {
            commit('SET_TOKEN', token)
        },
        logout({ commit }) {
            commit('CLEAR_USER')
        }
    }
}) 