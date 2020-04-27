package com.galago.sprite.physics;

import org.dyn4j.geometry.Vector2;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

/**
 * 
 * @author nickidebruyn
 */
public class Converter {

    public static Vector3f toVector3f(final Vector2 from) {
        return new Vector3f(toFloat(from.x), toFloat(from.y), 0);
    }

    public static float toFloat(final double from) {
        // return new FloatingDecimal(from).floatValue();
        return (float) from;
    }

    public static Vector2 toVector2(final Vector3f from) {
        return new Vector2(from.getX(), from.getY());
    }

    public static Vector2 toVector2(final Vector2f from) {
        return new Vector2(from.x, from.y);
    }

    public static Vector3f[] toVector3f(final Vector2[] vertices) {
        final Vector3f[] vectors = new Vector3f[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            vectors[i] = toVector3f(vertices[i]);
        }
        return vectors;
    }

}
