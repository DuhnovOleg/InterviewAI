import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline'
import ContentCopyIcon from '@mui/icons-material/ContentCopy'
import VisibilityIcon from '@mui/icons-material/Visibility'
import {
    Alert,
    Box,
    Button,
    Container,
    Paper,
    Stack,
    Typography,
} from '@mui/material'
import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getMyAssessments } from '../features/assessment/api/assessmentApi'
import { AssessmentPageHeader } from '../features/assessment/components/AssessmentPageHeader'
import type { AssessmentListItem } from '../features/assessment/types/assessment.types'

export function AssessmentListPage() {
    const [items, setItems] = useState<AssessmentListItem[]>([])
    const [error, setError] = useState<string | null>(null)
    const [loading, setLoading] = useState(true)
    const navigate = useNavigate()

    useEffect(() => {
        let active = true

        setLoading(true)
        setError(null)

        getMyAssessments()
            .then((data) => {
                if (!active) return
                setItems(data)
            })
            .catch((e: any) => {
                if (!active) return
                console.error('GET MY ASSESSMENTS ERROR', e?.response?.data ?? e)
                setError(
                    e?.response?.data?.message ??
                    e?.response?.data?.error ??
                    'Не удалось загрузить список интервью',
                )
            })
            .finally(() => {
                if (!active) return
                setLoading(false)
            })

        return () => {
            active = false
        }
    }, [])

    const copyLink = async (publicToken: string) => {
        await navigator.clipboard.writeText(
            `${window.location.origin}/assessment/public/${publicToken}`,
        )
    }

    return (
        <Container maxWidth="md">
            <Box py={4}>
                <AssessmentPageHeader
                    title="Мои HR-интервью"
                    subtitle="Здесь отображаются интервью, созданные вами для кандидатов"
                />

                <Stack spacing={2}>
                    <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
                        <Button
                            variant="contained"
                            startIcon={<AddCircleOutlineIcon />}
                            onClick={() => navigate('/hr/assessments/create')}
                        >
                            Создать интервью
                        </Button>
                    </Box>

                    {error && <Alert severity="error">{error}</Alert>}

                    {loading && (
                        <Paper sx={{ p: 3, borderRadius: 3 }}>
                            <Typography color="text.secondary">Загрузка...</Typography>
                        </Paper>
                    )}

                    {!loading && !error && items.length === 0 && (
                        <Paper sx={{ p: 3, borderRadius: 3 }}>
                            <Typography variant="h6">Интервью пока нет</Typography>
                            <Typography color="text.secondary" sx={{ mt: 1 }}>
                                Создайте первое HR-интервью и отправьте публичную ссылку кандидату.
                            </Typography>
                        </Paper>
                    )}

                    {!loading &&
                        items.map((item) => (
                            <Paper
                                key={item.id}
                                sx={{
                                    p: 2.5,
                                    borderRadius: 3,
                                    border: '1px solid',
                                    borderColor: 'divider',
                                }}
                            >
                                <Stack spacing={1.5}>
                                    <Box>
                                        <Typography variant="h6" fontWeight={700}>
                                            {item.title}
                                        </Typography>

                                        <Typography color="text.secondary">
                                            {item.profession} · {item.level}
                                        </Typography>
                                    </Box>

                                    <Typography>
                                        Прохождений: {item.attemptsCount ?? 0}
                                    </Typography>

                                    <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1}>
                                        <Button
                                            variant="outlined"
                                            startIcon={<ContentCopyIcon />}
                                            onClick={() => copyLink(item.publicToken)}
                                        >
                                            Скопировать ссылку
                                        </Button>

                                        <Button
                                            variant="contained"
                                            startIcon={<VisibilityIcon />}
                                            onClick={() => navigate(`/hr/assessments/${item.id}/attempts`)}
                                        >
                                            Результаты
                                        </Button>
                                    </Stack>
                                </Stack>
                            </Paper>
                        ))}
                </Stack>
            </Box>
        </Container>
    )
}