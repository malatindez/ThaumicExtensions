from os import listdir, system, scandir
from os.path import isfile, join
import sys
default_folder = "..\\src\\"
if len(sys.argv) >= 2:
    default_folder = sys.argv[1]
def f(current_directory):
    with scandir(current_directory) as entries:
        for entry in entries:
            if not isfile(entry):
                f(current_directory + entry.name + "\\")
            elif entry.name.lower().endswith(".json"):
                print("Refactoring " + entry.name)
                system("python texture_mapping\\json_formatter.py {}".format(current_directory + entry.name))

f(default_folder)