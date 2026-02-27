"""
English to French Translator using Hugging Face Transformers.

This program takes an English word or sentence and translates it to French
using the Helsinki-NLP/opus-mt-en-fr model from Hugging Face.
"""

import argparse
import sys

from transformers import pipeline


def translate_english_to_french(text: str) -> str:
    """Translate English text to French using Helsinki-NLP/opus-mt-en-fr model.

    Args:
        text: English text to translate.

    Returns:
        Translated French text.
    """
    translator = pipeline("translation_en_to_fr", model="Helsinki-NLP/opus-mt-en-fr")
    result = translator(text)
    return result[0]["translation_text"]


def main() -> None:
    """Main entry point for the translator CLI."""
    parser = argparse.ArgumentParser(
        description="Translate English words/sentences to French using a Hugging Face model."
    )
    parser.add_argument(
        "text",
        nargs="?",
        type=str,
        help="English text to translate to French.",
    )
    parser.add_argument(
        "--interactive",
        action="store_true",
        help="Run in interactive mode for continuous translations.",
    )

    args = parser.parse_args()

    if args.interactive:
        print("English to French Translator (Interactive Mode)")
        print("Type 'quit' or 'exit' to stop.\n")
        while True:
            try:
                text = input("Enter English text: ").strip()
            except (EOFError, KeyboardInterrupt):
                print("\nGoodbye!")
                break
            if text.lower() in ("quit", "exit"):
                print("Goodbye!")
                break
            if not text:
                continue
            translation = translate_english_to_french(text)
            print(f"French: {translation}\n")
    elif args.text:
        translation = translate_english_to_french(args.text)
        print(f"French: {translation}")
    else:
        parser.print_help()
        sys.exit(1)


if __name__ == "__main__":
    main()
