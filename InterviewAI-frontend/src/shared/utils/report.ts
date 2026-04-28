import type { ReportModel } from '../../features/report/types/report.types'

function num(value: unknown): number | null {
    if (value === null || value === undefined) return null
    const parsed = Number(value)
    return Number.isNaN(parsed) ? null : parsed
}

function strArray(value: unknown): string[] {
    if (!Array.isArray(value)) return []
    return value.map(String)
}

export function mapReport(sessionId: string, raw: Record<string, unknown>): ReportModel {
    return {
        sessionId,
        profession: raw['profession'] ? String(raw['profession']) : undefined,
        declaredLevel: raw['declared_level'] ? String(raw['declared_level']) : undefined,
        overallScore: num(raw['overall_score'] ?? raw['average_score']),
        technicalScore: num(raw['technical_score']),
        correctnessScore: num(raw['correctness_score']),
        completenessScore: num(raw['completeness_score']),
        clarityScore: num(raw['clarity_score']),
        relevanceScore: num(raw['relevance_score']),
        grammarScore: num(raw['grammar_score']),
        confidenceScore: num(raw['confidence_score']),
        responseSpeedScore: num(raw['response_speed_score']),
        consistencyScore: num(raw['consistency_score']),
        hireRecommendation: raw['hire_recommendation'] ? String(raw['hire_recommendation']) : undefined,
        recommendedLevel: raw['recommended_level'] ? String(raw['recommended_level']) : undefined,
        strengths: strArray(raw['strengths']),
        weaknesses: strArray(raw['weaknesses']),
        redFlags: strArray(raw['red_flags']),
        improvementPlan: strArray(raw['improvement_plan']),
        summary: raw['summary'] ? String(raw['summary']) : undefined,
        questionBreakdown: Array.isArray(raw['question_breakdown'])
            ? (raw['question_breakdown'] as Array<Record<string, unknown>>)
            : [],
    }
}