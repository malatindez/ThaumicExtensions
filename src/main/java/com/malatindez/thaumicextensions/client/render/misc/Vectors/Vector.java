package com.malatindez.thaumicextensions.client.render.misc.Vectors;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

@SuppressWarnings("UnusedReturnValue")
@SideOnly(Side.CLIENT)
public final class Vector {
    public static Vector3f mul(Vector3f left, Vector3f right, Vector3f dest) {
        if(dest == null) {
            return new Vector3f(left.x * right.x, left.y * right.y, left.z * right.z);
        }
        dest.set(left.x * right.x, left.y * right.y, left.z * right.z);
        return dest;
    }

    public static Vector3f div(Vector3f left, Vector3f right, Vector3f dest) {
        if(dest == null) {
            return new Vector3f(left.x / right.x, left.y / right.y, left.z / right.z);
        }
        dest.set(left.x / right.x, left.y / right.y, left.z / right.z);
        return dest;
    }

    public static Vector2f mul(Vector2f left, Vector2f right, Vector2f dest) {
        if(dest == null) {
            return new Vector2f(left.x * right.x, left.y * right.y);
        }
        dest.set(left.x * right.x, left.y * right.y);
        return dest;
    }


    public static Vector2f div(Vector2f left, Vector2f right, Vector2f dest) {
        if(dest == null) {
            return new Vector2f(left.x / right.x, left.y / right.y);
        }
        dest.set(left.x / right.x, left.y / right.y);
        return dest;
    }

    public static Vector4f mul(Vector4f left, Vector4f right, Vector4f dest) {
        if(dest == null) {
            return new Vector4f(left.x * right.x, left.y * right.y, left.z * right.z, left.w * right.w);
        }
        dest.set(left.x * right.x, left.y * right.y, left.z * right.z, left.w * right.w);
        return dest;
    }

    public static Vector4f div(Vector4f left, Vector4f right, Vector4f dest) {
        if(dest == null) {
            return new Vector4f(left.x / right.x, left.y / right.y, left.z / right.z, left.w / right.w);
        }
        dest.set(left.x / right.x, left.y / right.y, left.z / right.z, left.w / right.w);
        return dest;
    }
}
