package com.malatindez.thaumicextensions.client.render.misc;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import org.lwjgl.opengl.GL11;
import sun.java2d.pipe.SpanShapeRenderer;

import java.util.*;

public class Animation {
    enum Axis {
        x,
        y,
        z
    }

    public static class SimpleAnimation {
        int phase = 0;
        // Phases by order

        // 1 - modifying x, y, z coordinates,
        // which will be used in glTransformMatrix

        // 2 - rotating object around itself
        // You can use glRotateMatrix freely

        // 3 - scaling object
        // You can use glScaleMatrix freely

        // Other phases mean animation is broken
        // and therefore will be skipped in constructor
        // or exception will be thrown in addAnimation() function

        // Phase 1, modify coordinates
        // There should be no interaction with matrix
        void transform(float[] coordinates, double time2pi) {}
        // Phase 2, rotation
        // You can use here glRotateMatrix
        void rotate(double time2pi) {}
        // Phase 3, scaling
        // You can use here glScaleMatrix
        void scale(double time2pi) {}

        void modifyRotation() {}
        protected SimpleAnimation() {}
    }
    private ArrayList<SimpleAnimation> Phase1 = new ArrayList<>();
    private ArrayList<SimpleAnimation> Phase2 = new ArrayList<>();
    private ArrayList<SimpleAnimation> Phase3 = new ArrayList<>();
    Animation(SimpleAnimation[] animations) {
        for (SimpleAnimation animation : animations) {
            if(animation.phase == 1) {
                Phase1.add(animation);
            }
            else if(animation.phase == 2) {
                Phase2.add(animation);
            }
            else if(animation.phase == 3) {
                Phase3.add(animation);
            }
        }
    }
    public void addAnimation(SimpleAnimation animation) {
        if(animation.phase == 1) {
            Phase1.add(animation);
        }
        else if(animation.phase == 2) {
            Phase2.add(animation);
        }
        else if(animation.phase == 3) {
            Phase3.add(animation);
        }
        else {
            throw new IllegalArgumentException("Wrong animation phase");
        }
    }
    // You can get hash before putting animation in, through hashCode() function
    // returns true if animation was successfully deleted. Otherwise, returns false
    public boolean removeAnimation(int hash) {
        for(SimpleAnimation animation : Phase1) {
            if(animation.hashCode() == hash) {
                Phase1.remove(animation);
                return true;
            }
        }
        for(SimpleAnimation animation : Phase2) {
            if(animation.hashCode() == hash) {
                Phase2.remove(animation);
                return true;
            }
        }
        for(SimpleAnimation animation : Phase3) {
            if(animation.hashCode() == hash) {
                Phase3.remove(animation);
                return true;
            }
        }
        return false;
    }

    // Matrix should be already pushed on stack
    void RenderModel(float x, float y, float z, double time2pi) {
        float[] coordinates = new float[] {x,y,z};
        for(SimpleAnimation animation : Phase1) {
            animation.transform(coordinates, time2pi);
        }
        GL11.glTranslatef(coordinates[0], coordinates[1], coordinates[2]);
        for(SimpleAnimation animation : Phase2) {
            animation.rotate(time2pi);
        }
        for(SimpleAnimation animation : Phase3) {
            animation.scale(time2pi);
        }
    }


    public static class RotationAroundItself extends SimpleAnimation {

    }
    public static class RotationAroundCenter extends SimpleAnimation {

    }
    // Wave animation around axis through sine/cosine functions
    public static class Wave extends SimpleAnimation {
        Axis axis;
    }
}
