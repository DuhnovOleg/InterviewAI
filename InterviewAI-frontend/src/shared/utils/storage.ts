import type { MeResponse } from '../../features/auth/types/auth.types'

const AUTH_KEY = 'interviewai_auth'
const HISTORY_KEY = 'interviewai_recent_history'

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
    localStorage.setItem('accessToken', data.accessToken)
}

export function loadAuth(): StoredAuth | null {
    const raw = localStorage.getItem(AUTH_KEY)
    if (!raw) return null
    try {
        return JSON.parse(raw) as StoredAuth
    } catch {
        return null
    }
}

export function clearAuth() {
    localStorage.removeItem(AUTH_KEY)
    localStorage.removeItem('accessToken')
}

export function loadHistory(): StoredHistoryItem[] {
    const raw = localStorage.getItem(HISTORY_KEY)
    if (!raw) return []
    try {
        return JSON.parse(raw) as StoredHistoryItem[]
    } catch {
        return []
    }
}

export function saveHistory(items: StoredHistoryItem[]) {
    localStorage.setItem(HISTORY_KEY, JSON.stringify(items.slice(0, 5)))
}