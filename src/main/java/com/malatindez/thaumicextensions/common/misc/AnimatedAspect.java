package com.malatindez.thaumicextensions.common.misc;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.Aspect;
import java.util.ArrayList;
/*

for i in listdir():
    rename(i,i.replace("_delay-0.04s",''))

for i in listdir():
    img = Image.open(i)
    img = img.convert('RGBA')
    datas = img.getdata()
    newData = []
    for item in datas:
        if item[0] <= 30 and item[1] <= 30 and item[2] <= 70:
            newData.append((0,0,0,0))
        else:
            newData.append(item)
    img.putdata(newData)
    img.save(i,'PNG')


from math import ceil, sqrt

for i in listdir(): # transform to circle
    img = Image.open(i)
    img = img.convert('RGBA')
    datas = img.getdata()
    newData = []
    size = img.size[0]
    for j, item in enumerate(datas):
        if ceil(sqrt(((j % size)-size/2)**2 + (ceil(j / size)-size/2)**2)) > size/2:
            newData.append((0,0,0,0))
        else:
            newData.append(item)
    img.putdata(newData)
    img.save(i,'PNG')

for i in listdir():
    im = Image.open(i)
    im1 = im.resize((64,64))
    im1.save(i)
 */
public class AnimatedAspect extends Aspect {
    final ArrayList<ResourceLocation> images;
    public AnimatedAspect(String tag, int color, Aspect[] components, ArrayList<ResourceLocation> images, int blend) {
        super(tag, color, components, images.get(0), blend);
        this.images = images;
    }
    @Override
    public ResourceLocation getImage() {
        double pic = (System.currentTimeMillis()) % ((int)(1000.0f / 30.0f * images.size())); // 30 frames per second

        return images.get((int)(pic / 1000 * 30));
    }
}
