import {
    Alert,
    Box,
    Button,
    Container,
    Paper,
    Stack,
    TextField,
} from '@mui/material'
import { useState } from 'react'
import { createAssessment } from '../features/assessment/api/assessmentApi'
import { AssessmentPageHeader } from '../features/assessment/components/AssessmentPageHeader'
import { QuestionEditor } from '../features/assessment/components/QuestionEditor'
import { RequirementCheckboxes } from '../features/assessment/components/RequirementCheckboxes'
import type { AssessmentQuestion } from '../features/assessment/types/assessment.types'

export function CreateAssessmentPage() {
    const [title, setTitle] = useState('')
    const [description, setDescription] = useState('')
    const [profession, setProfession] = useState('')
    const [level, setLevel] = useState('middle')

    const [voiceRequired, setVoiceRequired] = useState(false)
    const [cameraRequired, setCameraRequired] = useState(false)
    const [recordingRequired, setRecordingRequired] = useState(false)

    const [questions, setQuestions] = useState<AssessmentQuestion[]>([
        {
            text: '',
            position: 1,
        },
    ])

    const [publicUrl, setPublicUrl] = useState<string | null>(null)
    const [error, setError] = useState<string | null>(null)
    const [loading, setLoading] = useState(false)

    const submit = async () => {
        setError(null)

        const cleanedQuestions = questions
            .map((question, index) => ({
                text: question.text.trim(),
                position: index + 1,
            }))
            .filter((question) => question.text.length > 0)

        if (!title.trim()) {
            setError('Введите название интервью')
            return
        }

        if (!profession.trim()) {
            setError('Введите профессию')
            return
        }

        if (cleanedQuestions.length === 0) {
            setError('Добавьте хотя бы один вопрос')
            return
        }

        setLoading(true)

        try {
            const data = await createAssessment({
                title: title.trim(),
                description: description.trim(),
                profession: profession.trim(),
                level: level.trim(),
                voiceRequired,
                cameraRequired,
                recordingRequired,
                questions: cleanedQuestions,
            })

            setPublicUrl(data.publicUrl)
        } catch (e: any) {
            console.error('CREATE ASSESSMENT ERROR', e?.response?.data ?? e)

            setError(
                e?.response?.data?.message ??
                e?.response?.data?.error ??
                'Не удалось создать интервью',
            )
        } finally {
            setLoading(false)
        }
    }

    const copyLink = async () => {
        if (!publicUrl) return
        await navigator.clipboard.writeText(publicUrl)
    }

    return (
        <Container maxWidth="md">
            <Box py={4}>
                <AssessmentPageHeader
                    title="Создание HR-интервью"
                    subtitle="Создайте список вопросов, настройте требования и получите публичную ссылку"
                />

                <Paper
                    sx={{
                        p: 3,
                        borderRadius: 3,
                        border: '1px solid',
                        borderColor: 'divider',
                    }}
                >
                    <Stack spacing={3}>
                        {error && <Alert severity="error">{error}</Alert>}

                        {publicUrl && (
                            <Alert
                                severity="success"
                                action={
                                    <Button color="inherit" size="small" onClick={copyLink}>
                                        Скопировать
                                    </Button>
                                }
                            >
                                Интервью создано: {publicUrl}
                            </Alert>
                        )}

                        <TextField
                            label="Название интервью"
                            value={title}
                            onChange={(event) => setTitle(event.target.value)}
                            fullWidth
                        />

                        <TextField
                            label="Описание"
                            value={description}
                            onChange={(event) => setDescription(event.target.value)}
                            fullWidth
                            multiline
                            minRows={3}
                        />

                        <TextField
                            label="Профессия"
                            value={profession}
                            onChange={(event) => setProfession(event.target.value)}
                            placeholder="Например: Java Backend Developer"
                            fullWidth
                        />

                        <TextField
                            label="Уровень"
                            value={level}
                            onChange={(event) => setLevel(event.target.value)}
                            placeholder="junior / middle / senior"
                            fullWidth
                        />

                        <RequirementCheckboxes
                            voiceRequired={voiceRequired}
                            cameraRequired={cameraRequired}
                            recordingRequired={recordingRequired}
                            onChange={(value) => {
                                setVoiceRequired(value.voiceRequired)
                                setCameraRequired(value.cameraRequired)
                                setRecordingRequired(value.recordingRequired)
                            }}
                        />

                        <QuestionEditor questions={questions} onChange={setQuestions} />

                        <Button variant="contained" disabled={loading} onClick={submit}>
                            Создать интервью
                        </Button>
                    </Stack>
                </Paper>
            </Box>
        </Container>
    )
}