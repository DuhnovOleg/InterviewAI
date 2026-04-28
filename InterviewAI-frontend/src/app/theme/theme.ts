import { createTheme } from '@mui/material/styles'

export const appTheme = createTheme({
    palette: {
        mode: 'dark',
        background: {
            default: '#0f1115',
            paper: '#171a21',
        },
        primary: {
            main: '#8ab4ff',
        },
        secondary: {
            main: '#7c4dff',
        },
        text: {
            primary: '#e8eaed',
            secondary: '#aeb4be',
        },
        divider: '#2a2f3a',
    },
    shape: {
        borderRadius: 14,
    },
    typography: {
        fontFamily: `'Inter', 'Segoe UI', 'Roboto', 'Arial', sans-serif`,
        h4: {
            fontWeight: 700,
        },
        h5: {
            fontWeight: 700,
        },
        button: {
            textTransform: 'none',
            fontWeight: 600,
        },
    },
    components: {
        MuiPaper: {
            styleOverrides: {
                root: {
                    backgroundImage: 'none',
                },
            },
        },
        MuiButton: {
            styleOverrides: {
                root: {
                    borderRadius: 12,
                },
            },
        },
        MuiTextField: {
            defaultProps: {
                variant: 'outlined',
            },
        },
    },
})