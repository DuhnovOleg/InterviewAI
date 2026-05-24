import ContentCopyIcon from '@mui/icons-material/ContentCopy'
import VisibilityIcon from '@mui/icons-material/Visibility'
import { Alert, Box, Button, Container, Paper, Stack, Typography } from '@mui/material'
import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { getAssessmentAttempts } from '../features/assessment/api/assessmentApi'
import { AssessmentPageHeader } from '../features/assessment/components/AssessmentPageHeader'
import type { AssessmentAttemptListItem } from '../features/assessment/types/assessment.types'

export function AssessmentAttemptsPage() {
    const { assessmentId } = useParams()
    const navigate = useNavigate()
    const [items, setItems] = useState<AssessmentAttemptListItem[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        if (!assessmentId) return
        setLoading(true)
        setError(null)
        getAssessmentAttempts(assessmentId)
            .then(setItems)
            .catch((e: any) => {
                console.error('GET ASSESSMENT ATTEMPTS ERROR', e?.response?.data ?? e)
                setError(e?.response?.data?.message ?? 'Не удалось загрузить прохождения интервью')
            })
            .finally(() => setLoading(false))
    }, [assessmentId])

    const copyResult = async (token?: string) => {
        if (!token) return
        await navigator.clipboard.writeText(`${window.location.origin}/assessment/result/${token}`)
    }

    return (
        <Container maxWidth="md">
            <Box py={4}>
                <AssessmentPageHeader title="Прохождения интервью" subtitle="Кандидаты, которые проходили интервью по вашей ссылке" />

                <Stack spacing={2}>
                    {error && <Alert severity="error">{error}</Alert>}
                    {loading && <Paper sx={{ p: 3, borderRadius: 3 }}>Загрузка...</Paper>}
                    {!loading && !error && items.length === 0 && (
                        <Paper sx={{ p: 3, borderRadius: 3 }}>
                            <Typography variant="h6">Пока никто не проходил это интервью</Typography>
                            <Typography color="text.secondary" sx={{ mt: 1 }}>Когда кандидат откроет публичную ссылку и начнет прохождение, он появится здесь.</Typography>
                        </Paper>
                    )}

                    {items.map((item) => (
                        <Paper key={item.id} sx={{ p: 2.5, borderRadius: 3, border: '1px solid', borderColor: 'divider' }}>
                            <Stack spacing={1.25}>
                                <Typography variant="h6" fontWeight={800}>{item.candidateName}</Typography>
                                <Typography color="text.secondary">{item.candidateEmail || 'Email не указан'}</Typography>
                                <Typography>Статус: {item.status}</Typography>
                                <Typography>Балл: {item.overallScore ?? 'еще нет'}</Typography>
                                <Typography>Рекомендация: {item.recommendation ?? 'еще нет'}</Typography>

                                <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1}>
                                    <Button variant="contained" startIcon={<VisibilityIcon />} onClick={() => navigate(`/hr/assessment-attempts/${item.id}`)}>
                                        Открыть результат
                                    </Button>
                                    {item.resultPublicToken && (
                                        <Button variant="outlined" startIcon={<ContentCopyIcon />} onClick={() => copyResult(item.resultPublicToken)}>
                                            Скопировать ссылку результата
                                        </Button>
                                    )}
                                </Stack>
                            </Stack>
                        </Paper>
                    ))}
                </Stack>
            </Box>
        </Container>
    )
}
