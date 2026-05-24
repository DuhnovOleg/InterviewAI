import { Alert, Box, Container, Typography } from '@mui/material'
import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { getAssessmentAttemptDetails } from '../features/assessment/api/assessmentApi'
import { AssessmentPageHeader } from '../features/assessment/components/AssessmentPageHeader'
import { AssessmentAttemptResultView } from '../features/assessment/components/AssessmentAttemptResultView'
import type { AssessmentAttemptDetails } from '../features/assessment/types/assessment.types'

export function AssessmentAttemptDetailsPage() {
    const { attemptId } = useParams()
    const [details, setDetails] = useState<AssessmentAttemptDetails | null>(null)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        if (!attemptId) return
        getAssessmentAttemptDetails(attemptId)
            .then(setDetails)
            .catch((e: any) => setError(e?.response?.data?.message ?? 'Не удалось загрузить результат кандидата'))
    }, [attemptId])

    return (
        <Container maxWidth="md">
            <Box py={4}>
                <AssessmentPageHeader title="Результат кандидата" subtitle="Детальная история ответов и оценок" />
                {error && <Alert severity="error">{error}</Alert>}
                {!error && !details && <Typography>Загрузка...</Typography>}
                {details && <AssessmentAttemptResultView details={details} />}
            </Box>
        </Container>
    )
}
