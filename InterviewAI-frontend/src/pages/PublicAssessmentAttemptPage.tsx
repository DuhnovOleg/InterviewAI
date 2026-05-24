import MicIcon from '@mui/icons-material/Mic'
import VideocamIcon from '@mui/icons-material/Videocam'
import FiberManualRecordIcon from '@mui/icons-material/FiberManualRecord'
import {
    Alert,
    Box,
    Button,
    Chip,
    Container,
    Divider,
    Paper,
    Stack,
    TextField,
    Typography,
} from '@mui/material'
import { useState } from 'react'
import { useAssessmentStore } from '../app/store/assessmentStore'
import { VoiceRecorderButton } from '../features/interview/components/VoiceRecorderButton'

export function PublicAssessmentAttemptPage() {
    const assessment = useAssessmentStore((state) => state.assessment)
    const messages = useAssessmentStore((state) => state.messages)
    const isLoading = useAssessmentStore((state) => state.isLoading)
    const interviewComplete = useAssessmentStore((state) => state.interviewComplete)
    const resultPublicToken = useAssessmentStore((state) => state.resultPublicToken)
    const submitAnswer = useAssessmentStore((state) => state.submitAnswer)
    const submitVoiceAnswer = useAssessmentStore((state) => state.submitVoiceAnswer)

    const [answer, setAnswer] = useState('')

    const sendTextAnswer = async () => {
        if (!answer.trim()) {
            return
        }

        await submitAnswer(answer)
        setAnswer('')
    }

    return (
        <Container maxWidth="md">
            <Box
                sx={{
                    minHeight: '100vh',
                    py: { xs: 2, md: 4 },
                    display: 'flex',
                    alignItems: 'center',
                }}
            >
                <Paper
                    elevation={8}
                    sx={{
                        width: '100%',
                        minHeight: { xs: 'calc(100vh - 32px)', md: 720 },
                        p: { xs: 2, md: 4 },
                        borderRadius: 4,
                        bgcolor: 'background.paper',
                        border: '1px solid',
                        borderColor: 'divider',
                        display: 'flex',
                        flexDirection: 'column',
                    }}
                >
                    <Stack spacing={2.5} sx={{ height: '100%' }}>
                        <Box>
                            <Typography variant="h4" fontWeight={800}>
                                Прохождение интервью
                            </Typography>

                            <Typography color="text.secondary" sx={{ mt: 1 }}>
                                Отвечайте на вопросы последовательно. После ответа система покажет фидбек и следующий вопрос.
                            </Typography>
                        </Box>

                        <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap>
                            {assessment?.voiceRequired && (
                                <Chip
                                    icon={<MicIcon />}
                                    label="Только голосовые ответы"
                                    color="primary"
                                    variant="filled"
                                />
                            )}

                            {assessment?.cameraRequired && (
                                <Chip
                                    icon={<VideocamIcon />}
                                    label="Требуется камера"
                                    color="warning"
                                    variant="filled"
                                />
                            )}

                            {assessment?.recordingRequired && (
                                <Chip
                                    icon={<FiberManualRecordIcon />}
                                    label="Запись сохраняется"
                                    color="error"
                                    variant="filled"
                                />
                            )}
                        </Stack>

                        {assessment?.cameraRequired && (
                            <Alert severity="warning">
                                Сейчас камера отмечена как требование. Проверку камеры и запись видео добавим следующим шагом.
                            </Alert>
                        )}

                        <Divider />

                        <Box
                            sx={{
                                flex: 1,
                                overflowY: 'auto',
                                display: 'flex',
                                flexDirection: 'column',
                                gap: 2,
                                pr: 1,
                                py: 1,
                            }}
                        >
                            {messages.length === 0 ? (
                                <Box
                                    sx={{
                                        alignSelf: 'flex-start',
                                        maxWidth: '85%',
                                        px: 2,
                                        py: 1.5,
                                        borderRadius: 3,
                                        bgcolor: 'action.hover',
                                        border: '1px solid',
                                        borderColor: 'divider',
                                    }}
                                >
                                    <Typography color="text.secondary">
                                        Интервью загружается...
                                    </Typography>
                                </Box>
                            ) : (
                                messages.map((message, index) => {
                                    const isUser = message.role === 'user'

                                    return (
                                        <Box
                                            key={`${message.role}-${index}`}
                                            sx={{
                                                display: 'flex',
                                                justifyContent: isUser ? 'flex-end' : 'flex-start',
                                            }}
                                        >
                                            <Box
                                                sx={{
                                                    maxWidth: '85%',
                                                    px: 2,
                                                    py: 1.5,
                                                    borderRadius: 3,
                                                    bgcolor: isUser ? 'primary.main' : 'action.hover',
                                                    color: isUser ? 'primary.contrastText' : 'text.primary',
                                                    border: isUser ? 'none' : '1px solid',
                                                    borderColor: 'divider',
                                                    boxShadow: isUser ? 2 : 0,
                                                }}
                                            >
                                                <Typography
                                                    variant="caption"
                                                    sx={{
                                                        display: 'block',
                                                        mb: 0.75,
                                                        opacity: 0.75,
                                                        fontWeight: 700,
                                                    }}
                                                >
                                                    {isUser ? 'Ваш ответ' : 'Интервьюер'}
                                                </Typography>

                                                <Typography whiteSpace="pre-line" lineHeight={1.6}>
                                                    {message.text}
                                                </Typography>
                                            </Box>
                                        </Box>
                                    )
                                })
                            )}
                        </Box>

                        <Divider />

                        {!interviewComplete && (
                            <Box>
                                {!assessment?.voiceRequired && (
                                    <Stack spacing={1.5}>
                                        <TextField
                                            label="Ваш ответ"
                                            placeholder="Введите ответ на вопрос..."
                                            multiline
                                            minRows={3}
                                            maxRows={6}
                                            value={answer}
                                            onChange={(event) => setAnswer(event.target.value)}
                                            fullWidth
                                        />

                                        <Button
                                            variant="contained"
                                            disabled={isLoading || !answer.trim()}
                                            onClick={sendTextAnswer}
                                        >
                                            Отправить ответ
                                        </Button>
                                    </Stack>
                                )}

                                {assessment?.voiceRequired && (
                                    <Stack spacing={1.5} alignItems="center">
                                        <Typography color="text.secondary" textAlign="center">
                                            Нажмите на микрофон, запишите ответ и остановите запись.
                                        </Typography>

                                        <VoiceRecorderButton
                                            disabled={isLoading}
                                            onRecorded={submitVoiceAnswer}
                                        />
                                    </Stack>
                                )}

                                {!assessment?.voiceRequired && (
                                    <Box sx={{ mt: 2, display: 'flex', justifyContent: 'center' }}>
                                        <VoiceRecorderButton
                                            disabled={isLoading}
                                            onRecorded={submitVoiceAnswer}
                                        />
                                    </Box>
                                )}
                            </Box>
                        )}

                        {interviewComplete && (
                            <Alert severity="success">
                                Интервью завершено.
                                {resultPublicToken &&
                                    ` Ссылка на результат: ${window.location.origin}/assessment/result/${resultPublicToken}`}
                            </Alert>
                        )}
                    </Stack>
                </Paper>
            </Box>
        </Container>
    )
}