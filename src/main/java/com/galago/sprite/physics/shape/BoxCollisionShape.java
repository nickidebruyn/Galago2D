package com.galago.sprite.physics.shape;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import java.io.IOException;
import java.util.UUID;

import org.dyn4j.geometry.Rectangle;

/**
 *
 * @author nickidebruyn
 */
public class BoxCollisionShape extends CollisionShape {

    private float width;
    private float height;

    public BoxCollisionShape() {
        width = 10;
        height = 10;
    }

    /**
     * creates a collision box from the given size
     */
    public BoxCollisionShape(float width, float height) {
        this.width = width;
        this.height = height;
        initShape();

    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(width, "width", 10);
        capsule.write(height, "height", 10);
    }

    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule capsule = im.getCapsule(this);
        this.width = capsule.readFloat("width", 10.0f);
        this.height = capsule.readFloat("height", 10.0f);
        initShape();
    }

    protected void createShape() {
        cShape = new Rectangle(width, height);
    }

}
