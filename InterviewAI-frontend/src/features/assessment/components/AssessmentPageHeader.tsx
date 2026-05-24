import ArrowBackIcon from '@mui/icons-material/ArrowBack'
import HomeIcon from '@mui/icons-material/Home'
import { Box, IconButton, Stack, Tooltip, Typography } from '@mui/material'
import { useNavigate } from 'react-router-dom'

type Props = {
    title: string
    subtitle?: string
    showBack?: boolean
}

export function AssessmentPageHeader({ title, subtitle, showBack = true }: Props) {
    const navigate = useNavigate()

    return (
        <Stack
            direction="row"
            alignItems="flex-start"
            justifyContent="space-between"
            spacing={2}
            sx={{ mb: 3 }}
        >
            <Stack direction="row" spacing={1.5} alignItems="flex-start">
                {showBack && (
                    <Tooltip title="Назад">
                        <IconButton onClick={() => navigate(-1)}>
                            <ArrowBackIcon />
                        </IconButton>
                    </Tooltip>
                )}

                <Box>
                    <Typography variant="h4" fontWeight={800}>
                        {title}
                    </Typography>

                    {subtitle && (
                        <Typography color="text.secondary" sx={{ mt: 0.5 }}>
                            {subtitle}
                        </Typography>
                    )}
                </Box>
            </Stack>

            <Tooltip title="На главную">
                <IconButton
                    color="primary"
                    onClick={() => navigate('/')}
                    sx={{
                        border: '1px solid',
                        borderColor: 'divider',
                    }}
                >
                    <HomeIcon />
                </IconButton>
            </Tooltip>
        </Stack>
    )
}