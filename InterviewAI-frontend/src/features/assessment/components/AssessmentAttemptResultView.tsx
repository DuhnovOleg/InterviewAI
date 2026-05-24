import { Box, Chip, Divider, Paper, Stack, Typography } from '@mui/material'
import type { AssessmentAttemptDetails } from '../types/assessment.types'

function Score({ label, value }: { label: string; value?: number | null }) {
    return (
        <Stack direction="row" justifyContent="space-between" spacing={2}>
            <Typography color="text.secondary">{label}</Typography>
            <Typography fontWeight={700}>{value ?? '—'}</Typography>
        </Stack>
    )
}

export function AssessmentAttemptResultView({ details }: { details: AssessmentAttemptDetails }) {
    return (
        <Paper sx={{ p: 3, borderRadius: 3, border: '1px solid', borderColor: 'divider' }}>
            <Stack spacing={2.5}>
                <Box>
                    <Typography variant="h5" fontWeight={800}>{details.assessmentTitle ?? 'Результат кандидата'}</Typography>
                    <Typography color="text.secondary" sx={{ mt: 0.5 }}>
                        {details.profession} {details.level ? `· ${details.level}` : ''}
                    </Typography>
                </Box>

                <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap>
                    <Chip label={`Кандидат: ${details.candidateName}`} />
                    {details.candidateEmail && <Chip label={details.candidateEmail} />}
                    <Chip label={`Статус: ${details.status}`} color={details.status === 'COMPLETED' ? 'success' : 'default'} />
                </Stack>

                <Divider />

                <Stack spacing={1}>
                    <Score label="Средний балл" value={details.overallScore} />
                    <Stack direction="row" justifyContent="space-between" spacing={2}>
                        <Typography color="text.secondary">Рекомендация</Typography>
                        <Typography fontWeight={700}>{details.recommendation ?? '—'}</Typography>
                    </Stack>
                </Stack>

                <Divider />

                <Typography variant="h6">Ответы кандидата</Typography>

                {details.answers.length === 0 && (
                    <Typography color="text.secondary">Ответов пока нет.</Typography>
                )}

                {details.answers.map((answer, index) => (
                    <Paper key={answer.id ?? index} variant="outlined" sx={{ p: 2, borderRadius: 2 }}>
                        <Stack spacing={1.25}>
                            <Typography variant="subtitle1" fontWeight={800}>Вопрос {index + 1}</Typography>
                            <Typography whiteSpace="pre-line">{answer.questionText}</Typography>
                            <Typography color="text.secondary" fontWeight={700}>Ответ</Typography>
                            <Typography whiteSpace="pre-line">{answer.answerText || '—'}</Typography>
                            <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap>
                                <Chip size="small" label={`Тип: ${answer.inputType}`} />
                                <Chip size="small" color="primary" label={`Балл: ${answer.overallScore ?? '—'}`} />
                                <Chip size="small" label={`Корректность: ${answer.correctnessScore ?? '—'}`} />
                                <Chip size="small" label={`Полнота: ${answer.completenessScore ?? '—'}`} />
                                <Chip size="small" label={`Релевантность: ${answer.relevanceScore ?? '—'}`} />
                            </Stack>
                            {answer.feedback && (
                                <Typography color="text.secondary" whiteSpace="pre-line">Фидбек: {answer.feedback}</Typography>
                            )}
                        </Stack>
                    </Paper>
                ))}
            </Stack>
        </Paper>
    )
}
