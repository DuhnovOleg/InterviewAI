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

function getReportPayload(raw: Record<string, unknown>): Record<string, unknown> {
    const nestedReport = raw.report

    if (
        nestedReport &&
        typeof nestedReport === 'object' &&
        !Array.isArray(nestedReport)
    ) {
        return nestedReport as Record<string, unknown>
    }

    return raw
}

function mapQuestionBreakdown(value: unknown): Array<Record<string, unknown>> {
    if (!Array.isArray(value)) {
        return []
    }

    return value.map((item) => {
        if (!item || typeof item !== 'object' || Array.isArray(item)) {
            return {}
        }

        const rawItem = item as Record<string, unknown>

        return {
            question: rawItem.question,
            overallScore: num(rawItem.overall_score ?? rawItem.overallScore),
            correctnessScore: num(rawItem.correctness_score ?? rawItem.correctnessScore),
            completenessScore: num(rawItem.completeness_score ?? rawItem.completenessScore),
            clarityScore: num(rawItem.clarity_score ?? rawItem.clarityScore),
            relevanceScore: num(rawItem.relevance_score ?? rawItem.relevanceScore),
            grammarScore: num(rawItem.grammar_score ?? rawItem.grammarScore),
            confidenceScore: num(rawItem.confidence_score ?? rawItem.confidenceScore),
            responseSpeedScore: num(rawItem.response_speed_score ?? rawItem.responseSpeedScore),
            feedback: rawItem.feedback,
        }
    })
}

export function mapReport(sessionId: string, raw: Record<string, unknown>): ReportModel {
    const report = getReportPayload(raw)

    return {
        sessionId: String(raw.sessionId ?? raw.session_id ?? report.session_id ?? sessionId),

        profession: report.profession ? String(report.profession) : undefined,

        declaredLevel:
            report.declared_level
                ? String(report.declared_level)
                : report.declaredLevel
                    ? String(report.declaredLevel)
                    : undefined,

        overallScore: num(report.overall_score ?? report.average_score ?? report.overallScore),
        technicalScore: num(report.technical_score ?? report.technicalScore),
        correctnessScore: num(report.correctness_score ?? report.correctnessScore),
        completenessScore: num(report.completeness_score ?? report.completenessScore),
        clarityScore: num(report.clarity_score ?? report.clarityScore),
        relevanceScore: num(report.relevance_score ?? report.relevanceScore),
        grammarScore: num(report.grammar_score ?? report.grammarScore),
        confidenceScore: num(report.confidence_score ?? report.confidenceScore),
        responseSpeedScore: num(report.response_speed_score ?? report.responseSpeedScore),
        consistencyScore: num(report.consistency_score ?? report.consistencyScore),

        hireRecommendation:
            report.hire_recommendation
                ? String(report.hire_recommendation)
                : report.hireRecommendation
                    ? String(report.hireRecommendation)
                    : undefined,

        recommendedLevel:
            report.recommended_level
                ? String(report.recommended_level)
                : report.recommendedLevel
                    ? String(report.recommendedLevel)
                    : undefined,

        strengths: strArray(report.strengths),
        weaknesses: strArray(report.weaknesses),
        redFlags: strArray(report.red_flags ?? report.redFlags),
        improvementPlan: strArray(report.improvement_plan ?? report.improvementPlan),

        summary: report.summary ? String(report.summary) : undefined,

        questionBreakdown: mapQuestionBreakdown(
            report.question_breakdown ?? report.questionBreakdown,
        ),
    }
}