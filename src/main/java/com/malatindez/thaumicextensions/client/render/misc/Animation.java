package com.malatindez.thaumicextensions.client.render.misc;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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
        double noise;
        protected SimpleAnimation(float speedX, float speedY, float speedZ, float radius) {
            this.type = Type.RotationAroundCenter;
            this.speed = speedX;
            this.speedY = speedY;
            this.speedZ = speedZ;
            this.offset = radius;
            this.noise = ThreadLocalRandom.current().nextDouble() * ThreadLocalRandom.current().nextInt(10000000);
        }
        protected SimpleAnimation(float speed, float offset, Type type, Axis axis) {
            this.speed = speed;
            this.offset = offset;
            this.type = type;
            this.axis = axis;
            this.noise = ThreadLocalRandom.current().nextDouble() * ThreadLocalRandom.current().nextInt(10000000);
        }
    }

    protected SimpleAnimation rotationAroundCenter = null;
    protected ArrayList<SimpleAnimation> rotationsAroundItself = new ArrayList<SimpleAnimation>();
    protected ArrayList<SimpleAnimation> waves = new ArrayList<SimpleAnimation>();
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
    

    void PushMatrix(float x, float y, float z, double noise) {
        PushMatrix(x, y, z,0,0,0, noise);
    }
    public static class Coordinates {
        float x,y,z;
        public Coordinates(float x, float y, float z) {
            this.x = x; this.y = y; this.z = z;
        }
    };
    Coordinates getModifiedCoordinates(float x, float y, float z, double noise) {
        double time = (((double)System.currentTimeMillis())) / 1000;
        x += 0.5f; //
        z += 0.5f; // Move model to the center of a block
        for(SimpleAnimation wave : waves) {
            float a = (float)sin((time*2*Math.PI + wave.noise + noise) / wave.speed) * wave.offset;
            if(wave.axis == Axis.x) { x += a; }
            else if(wave.axis == Axis.y) { y += a; }
            else if(wave.axis == Axis.z) { z += a; }
        }
        if(rotationAroundCenter != null) {
            double j = time + rotationAroundCenter.noise + noise;
            float l = (float) ((j * rotationAroundCenter.speed) % 360.0);
            float b = (float) ((j * rotationAroundCenter.speedY) % 360.0);
            float t = (float) ((j * rotationAroundCenter.speedZ) % 360.0);
            float radius = rotationAroundCenter.offset / (float) Math.sqrt(
                    rotationAroundCenter.speed == 0 ? 1 : 0 +
                            rotationAroundCenter.speedY == 0 ? 1 : 0 +
                            rotationAroundCenter.speedZ == 0 ? 1 : 0
            );
            float n = rotationAroundCenter.speed == 0 ? radius : 0;
            float m = rotationAroundCenter.speedY == 0 ? radius : 0;
            float v = rotationAroundCenter.speedZ == 0 ? radius : 0;
            // Holy fuck.
            x += n * cos(b) * cos(t) - m * cos(b) * sin(t) + v * sin(b);
            y += n * (sin(l) * sin(b) * cos(t) + cos(l) * sin(t)) +
                 m * (cos(l) * cos(t) - sin(l) * sin(b) * sin(t)) -
                 v * sin(l) * cos(b);
            z += n * (sin(l) * sin(t) -cos(l) * sin(b) * cos(t)) +
                    m * (cos(l) * sin(b) * sin(t) + sin(l) * cos(t)) +
                    v * cos(l) * cos(b);
        }
        return new Coordinates(x,y,z);
    }
    void PushMatrix(float x, float y, float z, float degreeX, float degreeY, float degreeZ, double noise) {
        double time = (((double)System.currentTimeMillis())) / 1000;
        x += 0.5f; //
        z += 0.5f; // Move model to the center of a block
        GL11.glPushMatrix();
        for(SimpleAnimation wave : waves) {
            float a = (float)sin((time*2*Math.PI + wave.noise + noise) / wave.speed) * wave.offset;
            if(wave.axis == Axis.x) { x += a; }
            else if(wave.axis == Axis.y) { y += a; }
            else if(wave.axis == Axis.z) { z += a; }
        }
        if(rotationAroundCenter != null) {
            double n =  time + rotationAroundCenter.noise  + noise;
            float a = (float) ((n * rotationAroundCenter.speed)  % 360.0);
            float b = (float) ((n * rotationAroundCenter.speedY)  % 360.0);
            float c = (float) ((n * rotationAroundCenter.speedZ)  % 360.0);
            GL11.glTranslatef(x,y,z);
            GL11.glRotatef(a, 1, 0, 0);
            GL11.glRotatef(b, 0, 1, 0);
            GL11.glRotatef(c, 0, 0, 1);
            float radius = rotationAroundCenter.offset / (float)Math.sqrt(
                    rotationAroundCenter.speed  == 0 ? 1 : 0 +
                    rotationAroundCenter.speedY == 0 ? 1 : 0 +
                    rotationAroundCenter.speedZ == 0 ? 1 : 0
            );
            GL11.glTranslatef(
                    rotationAroundCenter.speedZ == 0 ? 0 : (radius),
                    rotationAroundCenter.speed  == 0 ? 0 : (radius),
                    rotationAroundCenter.speedY == 0 ? 0 : (radius));
        } else {
            GL11.glTranslatef(x, y, z);
        }
        for(SimpleAnimation rotationAroundItself : rotationsAroundItself) {
            float a = (float)((time + rotationAroundItself.noise  + noise) * rotationAroundItself.speed % 360.0);
            if (rotationAroundItself.axis == Axis.x)      { GL11.glRotatef(a ,1,0,0); }
            else if (rotationAroundItself.axis == Axis.y) { GL11.glRotatef(a ,0,1,0); }
            else if (rotationAroundItself.axis == Axis.z) { GL11.glRotatef(a ,0,0,1); }
        }
        GL11.glRotatef(degreeX,1,0,0);
        GL11.glRotatef(degreeY,0,1,0);
        GL11.glRotatef(degreeZ,0,0,1);
    }

}
