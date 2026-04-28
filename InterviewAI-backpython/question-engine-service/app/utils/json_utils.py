import json
import re
from typing import Any


def extract_json_from_text(text: str) -> Any:
    cleaned = text.replace("```json", "").replace("```", "").strip()

    try:
        return json.loads(cleaned)
    except json.JSONDecodeError:
        pass

    match = re.search(r"(\{.*\}|\[.*\])", cleaned, re.DOTALL)
    if not match:
        raise ValueError("JSON not found in model response")

    return json.loads(match.group(1))