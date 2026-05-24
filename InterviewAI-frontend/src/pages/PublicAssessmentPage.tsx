import {
    Alert,
    Box,
    Button,
    Container,
    Paper,
    Stack,
    TextField,
    Typography,
} from '@mui/material'
import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useAssessmentStore } from '../app/store/assessmentStore'
import { AssessmentPageHeader } from '../features/assessment/components/AssessmentPageHeader'

export function PublicAssessmentPage() {
    const { publicToken } = useParams()
    const navigate = useNavigate()

    const assessment = useAssessmentStore((state) => state.assessment)
    const attemptId = useAssessmentStore((state) => state.attemptId)
    const loadPublicAssessment = useAssessmentStore((state) => state.loadPublicAssessment)
    const startAttempt = useAssessmentStore((state) => state.startAttempt)
    const resetAssessment = useAssessmentStore((state) => state.resetAssessment)

    const [candidateName, setCandidateName] = useState('')
    const [candidateEmail, setCandidateEmail] = useState('')
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        resetAssessment()

        if (publicToken) {
            loadPublicAssessment(publicToken).catch((e) => {
                setError(e.message || 'Не удалось загрузить интервью')
            })
        }
    }, [publicToken, loadPublicAssessment, resetAssessment])

    useEffect(() => {
        if (attemptId) {
            navigate(`/assessment/attempt/${attemptId}`)
        }
    }, [attemptId, navigate])

    const start = async () => {
        setError(null)

        if (!candidateName.trim()) {
            setError('Введите имя')
            return
        }

        if (!candidateEmail.trim()) {
            setError('Введите email')
            return
        }

        try {
            await startAttempt(candidateName.trim(), candidateEmail.trim())
        } catch (e: any) {
            setError(e.message || 'Не удалось начать интервью')
        }
    }

    if (!assessment) {
        return (
            <Container maxWidth="sm">
                <Box py={4}>
                    <AssessmentPageHeader title="Публичное интервью" />
                    {error ? <Alert severity="error">{error}</Alert> : <Typography>Загрузка...</Typography>}
                </Box>
            </Container>
        )
    }

    return (
        <Container maxWidth="sm">
            <Box py={4}>
                <AssessmentPageHeader
                    title={assessment.title}
                    subtitle="Заполните данные кандидата, чтобы начать интервью"
                />

                <Paper
                    sx={{
                        p: 3,
                        borderRadius: 3,
                        border: '1px solid',
                        borderColor: 'divider',
                    }}
                >
                    <Stack spacing={2}>
                        {assessment.description && (
                            <Typography color="text.secondary">{assessment.description}</Typography>
                        )}

                        <Typography>
                            Позиция: {assessment.profession} · {assessment.level}
                        </Typography>

                        <Typography>Количество вопросов: {assessment.questionsCount}</Typography>

                        {assessment.voiceRequired && (
                            <Alert severity="info">Ответы принимаются только голосом</Alert>
                        )}

                        {assessment.cameraRequired && (
                            <Alert severity="warning">Для прохождения потребуется включить камеру</Alert>
                        )}

                        {assessment.recordingRequired && (
                            <Alert severity="warning">Интервью будет записываться</Alert>
                        )}

                        {error && <Alert severity="error">{error}</Alert>}

                        <TextField
                            label="Ваше имя"
                            value={candidateName}
                            onChange={(event) => setCandidateName(event.target.value)}
                            fullWidth
                        />

                        <TextField
                            label="Email"
                            value={candidateEmail}
                            onChange={(event) => setCandidateEmail(event.target.value)}
                            fullWidth
                        />

                        <Button variant="contained" onClick={start}>
                            Начать интервью
                        </Button>
                    </Stack>
                </Paper>
            </Box>
        </Container>
    )
}