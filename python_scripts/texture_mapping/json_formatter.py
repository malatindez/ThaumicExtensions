import json
import sys
filename = ""
if len(sys.argv) >= 2:
    filename = sys.argv[1]
else:
    filename = input("enter path to file(or file name)")
string = open(filename, "r").read()
string = string.replace("\n", "").replace(" ", "").replace("\t","")
square_brackets_flag = 0
quotation_flag = False
i = 0
offset = 0
while i < len(string):
    print("{}/{}".format(i + 1, len(string)), end="\r")
    if string[i] == '"':
        if(string[i-1] != '\\'):
            quotation_flag = not quotation_flag
    elif not quotation_flag:
        if string[i] == '[':
            square_brackets_flag += 1
        elif string[i] == ']':
            square_brackets_flag -= 1
        if square_brackets_flag <= 0:
            if string[i] == ":":
                string = string[:i+1] +  " " + string[i+1:]
            elif string[i] == ",":
                string = string[:i+1] + "\n"  + (" " * offset) + string[i+1:]
            elif string[i] == "{":
                offset += 1
                string = string[:i+1] + "\n" + (" " * offset) + string[i+1:]
                i += offset
            elif string[i] == "}":
                offset -= 1
                string = string[:i] + "\n" + (" " * offset)  + string[i:]
                i += offset + 1
        elif string[i] == ",":
            string = string[:i+1] +  " " + string[i+1:]
    i += 1
    
open(filename, "w").write(string)