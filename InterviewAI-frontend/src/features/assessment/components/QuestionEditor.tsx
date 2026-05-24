import AddIcon from '@mui/icons-material/Add'
import DeleteIcon from '@mui/icons-material/Delete'
import { Button, IconButton, Stack, TextField, Typography } from '@mui/material'
import type { AssessmentQuestion } from '../types/assessment.types'

type Props = {
    questions: AssessmentQuestion[]
    onChange: (questions: AssessmentQuestion[]) => void
}

export function QuestionEditor({ questions, onChange }: Props) {
    const updateQuestion = (index: number, text: string) => {
        onChange(
            questions.map((question, currentIndex) =>
                currentIndex === index ? { ...question, text } : question,
            ),
        )
    }

    const addQuestion = () => {
        onChange([
            ...questions,
            {
                text: '',
                position: questions.length + 1,
            },
        ])
    }

    const removeQuestion = (index: number) => {
        const nextQuestions = questions
            .filter((_, currentIndex) => currentIndex !== index)
            .map((question, currentIndex) => ({
                ...question,
                position: currentIndex + 1,
            }))

        onChange(nextQuestions)
    }

    return (
        <Stack spacing={2}>
            <Typography variant="h6">Вопросы интервью</Typography>

            {questions.map((question, index) => (
                <Stack key={index} direction="row" spacing={1} alignItems="flex-start">
                    <TextField
                        fullWidth
                        multiline
                        minRows={2}
                        label={`Вопрос ${index + 1}`}
                        value={question.text}
                        onChange={(event) => updateQuestion(index, event.target.value)}
                    />

                    <IconButton
                        color="error"
                        disabled={questions.length === 1}
                        onClick={() => removeQuestion(index)}
                    >
                        <DeleteIcon />
                    </IconButton>
                </Stack>
            ))}

            <Button startIcon={<AddIcon />} variant="outlined" onClick={addQuestion}>
                Добавить вопрос
            </Button>
        </Stack>
    )
}