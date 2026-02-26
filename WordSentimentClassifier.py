"""
Word Sentiment Classifier

Given a word and two lists (one containing positive words and one containing
negative words), this program compares the input word against both lists and
determines whether the word is positive, negative, or neutral.
"""


# Predefined lists of positive and negative words
POSITIVE_WORDS = [
    "happy", "joy", "love", "excellent", "wonderful", "fantastic", "great",
    "amazing", "awesome", "brilliant", "cheerful", "delightful", "eager",
    "faithful", "generous", "grateful", "harmonious", "incredible",
    "jubilant", "kind", "lively", "magnificent", "noble", "optimistic",
    "peaceful", "pleasant", "radiant", "splendid", "terrific", "upbeat",
    "vibrant", "warm", "zealous", "beautiful", "brave", "calm", "charming",
    "creative", "elegant", "energetic", "friendly", "gentle", "heroic",
    "honest", "humble", "inspiring", "intelligent", "joyful", "loyal",
    "marvelous", "nice", "outstanding", "passionate", "polite", "reliable",
    "respectful", "sincere", "strong", "successful", "talented", "thankful",
    "thoughtful", "trustworthy", "victorious", "wise", "worthy",
]

NEGATIVE_WORDS = [
    "sad", "angry", "hate", "terrible", "horrible", "awful", "bad",
    "dreadful", "miserable", "painful", "ugly", "bitter", "cruel",
    "dangerous", "depressed", "disgusting", "evil", "fearful", "greedy",
    "harsh", "hostile", "ignorant", "jealous", "lazy", "mean", "nasty",
    "obnoxious", "pessimistic", "rude", "selfish", "spiteful", "toxic",
    "unhappy", "vicious", "wicked", "worthless", "aggressive", "annoying",
    "arrogant", "boring", "careless", "cowardly", "deceitful", "dishonest",
    "foolish", "gloomy", "grumpy", "hopeless", "impatient", "insecure",
    "irrational", "malicious", "moody", "narrow", "negative", "offensive",
    "pathetic", "resentful", "ruthless", "shameful", "stubborn", "tragic",
    "ungrateful", "violent", "vulgar", "weak",
]


def classify_word(word):
    """
    Classify a word as positive, negative, or neutral by searching
    through the positive and negative word lists.

    Args:
        word (str): The word to classify.

    Returns:
        str: 'positive', 'negative', or 'neutral'.
    """
    word_lower = word.strip().lower()

    if word_lower in POSITIVE_WORDS:
        return "positive"
    elif word_lower in NEGATIVE_WORDS:
        return "negative"
    else:
        return "neutral"


def main():
    print("=" * 50)
    print("       Word Sentiment Classifier")
    print("=" * 50)
    print()
    print("This program classifies a word as positive,")
    print("negative, or neutral based on predefined word lists.")
    print("Type 'quit' to exit.\n")

    while True:
        word = input("Enter a word: ").strip()

        if word.lower() == "quit":
            print("Goodbye!")
            break

        if not word:
            print("Please enter a valid word.\n")
            continue

        result = classify_word(word)

        if result == "positive":
            print(f"  The word '{word}' is POSITIVE!\n")
        elif result == "negative":
            print(f"  The word '{word}' is NEGATIVE!\n")
        else:
            print(f"  The word '{word}' is NEUTRAL (not found in either list).\n")


if __name__ == "__main__":
    main()
