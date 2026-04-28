import { Box, CircularProgress } from '@mui/material'

export function CenteredLoader() {
    return (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
            <CircularProgress />
        </Box>
    )
}