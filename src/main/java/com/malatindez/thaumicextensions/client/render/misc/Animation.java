package com.malatindez.thaumicextensions.client.render.misc;

import com.malatindez.thaumicextensions.client.lib.Transformation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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
        float amplitude;
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
            this.amplitude = radius;
            this.noise = ThreadLocalRandom.current().nextDouble() * ThreadLocalRandom.current().nextInt(10000000);
        }
        protected SimpleAnimation(float speed, float amplitude, Type type, Axis axis) {
            this.speed = speed;
            this.amplitude = amplitude;
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
    // time determined in seconds, lambda time from -amplitude to +amplitude and backwards
    public static SimpleAnimation Wave(float amplitude, float time, Axis axis) {
        return new SimpleAnimation(time,amplitude,Type.Wave,axis);
    }
    


    Vector3f getModifiedCoordinates(Transformation transform, double noise) {
        Matrix4f matrix = new Matrix4f();
        matrix.m00 = matrix.m11 = matrix.m22 = matrix.m33 = 1;
        double time = ((double)Minecraft.getSystemTime()) / 1000;
        for(SimpleAnimation wave : waves) {
            float a = (float)sin((time*2*Math.PI + wave.noise + noise) / wave.speed) * wave.amplitude;
            if(wave.axis == Axis.x) { transform.position.x += a; }
            else if(wave.axis == Axis.y) { transform.position.y += a; }
            else if(wave.axis == Axis.z) { transform.position.z += a; }
        }
        if(rotationAroundCenter != null) {
            double n =  time + rotationAroundCenter.noise  + noise;
            float a = (float) ((n * rotationAroundCenter.speed)  % 360.0);
            float b = (float) ((n * rotationAroundCenter.speedY)  % 360.0);
            float c = (float) ((n * rotationAroundCenter.speedZ)  % 360.0);
            matrix.translate(transform.position);
            matrix.rotate(a / 180.0f * (float)Math.PI, new Vector3f(1,0,0));
            matrix.rotate(b / 180.0f * (float)Math.PI, new Vector3f(0,1,0));
            matrix.rotate(c / 180.0f * (float)Math.PI, new Vector3f(0,0,1));
            float radius = rotationAroundCenter.amplitude / (float)Math.sqrt(
                    rotationAroundCenter.speed  == 0 ? 1 : 0 +
                            rotationAroundCenter.speedY == 0 ? 1 : 0 +
                            rotationAroundCenter.speedZ == 0 ? 1 : 0
            );
            matrix.translate( new Vector3f(
                    rotationAroundCenter.speedZ == 0 ? 0 : (radius),
                    rotationAroundCenter.speed  == 0 ? 0 : (radius),
                    rotationAroundCenter.speedY == 0 ? 0 : (radius)));
        }
        else {
            matrix.translate(new Vector3f(transform.position.x, transform.position.y, transform.position.z));
        }
        return new Vector3f(matrix.m30,matrix.m31,matrix.m32);
    }
    /**
     * Pushes matrix onto stack according to the current time & noise
     * @param transform - current object position, rotation, scale and color
     * @param noise - TileEntity noise, which will be used as unique offset for this object
     */
    void getMatrix(Transformation transform, double noise) {
        double time = ((double)Minecraft.getSystemTime()) / 1000;
        GL11.glPushMatrix();
        for(SimpleAnimation wave : waves) {
            float a = (float)sin((time*2*Math.PI + wave.noise + noise) / wave.speed) * wave.amplitude;
            if(wave.axis == Axis.x) { transform.position.x += a; }
            else if(wave.axis == Axis.y) { transform.position.y += a; }
            else if(wave.axis == Axis.z) { transform.position.z += a; }
        }
        if(rotationAroundCenter != null) {
            double n =  time + rotationAroundCenter.noise  + noise;
            float a = (float) ((n * rotationAroundCenter.speed)  % 360.0);
            float b = (float) ((n * rotationAroundCenter.speedY)  % 360.0);
            float c = (float) ((n * rotationAroundCenter.speedZ)  % 360.0);
            GL11.glTranslatef(transform.position.x, transform.position.y, transform.position.z);
            GL11.glRotatef(a, 1, 0, 0);
            GL11.glRotatef(b, 0, 1, 0);
            GL11.glRotatef(c, 0, 0, 1);
            float radius = rotationAroundCenter.amplitude / (float)Math.sqrt(
                    rotationAroundCenter.speed  == 0 ? 1 : 0 +
                    rotationAroundCenter.speedY == 0 ? 1 : 0 +
                    rotationAroundCenter.speedZ == 0 ? 1 : 0
            );
            GL11.glTranslatef(
                    rotationAroundCenter.speedZ == 0 ? 0 : (radius),
                    rotationAroundCenter.speed  == 0 ? 0 : (radius),
                    rotationAroundCenter.speedY == 0 ? 0 : (radius));
        } else {
            GL11.glTranslatef(transform.position.x, transform.position.y, transform.position.z);
        }
        for(SimpleAnimation rotationAroundItself : rotationsAroundItself) {
            float a = (float)((time + rotationAroundItself.noise  + noise) * rotationAroundItself.speed % 360.0);
            if (rotationAroundItself.axis == Axis.x)      { GL11.glRotatef(a ,1,0,0); }
            else if (rotationAroundItself.axis == Axis.y) { GL11.glRotatef(a ,0,1,0); }
            else if (rotationAroundItself.axis == Axis.z) { GL11.glRotatef(a ,0,0,1); }
        }
        GL11.glRotatef(transform.degree.x,1,0,0);
        GL11.glRotatef(transform.degree.y,0,1,0);
        GL11.glRotatef(transform.degree.z,0,0,1);
        GL11.glScalef(transform.scale.x, transform.scale.y, transform.scale.z);
        GL11.glColor4f(transform.color.x, transform.color.y, transform.color.z, transform.color.w);
    }

}
