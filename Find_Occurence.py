#Author - Chetan Sharma
#Question - Write a program/function that checks for the presence of a given character in a string and prints out all the locations


def check_char(char):
    return len(char) != 1
 
def find_occurence_of_char(temp_string, temp_char):
 
    if not isinstance(temp_string, str) or not isinstance(temp_char, str):
        raise ValueError(f"temp_string and temp_char should be string")
 
    if check_char(temp_char):
        raise ValueError("temp_char can't be empty or it should a char")
 
    if len(temp_char) > len(temp_string):
        raise ValueError("char shouldn't have")
 
    for i, v in enumerate(temp_string.strip()):
        if temp_char == v:
            print(f"{temp_char} is present at {i}")
