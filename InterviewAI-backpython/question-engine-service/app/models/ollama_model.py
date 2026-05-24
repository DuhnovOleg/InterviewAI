from dataclasses import dataclass


@dataclass(frozen=True)
class OllamaModelProfile:
    name: str
    num_ctx: int
    num_predict: int
    temperature: float
    top_p: float
    timeout_seconds: float


MODEL_PROFILES: dict[str, OllamaModelProfile] = {
    "qwen3_8b": OllamaModelProfile(
        name="qwen3:8b",
        num_ctx=8192,
        num_predict=2048,
        temperature=0.1,
        top_p=0.9,
        timeout_seconds=90.0,
    ),
    "ministral_3_8b": OllamaModelProfile(
        name="ministral-3:8b",
        num_ctx=8192,
        num_predict=2048,
        temperature=0.1,
        top_p=0.9,
        timeout_seconds=90.0,
    ),
    "mistral_7b_instruct_v03": OllamaModelProfile(
        name="mistral:7b-instruct-v0.3-q8_0",
        num_ctx=8192,
        num_predict=2048,
        temperature=0.1,
        top_p=0.9,
        timeout_seconds=90.0,
    ),
    "granite_33_8b": OllamaModelProfile(
        name="granite3.3:8b",
        num_ctx=8192,
        num_predict=2048,
        temperature=0.1,
        top_p=0.9,
        timeout_seconds=90.0,
    ),
    "deepseek_r1_qwen_7b": OllamaModelProfile(
        name="deepseek-r1:7b",
        num_ctx=8192,
        num_predict=2048,
        temperature=0.1,
        top_p=0.9,
        timeout_seconds=120.0,
    ),
    "phi4_mini": OllamaModelProfile(
        name="phi4-mini",
        num_ctx=8192,
        num_predict=2048,
        temperature=0.1,
        top_p=0.9,
        timeout_seconds=60.0,
    ),
    "llama31_8b": OllamaModelProfile(
        name="llama3.1:8b-instruct-q8_0",
        num_ctx=8192,
        num_predict=2048,
        temperature=0.1,
        top_p=0.9,
        timeout_seconds=90.0,
    ),
    "gemma3_4b": OllamaModelProfile(
        name="gemma3:4b",
        num_ctx=8192,
        num_predict=2048,
        temperature=0.1,
        top_p=0.9,
        timeout_seconds=60.0,
    ),
    "mistral_nemo_12b": OllamaModelProfile(
        name="mistral-nemo:12b-instruct-2407-q4_K_M",
        num_ctx=4096,
        num_predict=2048,
        temperature=0.1,
        top_p=0.9,
        timeout_seconds=150.0,
    ),
}