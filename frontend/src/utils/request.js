import axios from 'axios'
import { Message } from 'element-ui'
import router from '@/router'

const service = axios.create({
  baseURL: '/api',
  timeout: 30000
})

service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) {
      return res
    } else {
      Message.error(res.message || '请求失败')
      if (res.code === 401) {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        router.push('/login')
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    }
  },
  error => {
    console.error('Response error:', error)
    
    if (error.code === 'ECONNREFUSED' || 
        error.message.includes('ECONNREFUSED') ||
        error.message.includes('Network Error') ||
        !error.response) {
      Message.error('无法连接到服务器，请检查后端服务是否启动')
      return Promise.reject(new Error('服务器连接失败'))
    }
    
    if (error.response) {
      if (error.response.status === 401) {
        Message.error('登录已过期，请重新登录')
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        router.push('/login')
      } else if (error.response.status === 500) {
        Message.error('服务器内部错误')
      } else if (error.response.status === 404) {
        Message.error('请求的接口不存在')
      } else {
        Message.error(error.message || '请求失败')
      }
    }
    return Promise.reject(error)
  }
)

export default service
