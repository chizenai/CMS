import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

function safeParseJSON(str, defaultValue = {}) {
  if (!str || str === 'undefined' || str === 'null') {
    return defaultValue
  }
  try {
    return JSON.parse(str)
  } catch (e) {
    console.warn('Failed to parse JSON:', str, e)
    return defaultValue
  }
}

export default new Vuex.Store({
  state: {
    token: localStorage.getItem('token') || '',
    userInfo: safeParseJSON(localStorage.getItem('userInfo'))
  },
  getters: {
    token: state => state.token,
    userInfo: state => state.userInfo
  },
  mutations: {
    SET_TOKEN(state, token) {
      state.token = token
      localStorage.setItem('token', token)
    },
    SET_USER_INFO(state, userInfo) {
      state.userInfo = userInfo
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
    },
    LOGOUT(state) {
      state.token = ''
      state.userInfo = {}
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    }
  },
  actions: {
    login({ commit }, { token, userInfo }) {
      commit('SET_TOKEN', token)
      commit('SET_USER_INFO', userInfo)
    },
    logout({ commit }) {
      commit('LOGOUT')
    }
  },
  modules: {
  }
})
