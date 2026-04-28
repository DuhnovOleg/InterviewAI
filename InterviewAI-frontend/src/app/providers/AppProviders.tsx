import { CssBaseline, ThemeProvider } from '@mui/material'
import type { PropsWithChildren } from 'react'
import { BrowserRouter } from 'react-router-dom'
import { appTheme } from '../theme/theme'

export function AppProviders({ children }: PropsWithChildren) {
    return (
        <ThemeProvider theme={appTheme}>
            <CssBaseline />
            <BrowserRouter>{children}</BrowserRouter>
        </ThemeProvider>
    )
}