package com.malatindez.thaumicextensions.client.render.misc;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import org.lwjgl.opengl.GL11;
import sun.java2d.pipe.SpanShapeRenderer;

import java.util.*;

import static java.lang.Math.sin;

public class Animation {
    enum Axis {
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

    public static class SimpleAnimation {
        protected byte priority = 0;
        // less priority -> closer to the end of calling list
        int phase = 0;
        // Phases by order

        // 1 - determining object position

        // 2 - rotating object around center
        // You can use glRotateMatrix freely

        // 3 - rotating object around itself
        // You can use glRotateMatrix freely

        // 4 - scaling object
        // You can use glScaleMatrix freely

        // Other phases mean animation is broken
        // and therefore will be skipped in constructor
        // or exception will be thrown in addAnimation() function

        // Phase 1, modify coordinates
        // There should be no interaction with matrix
        void transform(float[] coordinates, double time2pi) {}

        // after phase 1 there's translated matrix onto x, y, z coords of our object.
        // Phase 2, rotation around center
        // You can use here glRotateMatrix here
        void rotate(double time2pi) {}
        // After it matrix will be translated to -x, -y, -z so object will be rotated around the center of rotation
        // Phase 3, scaling
        // You can use here glScaleMatrix
        void scale(double time2pi) {}

        void modifyRotation() {}
        // this noise can be used for rotation animations
        protected float noise;
        // for correct noise initialization GenNoise() should be called at the end of a constructor
        protected void GenNoise() {
            noise = (float)this.hashCode() / 10000000.0f;
        }
        protected SimpleAnimation() {}
    }
    private ArrayList<SimpleAnimation> Phase1 = new ArrayList<>();
    private ArrayList<SimpleAnimation> Phase2 = new ArrayList<>();
    private ArrayList<SimpleAnimation> Phase3 = new ArrayList<>();
    private ArrayList<SimpleAnimation> Phase4 = new ArrayList<>();
    protected void sortAnimations(ArrayList<SimpleAnimation> animations) {
        Collections.sort(animations, new Comparator<SimpleAnimation>() {
            @Override
            public int compare(SimpleAnimation a, SimpleAnimation b) {
                return b.priority - a.priority;
            }
        });
    }

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
            else if(animation.phase == 4) {
                Phase4.add(animation);
            }
        }
        this.sortAnimations(Phase1);
        this.sortAnimations(Phase2);
        this.sortAnimations(Phase3);
        this.sortAnimations(Phase4);
    }
    public void addAnimation(SimpleAnimation animation) {
        if(animation.phase == 1) {
            Phase1.add(animation);
            this.sortAnimations(Phase1);
        }
        else if(animation.phase == 2) {
            Phase2.add(animation);
            this.sortAnimations(Phase2);
        }
        else if(animation.phase == 3) {
            Phase3.add(animation);
            this.sortAnimations(Phase3);
        }
        else if(animation.phase == 4) {
            Phase4.add(animation);
            this.sortAnimations(Phase4);
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
        for(SimpleAnimation animation : Phase4) {
            if(animation.hashCode() == hash) {
                Phase4.remove(animation);
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
        GL11.glTranslatef(-coordinates[0], -coordinates[1], -coordinates[2]);
        for(SimpleAnimation animation : Phase3) {
            animation.rotate(time2pi);
        }
        for(SimpleAnimation animation : Phase4) {
            animation.scale(time2pi);
        }
    }


    public static class RotationAroundItself extends SimpleAnimation {
        private Axis axis;
        private float speed;
        // speed determined in degrees per second
        RotationAroundItself(float speed, Axis axis) {
            this.speed = speed;
            this.axis = axis;
            this.GenNoise();
        }
        @Override
        void rotate(double time2pi) {
            GL11.glRotatef((float)(time2pi+noise) * speed,
                    axis == Axis.x ? 1.0f : 0.0f,
                    axis == Axis.y ? 1.0f : 0.0f,
                    axis == Axis.z ? 1.0f : 0.0f);
        }
        void setSpeed(float speed) {
            this.speed = speed;
        }
        float getSpeed() {
            return speed;
        }
    }
    public static class RotationAroundCenter extends SimpleAnimation {
        private Axis axis;

        private float speed;
        // speed determined in degrees per second
        RotationAroundCenter(float speed, Axis axis) {
            this.speed = speed;
            this.axis = axis;
            this.GenNoise();
            this.priority = -128;
        }
        void transform(float[] coordinates, double time2pi) {

        }
        void setSpeed(float speed) {
            this.speed = speed/180.0f * (float)Math.PI;
        }
        float getSpeed() {
            return speed * 180.0f / (float)Math.PI;
        }
    }
    // Wave animation around axis through sine/cosine functions
    public static class Wave extends SimpleAnimation {
        Axis axis;
    }
}
