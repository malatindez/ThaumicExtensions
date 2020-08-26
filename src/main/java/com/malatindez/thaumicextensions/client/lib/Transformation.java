package com.malatindez.thaumicextensions.client.lib;

import com.malatindez.thaumicextensions.client.render.misc.Animation;
import com.sun.istack.internal.NotNull;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class Transformation {
    public final Vector3f position = new Vector3f(0,0,0);
    public final Vector3f degree = new Vector3f(0,0,0);
    public final Vector3f scale = new Vector3f(1,1,1);
    public final Vector4f color = new Vector4f(1,1,1,1);
    public Transformation() {}
    public Transformation(float x, float y, float z) {
        this.position.set(x,y,z);
    }
    public Transformation(float x, float y, float z,
                          float degreeX, float degreeY, float degreeZ) {
        this(x, y, z);
        this.degree.set(degreeX, degreeY, degreeZ);
    }
    public Transformation(float x,       float y,       float z,
                          float degreeX, float degreeY, float degreeZ,
                          float scaleX,  float scaleY,  float scaleZ) {
        this(   x,       y,       z,
                degreeX, degreeY, degreeZ);
        this.scale.set(scaleX, scaleY, scaleZ);
    }
    public Transformation(float x,       float y,       float z,
                          float degreeX, float degreeY, float degreeZ,
                          float scaleX,  float scaleY,  float scaleZ,
                          float colorR,  float colorG,  float colorB, float colorA) {
        this(   x,       y,       z,
                degreeX, degreeY, degreeZ,
                scaleX,  scaleY,  scaleZ);
        this.color.set(colorR, colorG, colorB, colorA);
    }
    public Transformation(@NotNull final Vector3f position) {
        this.position.set(position);
    }
    public Transformation(@NotNull final Vector3f position, @NotNull final Vector3f degree) {
        this(position);
        this.degree.set(degree);
    }
    public Transformation(@NotNull final Vector3f position, @NotNull final Vector3f degree,
                          @NotNull final Vector3f scale) {
        this(position, degree);
        this.scale.set(scale);
    }
    public Transformation(@NotNull final Vector3f position, @NotNull final Vector3f degree,
                          @NotNull final Vector3f scale,    @NotNull final Vector4f color) {
        this(position, degree, scale);
        this.color.set(color);
    }
    public Transformation(@NotNull final Transformation transform) {
        this(transform.position, transform.degree, transform.scale, transform.color);
    }
}
