import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import router from '@/router'
import { API_BASE_URL } from '@/config'

const request = axios.create({
    baseURL: API_BASE_URL,
    timeout: 5000
})

// 请求拦截器
request.interceptors.request.use(
    config => {
        const userStore = useUserStore()
        const token = userStore.token
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`
        }
        return config
    },
    error => {
        ElMessage.error('请求发送失败')
        return Promise.reject(error)
    }
)

// 响应拦截器
request.interceptors.response.use(
    response => {
        const res = response.data
        if (res.code !== 200) {
            ElMessage.error(res.message || '服务器异常')
            return Promise.reject(new Error(res.message || '服务器异常'))
        }
        return res
    },
    error => {
        const userStore = useUserStore()
        const currentPath = router.currentRoute.value.path
        let errorMessage = '网络请求失败'
        
        if (error.response) {
            switch (error.response.status) {
                case 401:
                    // 如果是401错误，说明token过期或无效
                    userStore.logout()
                    // 如果当前不在登录页面，则跳转到登录页面
                    if (currentPath !== '/mobile/login') {
                        errorMessage = '登录已过期，请重新登录'
                        ElMessage.error(errorMessage)
                        // 清除所有状态后再跳转
                        setTimeout(() => {
                            router.replace('/mobile/login')
                        }, 100)
                    }
                    break
                case 403:
                    errorMessage = '没有权限访问'
                    ElMessage.error(errorMessage)
                    break
                case 404:
                    errorMessage = '请求的接口不存在'
                    ElMessage.error(errorMessage)
                    break
                case 500:
                    errorMessage = '服务器内部错误'
                    ElMessage.error(errorMessage)
                    break
                default:
                    ElMessage.error(errorMessage)
            }
        } else if (error.message === 'Network Error') {
            errorMessage = '网络连接失败'
            ElMessage.error(errorMessage)
        } else if (error.code === 'ECONNABORTED') {
            errorMessage = '请求超时，请稍后重试'
            ElMessage.error(errorMessage)
        }
        
        return Promise.reject(error)
    }
)

export default request 