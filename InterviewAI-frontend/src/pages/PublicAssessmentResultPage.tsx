import { Alert, Box, Container } from '@mui/material'
import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { getPublicAssessmentResult } from '../features/assessment/api/assessmentApi'
import { AssessmentPageHeader } from '../features/assessment/components/AssessmentPageHeader'
import type { AssessmentAttemptDetails } from '../features/assessment/types/assessment.types'
import { AssessmentAttemptResultView } from '../features/assessment/components/AssessmentAttemptResultView'

export function PublicAssessmentResultPage() {
    const { resultToken = '' } = useParams()
    const [details, setDetails] = useState<AssessmentAttemptDetails | null>(null)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        if (!resultToken) return
        getPublicAssessmentResult(resultToken)
            .then(setDetails)
            .catch((e: any) => setError(e?.response?.data?.message ?? 'Не удалось загрузить результат'))
    }, [resultToken])

    return (
        <Container maxWidth="md">
            <Box py={4}>
                <AssessmentPageHeader title="Результат интервью" subtitle="Публичная страница результата прохождения" />
                {error && <Alert severity="error">{error}</Alert>}
                {details && <AssessmentAttemptResultView details={details} />}
            </Box>
        </Container>
    )
}
