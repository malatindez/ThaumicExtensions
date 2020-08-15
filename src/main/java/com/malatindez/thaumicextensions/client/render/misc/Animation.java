package com.malatindez.thaumicextensions.client.render.misc;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import org.lwjgl.opengl.GL11;
import sun.java2d.pipe.SpanShapeRenderer;

import java.util.*;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
@SideOnly(Side.CLIENT)
public class Animation {
    public enum Axis {
        x(0),
        y(1),
        z(2);

        private final int value;
        Axis(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }
    enum Type {
        RotationAroundCenter,
        RotationAroundItself,
        Wave
    }
    public static class SimpleAnimation {
        float speed;
        float offset;
        float speedY;
        float speedZ;
        Type type;
        Axis axis;
        float noise = 0;
        protected SimpleAnimation(float speedX, float speedY, float speedZ, float radius) {
            this.type = Type.RotationAroundCenter;
            this.speed = speedX;
            this.speedY = speedY;
            this.speedZ = speedZ;
            this.offset = radius;
        }
        protected SimpleAnimation(float speed, float offset, Type type, Axis axis) {
            this.speed = speed;
            this.offset = offset;
            this.type = type;
            this.axis = axis;
            this.noise = (float)this.hashCode() / 1000000.0f;
        }
    }

    protected SimpleAnimation rotationAroundCenter = null;
    protected ArrayList<SimpleAnimation> rotationsAroundItself = new ArrayList<>();
    protected ArrayList<SimpleAnimation> waves = new ArrayList<>();
    public Animation(SimpleAnimation[] animations) {
        if (animations == null) {
            return;
        }
        for (SimpleAnimation animation : animations) {
            switch(animation.type) {
                case RotationAroundCenter:
                    rotationAroundCenter = animation;
                    break;
                case RotationAroundItself:
                    rotationsAroundItself.add(animation);
                    break;
                case Wave:
                    waves.add(animation);
                    break;
            }
        }
    }
    void addAnimation(SimpleAnimation animation) {
        switch(animation.type) {
            case RotationAroundCenter:
                rotationAroundCenter = animation;
                break;
            case RotationAroundItself:
                rotationsAroundItself.add(animation);
                break;
            case Wave:
                waves.add(animation);
                break;
        }
    }
    // speed determined in degrees per second
    public static SimpleAnimation RotateAroundCenterAtRadius(float degreesPerSecondX, float degreesPerSecondY, float degreesPerSecondZ, float radius) {
        return new SimpleAnimation(degreesPerSecondX,degreesPerSecondY,degreesPerSecondZ,radius);
    }
    // speed determined in degrees per second
    public static SimpleAnimation RotateAroundItself(float degreesPerSecond, Axis axis) {
        return new SimpleAnimation(degreesPerSecond,0,Type.RotationAroundItself,axis);
    }
    // amplitude determined in meters, object will be going upside down on [-amplitude, +amplitude]
    // time determined in seconds, lambda time from -offset to +offset and backwards
    public static SimpleAnimation Wave(float amplitude, float time, Axis axis) {
        return new SimpleAnimation(time,amplitude,Type.Wave,axis);
    }
    

    void PushMatrix(float x, float y, float z, double time2pi) {
        GL11.glPushMatrix();
        for(SimpleAnimation wave : waves) {
            float a = (float)sin((time2pi + wave.noise) / wave.speed) * wave.offset;
            if(wave.axis == Axis.x) { x += a; }
            else if(wave.axis == Axis.y) { y += a; }
            else if(wave.axis == Axis.z) { z += a; }
        }
        for(SimpleAnimation rotationAroundItself : rotationsAroundItself) {
            float a = (float)sin((time2pi + rotationAroundItself.noise) / rotationAroundItself.speed);
            if (rotationAroundItself.axis == Axis.x)      { GL11.glRotatef(a * 180.0f ,1,0,0); }
            else if (rotationAroundItself.axis == Axis.y) { GL11.glRotatef(a * 180.0f ,1,0,0); }
            else if (rotationAroundItself.axis == Axis.z) { GL11.glRotatef(a * 180.0f ,1,0,0); }
        }
        if(rotationAroundCenter != null) {
            GL11.glTranslatef(
                    rotationAroundCenter.speed  == 0 ? x : (rotationAroundCenter.offset + x),
                    rotationAroundCenter.speedY == 0 ? y : (rotationAroundCenter.offset + y),
                    rotationAroundCenter.speedZ == 0 ? z : (rotationAroundCenter.offset + z));
            float sx = (float)sin((time2pi + rotationAroundCenter.noise)        / rotationAroundCenter.speed  / 360);
            float sy = (float)cos((time2pi + rotationAroundCenter.noise)        / rotationAroundCenter.speedY / 360);
            float sz = (float)sin((time2pi + 1.1f * rotationAroundCenter.noise) / rotationAroundCenter.speedZ / 360);
            GL11.glRotatef(sx * 180.0f ,1,0,0);
            GL11.glRotatef(sy * 180.0f ,0,1,0);
            GL11.glRotatef(sz * 180.0f ,0,0,1);
            GL11.glTranslatef(
                    rotationAroundCenter.speed  == 0 ? -x : (-rotationAroundCenter.offset - x),
                    rotationAroundCenter.speedY == 0 ? -y : (-rotationAroundCenter.offset - y),
                    rotationAroundCenter.speedZ == 0 ? -z : (-rotationAroundCenter.offset - z));
        } else {
            GL11.glTranslatef(x, y, z);
        }
        GL11.glPopMatrix();
    }
}
