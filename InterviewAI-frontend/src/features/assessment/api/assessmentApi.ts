import { http } from '../../../shared/api/http'
import type {
    AssessmentAttemptDetails,
    AssessmentAttemptListItem,
    AssessmentListItem,
    CreateAssessmentRequest,
    CreateAssessmentResponse,
    PublicAssessment,
    StartPublicAssessmentRequest,
    StartPublicAssessmentResponse,
    SubmitAssessmentAnswerRequest,
    SubmitAssessmentAnswerResponse,
    SubmitAssessmentVoiceAnswerRequest,
} from '../types/assessment.types'

export async function createAssessment(
    payload: CreateAssessmentRequest,
): Promise<CreateAssessmentResponse> {
    const { data } = await http.post<CreateAssessmentResponse>('/api/v1/assessments', payload)
    return data
}

export async function getMyAssessments(): Promise<AssessmentListItem[]> {
    const { data } = await http.get<AssessmentListItem[]>('/api/v1/assessments/my')
    return data
}

export async function getAssessmentAttempts(
    assessmentId: string,
): Promise<AssessmentAttemptListItem[]> {
    const { data } = await http.get<AssessmentAttemptListItem[]>(
        `/api/v1/assessments/${assessmentId}/attempts`,
    )
    return data
}

export async function getAssessmentAttemptDetails(
    attemptId: string,
): Promise<AssessmentAttemptDetails> {
    const { data } = await http.get<AssessmentAttemptDetails>(
        `/api/v1/assessments/attempts/${attemptId}`,
    )
    return data
}

export async function getPublicAssessment(publicToken: string): Promise<PublicAssessment> {
    const { data } = await http.get<PublicAssessment>(
        `/api/v1/public/assessments/${publicToken}`,
    )
    return data
}

export async function startPublicAssessment(
    publicToken: string,
    payload: StartPublicAssessmentRequest,
): Promise<StartPublicAssessmentResponse> {
    const { data } = await http.post<StartPublicAssessmentResponse>(
        `/api/v1/public/assessments/${publicToken}/start`,
        payload,
    )
    return data
}

export async function submitPublicAssessmentAnswer(
    attemptId: string,
    payload: SubmitAssessmentAnswerRequest,
): Promise<SubmitAssessmentAnswerResponse> {
    const { data } = await http.post<SubmitAssessmentAnswerResponse>(
        `/api/v1/public/assessment-attempts/${attemptId}/answer`,
        payload,
    )
    return data
}

export async function submitPublicAssessmentVoiceAnswer(
    attemptId: string,
    payload: SubmitAssessmentVoiceAnswerRequest,
): Promise<SubmitAssessmentAnswerResponse> {
    const { data } = await http.post<SubmitAssessmentAnswerResponse>(
        `/api/v1/public/assessment-attempts/${attemptId}/answer/voice`,
        payload,
    )
    return data
}

export async function getPublicAssessmentResult(token: string): Promise<AssessmentAttemptDetails> {
    const { data } = await http.get<AssessmentAttemptDetails>(
        `/api/v1/public/assessment-results/${token}`,
    )
    return data
}