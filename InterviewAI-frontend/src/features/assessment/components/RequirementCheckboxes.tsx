import { Checkbox, FormControlLabel, Stack, Typography } from '@mui/material'

type RequirementsValue = {
    voiceRequired: boolean
    cameraRequired: boolean
    recordingRequired: boolean
}

type Props = RequirementsValue & {
    onChange: (value: RequirementsValue) => void
}

export function RequirementCheckboxes({
                                          voiceRequired,
                                          cameraRequired,
                                          recordingRequired,
                                          onChange,
                                      }: Props) {
    return (
        <Stack spacing={1}>
            <Typography variant="h6">Требования</Typography>

            <FormControlLabel
                control={
                    <Checkbox
                        checked={voiceRequired}
                        onChange={(event) =>
                            onChange({
                                voiceRequired: event.target.checked,
                                cameraRequired,
                                recordingRequired,
                            })
                        }
                    />
                }
                label="Обязательно отвечать только голосом"
            />

            <FormControlLabel
                control={
                    <Checkbox
                        checked={cameraRequired}
                        onChange={(event) =>
                            onChange({
                                voiceRequired,
                                cameraRequired: event.target.checked,
                                recordingRequired,
                            })
                        }
                    />
                }
                label="Обязательно включить видеокамеру"
            />

            <FormControlLabel
                control={
                    <Checkbox
                        checked={recordingRequired}
                        onChange={(event) =>
                            onChange({
                                voiceRequired,
                                cameraRequired,
                                recordingRequired: event.target.checked,
                            })
                        }
                    />
                }
                label="Сохранять запись интервью"
            />
        </Stack>
    )
}