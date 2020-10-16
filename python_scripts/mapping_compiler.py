from os import listdir
from os.path import isfile, join
from PIL import Image
import rpack
import math
import json
import sys
import threading
import datetime
import time
folder = ""
filename = ""
if len(sys.argv) >= 3:
    folder = sys.argv[1]
    filename = sys.argv[2]
else:
    folder = input("Enter folder name: ")
    filename = input("Enter output filename: ")
    
if not folder.endswith("\\"):
    folder += "\\"
    
names = [f for f in listdir(folder) if isfile(join(folder, f)) and (f.endswith(".png") or f.endswith(".jpg"))]

images = []
for i in range(len(names)):
    s = "Loading {}, {}/{}".format(names[i], i + 1, len(names))
    s += " " * (84 - len(s))
    print(s, end="\r")
    images.append(Image.open(folder + names[i]))

print("\nCalculating each image position...")
print(time.time())
rectangles = [i.size for i in images]
positions = rpack.pack(rectangles)
size = rpack.enclosing_size(rectangles, positions)
print(time.time())
print("\nPositions calculated! Creating a new image with size {} by {} pixels.".format(size[0], size[1]))

output_file = Image.new("RGBA", size)


for i in range(len(positions)):
    s = "Putting {} in final image, {}/{}".format(names[i].replace(".png","").replace(".jpg",""), i + 1, len(names))
    s += " " * (84 - len(s))
    print(s, end="\r")
    output_file.paste(images[i], positions[i])

print("\nSaving the image...")
output_file.save(filename + ".png")
print("Image saved!")
d = dict()
d["resource_domain"] = "thaumicextensions"
d["resource_path"] = "textures/gui/" + filename + ".png"
d["size"] = [size[0], size[1]]
print("Generating json file...")
for i in range(len(names)):
    f = dict()
    f["texFrom"] = [positions[i][0], positions[i][1]]
    f["iconSize"] = [rectangles[i][0], rectangles[i][1]]
    d[names[i].replace(".png","").replace(".jpg","")] = f
print("Saving the json file...")
open(filename + ".json", 'w').write(json.dumps(d))
print