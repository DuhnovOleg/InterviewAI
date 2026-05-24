export type AssessmentQuestion = {
    text: string
    position: number
}

export type CreateAssessmentRequest = {
    title: string
    description?: string
    profession: string
    level: string
    voiceRequired: boolean
    cameraRequired: boolean
    recordingRequired: boolean
    questions: AssessmentQuestion[]
}

export type CreateAssessmentResponse = {
    assessmentId: string
    publicToken: string
    publicUrl: string
}

export type AssessmentListItem = {
    id: string
    title: string
    profession: string
    level: string
    publicToken: string
    active: boolean
    attemptsCount: number
    createdAt: string
}

export type PublicAssessment = {
    title: string
    description?: string
    profession: string
    level: string
    voiceRequired: boolean
    cameraRequired: boolean
    recordingRequired: boolean
    questionsCount: number
}

export type StartPublicAssessmentRequest = {
    candidateName: string
    candidateEmail: string
}

export type StartPublicAssessmentResponse = {
    attemptId: string
    question: string
    questionNumber: number
    totalQuestions: number
    voiceRequired: boolean
    cameraRequired: boolean
}

export type SubmitAssessmentAnswerRequest = {
    answer: string
    responseTimeSeconds?: number
}

export type SubmitAssessmentVoiceAnswerRequest = {
    audioBase64: string
    responseTimeSeconds?: number
}

export type SubmitAssessmentAnswerResponse = {
    attemptId: string
    question?: string
    questionNumber?: number
    totalQuestions?: number
    interviewComplete: boolean
    previousScore?: number
    feedback?: string
    transcribedText?: string
    message?: string
    resultPublicToken?: string
    resultPublicUrl?: string
}

export type AssessmentAttemptListItem = {
    id: string
    candidateName: string
    candidateEmail: string
    status: string
    overallScore?: number
    recommendation?: string
    startedAt: string
    completedAt?: string
    reportId?: string
    resultPublicToken?: string
}

export type AssessmentAttemptDetails = {
    id: string
    assessmentId?: string
    assessmentTitle?: string
    profession?: string
    level?: string
    candidateName: string
    candidateEmail: string
    status: string
    overallScore?: number
    recommendation?: string
    reportId?: string
    resultPublicToken?: string
    resultPublicUrl?: string
    startedAt?: string
    completedAt?: string
    answers: AssessmentAttemptAnswer[]
}

export type AssessmentAttemptAnswer = {
    id?: string
    questionText: string
    answerText: string
    inputType: string
    overallScore?: number
    correctnessScore?: number
    completenessScore?: number
    clarityScore?: number
    relevanceScore?: number
    grammarScore?: number
    feedback?: string
}