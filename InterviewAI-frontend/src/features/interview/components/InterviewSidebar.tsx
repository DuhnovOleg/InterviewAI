import HistoryIcon from '@mui/icons-material/History'
import LogoutIcon from '@mui/icons-material/Logout'
import RefreshIcon from '@mui/icons-material/Refresh'
import {
    Box,
    Button,
    Divider,
    List,
    ListItemButton,
    ListItemText,
    Paper,
    Stack,
    Typography,
} from '@mui/material'
import type { HistoryItem } from '../types/interview.types'

type Props = {
    username?: string
    isAuthenticated: boolean
    history: HistoryItem[]
    onLogout: () => void
    onReset: () => void
    onOpenReport: (sessionId: string) => void
}

export function InterviewSidebar({
                                     username,
                                     isAuthenticated,
                                     history,
                                     onLogout,
                                     onReset,
                                     onOpenReport,
                                 }: Props) {
    return (
        <Paper
            elevation={0}
            sx={{
                width: 320,
                height: '100vh',
                borderRight: '1px solid',
                borderColor: 'divider',
                bgcolor: '#12151c',
                display: 'flex',
                flexDirection: 'column',
            }}
        >
            <Box sx={{ p: 2 }}>
                <Typography variant="h6" fontWeight={700}>
                    Interview AI
                </Typography>
                <Typography variant="body2" color="text.secondary">
                    Тренажер собеседований
                </Typography>
            </Box>

            <Box sx={{ px: 2, pb: 2 }}>
                <Button fullWidth variant="contained" startIcon={<RefreshIcon />} onClick={onReset}>
                    Новый диалог
                </Button>
            </Box>

            <Divider />

            <Box sx={{ p: 2 }}>
                {isAuthenticated ? (
                    <>
                        <Typography variant="subtitle2" color="text.secondary">
                            Пользователь
                        </Typography>
                        <Typography variant="body1" fontWeight={600}>
                            {username}
                        </Typography>
                    </>
                ) : (
                    <>
                        <Typography variant="body2" color="text.secondary">
                            Гостевой режим
                        </Typography>
                    </>
                )}
            </Box>

            {isAuthenticated && (
                <>
                    <Divider />
                    <Box sx={{ p: 2, flex: 1, overflow: 'auto' }}>
                        <Stack direction="row" alignItems="center" spacing={1} sx={{ mb: 1 }}>
                            <HistoryIcon fontSize="small" />
                            <Typography variant="subtitle1">Последние интервью</Typography>
                        </Stack>

                        <List dense disablePadding>
                            {history.length === 0 && (
                                <Typography variant="body2" color="text.secondary">
                                    Пока нет сохраненных интервью
                                </Typography>
                            )}

                            {history.slice(0, 5).map((item) => (
                                <ListItemButton
                                    key={item.sessionId}
                                    sx={{
                                        mb: 1,
                                        borderRadius: 2,
                                        alignItems: 'flex-start',
                                        border: '1px solid',
                                        borderColor: 'divider',
                                    }}
                                    onClick={() => onOpenReport(item.sessionId)}
                                >
                                    <ListItemText
                                        primary={`${item.profession} • ${item.level}`}
                                        secondary={
                                            <>
                                                <Typography component="span" variant="body2" color="text.secondary">
                                                    Балл: {item.averageScore ?? '—'}
                                                </Typography>
                                                <br />
                                                <Typography component="span" variant="caption" color="text.secondary">
                                                    {new Date(item.createdAt).toLocaleString()}
                                                </Typography>
                                            </>
                                        }
                                    />
                                </ListItemButton>
                            ))}
                        </List>
                    </Box>

                    <Divider />

                    <Box sx={{ p: 2 }}>
                        <Button fullWidth variant="outlined" color="inherit" startIcon={<LogoutIcon />} onClick={onLogout}>
                            Выйти
                        </Button>
                    </Box>
                </>
            )}
        </Paper>
    )
}