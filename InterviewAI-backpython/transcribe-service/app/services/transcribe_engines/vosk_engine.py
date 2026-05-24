import json
import wave
from pathlib import Path

from app.config import settings


class VoskEngine:
    name = "vosk"

    def __init__(self):
        try:
            from vosk import Model, SetLogLevel
        except ImportError as exc:
            raise RuntimeError(
                "Vosk is not installed. Install it with: pip install vosk"
            ) from exc

        SetLogLevel(-1)
        self.model = Model(settings.vosk_model_path)

    def transcribe(self, wav_path: Path, language_hint: str | None = None) -> str:
        try:
            from vosk import KaldiRecognizer
        except ImportError as exc:
            raise RuntimeError(
                "Vosk is not installed. Install it with: pip install vosk"
            ) from exc

        parts: list[str] = []

        with wave.open(str(wav_path), "rb") as wf:
            if wf.getnchannels() != 1:
                raise ValueError("Vosk expects mono WAV")

            if wf.getsampwidth() != 2:
                raise ValueError("Vosk expects 16-bit PCM WAV")

            recognizer = KaldiRecognizer(self.model, wf.getframerate())
            recognizer.SetWords(True)

            while True:
                data = wf.readframes(4000)

                if not data:
                    break

                if recognizer.AcceptWaveform(data):
                    result = json.loads(recognizer.Result())
                    text = result.get("text", "").strip()

                    if text:
                        parts.append(text)

            final_result = json.loads(recognizer.FinalResult())
            final_text = final_result.get("text", "").strip()

            if final_text:
                parts.append(final_text)

        return " ".join(parts).strip()