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
    password: z.string().min(1, 'Введите пароль'),
})

type FormValues = z.infer<typeof schema>

type Props = {
    open: boolean
    onClose: () => void
}

export function LoginDialog({ open, onClose }: Props) {
    const login = useAuthStore((s) => s.login)
    const isLoading = useAuthStore((s) => s.isLoading)
    const error = useAuthStore((s) => s.error)
    const clearError = useAuthStore((s) => s.clearError)

    const { control, handleSubmit, reset } = useForm<FormValues>({
        resolver: zodResolver(schema),
        defaultValues: {
            email: '',
            password: '',
        },
    })

    const onSubmit = async (values: FormValues) => {
        try {
            await login(values)
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
            <DialogTitle>Вход</DialogTitle>
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
                    Войти
                </Button>
            </DialogActions>
        </Dialog>
    )
}