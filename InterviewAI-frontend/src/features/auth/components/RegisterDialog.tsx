import { zodResolver } from '@hookform/resolvers/zod'
import {
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    Stack,
    TextField,
} from '@mui/material'
import { Controller, useForm } from 'react-hook-form'
import { z } from 'zod'
import { useAuthStore } from '../../../app/store/authStore'
import { ErrorText } from '../../../shared/components/ErrorText'

const schema = z.object({
    email: z.string().email('Введите корректный email'),
    username: z.string().min(3, 'Минимум 3 символа'),
    password: z
        .string()
        .min(8, 'Минимум 8 символов')
        .regex(/^(?=.*[A-Z])(?=.*[a-z])(?=.*\d).+$/, 'Пароль должен содержать верхний, нижний регистр и цифру'),
})

type FormValues = z.infer<typeof schema>

type Props = {
    open: boolean
    onClose: () => void
}

export function RegisterDialog({ open, onClose }: Props) {
    const register = useAuthStore((s) => s.register)
    const isLoading = useAuthStore((s) => s.isLoading)
    const error = useAuthStore((s) => s.error)
    const clearError = useAuthStore((s) => s.clearError)

    const { control, handleSubmit, reset } = useForm<FormValues>({
        resolver: zodResolver(schema),
        defaultValues: {
            email: '',
            username: '',
            password: '',
        },
    })

    const onSubmit = async (values: FormValues) => {
        try {
            await register(values)
            reset()
            onClose()
        } catch {
            // handled in store
        }
    }

    return (
        <Dialog
            open={open}
            onClose={() => {
                clearError()
                onClose()
            }}
            fullWidth
            maxWidth="xs"
        >
            <DialogTitle>Регистрация</DialogTitle>
            <DialogContent>
                <Stack spacing={2} sx={{ mt: 1 }}>
                    <Controller
                        name="email"
                        control={control}
                        render={({ field, fieldState }) => (
                            <TextField
                                {...field}
                                label="Email"
                                error={!!fieldState.error}
                                helperText={fieldState.error?.message}
                                fullWidth
                            />
                        )}
                    />

                    <Controller
                        name="username"
                        control={control}
                        render={({ field, fieldState }) => (
                            <TextField
                                {...field}
                                label="Имя пользователя"
                                error={!!fieldState.error}
                                helperText={fieldState.error?.message}
                                fullWidth
                            />
                        )}
                    />

                    <Controller
                        name="password"
                        control={control}
                        render={({ field, fieldState }) => (
                            <TextField
                                {...field}
                                label="Пароль"
                                type="password"
                                error={!!fieldState.error}
                                helperText={fieldState.error?.message}
                                fullWidth
                            />
                        )}
                    />

                    {error && <ErrorText>{error}</ErrorText>}
                </Stack>
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose}>Отмена</Button>
                <Button onClick={handleSubmit(onSubmit)} variant="contained" disabled={isLoading}>
                    Зарегистрироваться
                </Button>
            </DialogActions>
        </Dialog>
    )
}