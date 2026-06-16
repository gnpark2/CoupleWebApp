import axios from 'axios'

const AUTH_URL      = import.meta.env.VITE_AUTH_URL || 'http://localhost:8081'
const USER_URL      = import.meta.env.VITE_USER_URL || 'http://localhost:8082'
const COUPLE_URL    = import.meta.env.VITE_COUPLE_URL || 'http://localhost:8083'
const FEELING_URL   = import.meta.env.VITE_FEELING_URL || 'http://localhost:8085'
const DIARY_URL     = import.meta.env.VITE_DIARY_URL || 'http://localhost:8086'
const CHARACTER_URL = import.meta.env.VITE_CHARACTER_URL || 'http://localhost:8087'
const CALENDAR_URL  = import.meta.env.VITE_CALENDAR_URL || 'http://localhost:8088'
const AI_URL        = import.meta.env.VITE_AI_URL || 'http://localhost:8090'

function makeClient(baseURL: string) {
  const instance = axios.create({ baseURL })

  instance.interceptors.request.use((config) => {
    const token = localStorage.getItem('accessToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  })

  // On 401, try refresh once then retry
  instance.interceptors.response.use(
    (res) => res,
    async (error) => {
      const original = error.config
      if (error.response?.status === 401 && !original._retry) {
        original._retry = true
        const refreshToken = localStorage.getItem('refreshToken')
        if (refreshToken) {
          try {
            const { data } = await axios.post(`${AUTH_URL}/api/auth/refresh`, { refreshToken })
            localStorage.setItem('accessToken', data.accessToken)
            localStorage.setItem('refreshToken', data.refreshToken)
            original.headers.Authorization = `Bearer ${data.accessToken}`
            return instance(original)
          } catch {
            localStorage.clear()
            window.location.href = '/'
          }
        }
      }
      return Promise.reject(error)
    }
  )

  return instance
}

export const authApi      = makeClient(AUTH_URL)
export const userApi      = makeClient(USER_URL)
export const coupleApi     = makeClient(COUPLE_URL)
export const feelingApi    = makeClient(FEELING_URL)
export const diaryApi      = makeClient(DIARY_URL)
export const characterApi  = makeClient(CHARACTER_URL)
export const calendarApi   = makeClient(CALENDAR_URL)
export const aiApi         = makeClient(AI_URL)

export const REALTIME_WS_URL = import.meta.env.VITE_REALTIME_WS_URL || 'http://localhost:8084/ws'
