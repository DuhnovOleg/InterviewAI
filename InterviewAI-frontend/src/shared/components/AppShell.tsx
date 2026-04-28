import { Box } from '@mui/material'
import type { PropsWithChildren } from 'react'

export function AppShell({ children }: PropsWithChildren) {
    return (
        <Box
            sx={{
                minHeight: '100vh',
                bgcolor: 'background.default',
                color: 'text.primary',
            }}
        >
            {children}
        </Box>
    )
}