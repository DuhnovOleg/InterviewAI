import { create } from 'zustand'
import {
    getPublicAssessment,
    startPublicAssessment,
    submitPublicAssessmentAnswer,
    submitPublicAssessmentVoiceAnswer,
} from '../../features/assessment/api/assessmentApi'
import type { PublicAssessment } from '../../features/assessment/types/assessment.types'

type ChatRole = 'user' | 'assistant'

export type AssessmentChatMessage = {
    role: ChatRole
    text: string
}

type AssessmentStore = {
    publicToken: string | null
    attemptId: string | null
    assessment: PublicAssessment | null
    question: string | null
    questionNumber: number
    totalQuestions: number
    interviewComplete: boolean
    resultPublicToken: string | null
    isLoading: boolean
    messages: AssessmentChatMessage[]

    loadPublicAssessment: (publicToken: string) => Promise<void>
    startAttempt: (candidateName: string, candidateEmail: string) => Promise<void>
    submitAnswer: (answer: string) => Promise<void>
    submitVoiceAnswer: (audioBase64: string) => Promise<void>
    resetAssessment: () => void
}

function pushMessage(
    messages: AssessmentChatMessage[],
    role: ChatRole,
    text: string,
): AssessmentChatMessage[] {
    return [...messages, { role, text }]
}

export const useAssessmentStore = create<AssessmentStore>((set, get) => ({
    publicToken: null,
    attemptId: null,
    assessment: null,
    question: null,
    questionNumber: 0,
    totalQuestions: 0,
    interviewComplete: false,
    resultPublicToken: null,
    isLoading: false,
    messages: [],

    loadPublicAssessment: async (publicToken: string) => {
        set({ isLoading: true })

        try {
            const assessment = await getPublicAssessment(publicToken)

            set({
                publicToken,
                assessment,
                isLoading: false,
            })
        } catch {
            set({ isLoading: false })
            throw new Error('Не удалось загрузить интервью')
        }
    },

    startAttempt: async (candidateName: string, candidateEmail: string) => {
        const publicToken = get().publicToken

        if (!publicToken) {
            throw new Error('Публичный токен интервью не найден')
        }

        set({ isLoading: true })

        try {
            const data = await startPublicAssessment(publicToken, {
                candidateName,
                candidateEmail,
            })

            set((state) => ({
                attemptId: data.attemptId,
                question: data.question,
                questionNumber: data.questionNumber,
                totalQuestions: data.totalQuestions,
                isLoading: false,
                messages: pushMessage(
                    state.messages,
                    'assistant',
                    `Интервью начато.\n\nВопрос ${data.questionNumber} из ${data.totalQuestions}\n\n${data.question}`,
                ),
            }))
        } catch {
            set({ isLoading: false })
            throw new Error('Не удалось начать интервью')
        }
    },

    submitAnswer: async (answer: string) => {
        const attemptId = get().attemptId

        if (!attemptId || !answer.trim()) {
            return
        }

        set((state) => ({
            isLoading: true,
            messages: pushMessage(state.messages, 'user', answer),
        }))

        try {
            const data = await submitPublicAssessmentAnswer(attemptId, {
                answer,
            })

            const assistantParts: string[] = []

            if (data.feedback) {
                assistantParts.push(`Фидбек: ${data.feedback}`)
            }

            if (data.message) {
                assistantParts.push(data.message)
            }

            if (data.question) {
                assistantParts.push(data.question)
            }

            set((state) => ({
                isLoading: false,
                interviewComplete: data.interviewComplete,
                resultPublicToken: data.resultPublicToken ?? state.resultPublicToken,
                question: data.question ?? state.question,
                questionNumber: data.questionNumber ?? state.questionNumber,
                totalQuestions: data.totalQuestions ?? state.totalQuestions,
                messages: pushMessage(
                    state.messages,
                    'assistant',
                    assistantParts.join('\n\n') || 'Ответ обработан',
                ),
            }))
        } catch (error: any) {
            console.error('ASSESSMENT TEXT ANSWER ERROR', error?.response?.data ?? error)

            set((state) => ({
                isLoading: false,
                messages: pushMessage(
                    state.messages,
                    'assistant',
                    error?.response?.data?.message ??
                    error?.response?.data?.error ??
                    error?.response?.data?.detail ??
                    'Не удалось отправить ответ',
                ),
            }))
        }
    },

    submitVoiceAnswer: async (audioBase64: string) => {
        const attemptId = get().attemptId

        if (!attemptId) {
            return
        }

        set({ isLoading: true })

        try {
            const data = await submitPublicAssessmentVoiceAnswer(attemptId, {
                audioBase64,
            })

            const assistantParts: string[] = []

            if (data.feedback) {
                assistantParts.push(`Фидбек: ${data.feedback}`)
            }

            if (data.message) {
                assistantParts.push(data.message)
            }

            if (data.question) {
                assistantParts.push(data.question)
            }

            set((state) => {
                let messages = state.messages

                messages = pushMessage(
                    messages,
                    'user',
                    data.transcribedText || '🎙️ Голосовой ответ обработан',
                )

                messages = pushMessage(
                    messages,
                    'assistant',
                    assistantParts.join('\n\n') || 'Голосовой ответ обработан',
                )

                return {
                    isLoading: false,
                    interviewComplete: data.interviewComplete,
                    resultPublicToken: data.resultPublicToken ?? state.resultPublicToken,
                    question: data.question ?? state.question,
                    questionNumber: data.questionNumber ?? state.questionNumber,
                    totalQuestions: data.totalQuestions ?? state.totalQuestions,
                    messages,
                }
            })
        } catch (error: any) {
            console.error('ASSESSMENT VOICE ANSWER ERROR', error?.response?.data ?? error)

            set((state) => ({
                isLoading: false,
                messages: pushMessage(
                    state.messages,
                    'assistant',
                    error?.response?.data?.message ??
                    error?.response?.data?.error ??
                    error?.response?.data?.detail ??
                    'Не удалось отправить голосовой ответ',
                ),
            }))
        }
    },

    resetAssessment: () => {
        set({
            publicToken: null,
            attemptId: null,
            assessment: null,
            question: null,
            questionNumber: 0,
            totalQuestions: 0,
            interviewComplete: false,
            resultPublicToken: null,
            isLoading: false,
            messages: [],
        })
    },
}))