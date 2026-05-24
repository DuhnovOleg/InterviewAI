import type { MeResponse } from '../../features/auth/types/auth.types'

const AUTH_KEY = 'interviewai_auth'
const HISTORY_KEY = 'interviewai_recent_history'

const ACCESS_TOKEN_KEY = 'accessToken'
const REFRESH_TOKEN_KEY = 'refreshToken'

export type StoredAuth = {
    accessToken: string
    refreshToken: string
    user: MeResponse
}

export type StoredHistoryItem = {
    sessionId: string
    profession: string
    level: string
    createdAt: string
    averageScore: number | null
    recommendation: string
}

export function saveAuth(data: StoredAuth) {
    localStorage.setItem(AUTH_KEY, JSON.stringify(data))

    localStorage.setItem(ACCESS_TOKEN_KEY, data.accessToken)
    localStorage.setItem(REFRESH_TOKEN_KEY, data.refreshToken)
}

export function loadAuth(): StoredAuth | null {
    const raw = localStorage.getItem(AUTH_KEY)

    if (!raw) {
        return null
    }

    try {
        const parsed = JSON.parse(raw) as StoredAuth

        if (!parsed.accessToken || !parsed.refreshToken || !parsed.user) {
            clearAuth()
            return null
        }

        localStorage.setItem(ACCESS_TOKEN_KEY, parsed.accessToken)
        localStorage.setItem(REFRESH_TOKEN_KEY, parsed.refreshToken)

        return parsed
    } catch {
        clearAuth()
        return null
    }
}

export function clearAuth() {
    localStorage.removeItem(AUTH_KEY)
    localStorage.removeItem(ACCESS_TOKEN_KEY)
    localStorage.removeItem(REFRESH_TOKEN_KEY)
}

export function loadHistory(): StoredHistoryItem[] {
    const raw = localStorage.getItem(HISTORY_KEY)

    if (!raw) {
        return []
    }

    try {
        return JSON.parse(raw) as StoredHistoryItem[]
    } catch {
        return []
    }
}

export function saveHistory(items: StoredHistoryItem[]) {
    localStorage.setItem(HISTORY_KEY, JSON.stringify(items.slice(0, 5)))
}