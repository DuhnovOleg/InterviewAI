import { Typography } from '@mui/material'
import type { PropsWithChildren } from 'react'

export function ErrorText({ children }: PropsWithChildren) {
    return (
        <Typography color="error" variant="body2">
            {children}
        </Typography>
    )
}