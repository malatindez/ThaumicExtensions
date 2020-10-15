from os import listdir
from os.path import isfile, join
from PIL import Image
import rpack
import math
import json

folder = input("Enter folder name: ")
if not folder.endswith("\\"):
    folder += "\\"
names = [f for f in listdir(folder) if isfile(join(folder, f)) and (f.endswith(".png") or f.endswith(".jpg"))]
images = [Image.open(folder + name) for name in names]
rectangles = [i.size for i in images]
positions = rpack.pack(rectangles)
size = rpack.enclosing_size(rectangles, positions)
size = [int(math.ceil(size[0])), int(math.ceil(size[1]))]
output_file = Image.new("RGBA", size)

for i in range(len(positions)):
    output_file.paste(images[i], positions[i])
filename = input("Enter output filename: ")
output_file.save(filename + ".png")

d = dict()
d["resource_domain"] = "thaumicextensions"
d["resource_path"] = "textures/gui/" + filename + ".png"
d["size"] = [size[0], size[1]]

for i in range(len(names)):
    f = dict()
    f["texFrom"] = [positions[i][0], positions[i][1]]
    f["iconSize"] = [rectangles[i][0], rectangles[i][1]]
    d[names[i].replace(".png","").replace(".jpg","")] = f
open(filename + ".json", 'w').write(json.dumps(d))