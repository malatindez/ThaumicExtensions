package com.malatindez.thaumicextensions.client.lib;

import com.malatindez.thaumicextensions.client.render.misc.Animation;

public class Transformation {
    public float x, y, z;
    public float degreeX = 0, degreeY = 0, degreeZ = 0;
    public float scaleX = 1, scaleY = 1, scaleZ = 1;
    public float colorR = 1, colorG = 1, colorB = 1, colorA = 1;
    public Transformation() {}
    public Transformation(float x, float y, float z) {
        this.x = x; this.y = y; this.z = z;
    }
    public Transformation(float x, float y, float z,
                          float degreeX, float degreeY, float degreeZ) {
        this(x,y,z);
        this.degreeX = degreeX; this.degreeY = degreeY; this.degreeZ = degreeZ;
    }
    public Transformation(float x, float y, float z,
                          float degreeX, float degreeY, float degreeZ,
                          float scaleX, float scaleY, float scaleZ) {
        this(x,y,z,degreeX,degreeY,degreeZ);
        this.scaleX = scaleX; this.scaleY = scaleY; this.scaleZ = scaleZ;
    }
    public Transformation(float x, float y, float z,
                          float degreeX, float degreeY, float degreeZ,
                          float scaleX, float scaleY, float scaleZ,
                          float colorR, float colorG, float colorB, float colorA) {
        this(x,y,z,degreeX,degreeY,degreeZ,scaleX,scaleY,scaleZ);
        this.colorR = colorR; this.colorG = colorG; this.colorB = colorB; this.colorA = colorA;
    }

    public Transformation(Transformation transform) {
        x =  transform.x; y = transform.y; z = transform.z;
        degreeX = transform.degreeX; degreeY = transform.degreeY; degreeZ = transform.degreeZ;
        scaleX = transform.scaleX; scaleY = transform.scaleY; scaleZ = transform.scaleZ;
        colorR = transform.colorR; colorG = transform.colorG; colorB = transform.colorB; colorA = transform.colorA;
    }
}
