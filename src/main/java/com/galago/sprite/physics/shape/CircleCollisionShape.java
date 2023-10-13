package com.galago.sprite.physics.shape;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import org.dyn4j.geometry.Circle;

import java.io.IOException;
import java.util.UUID;

/**
 * @author nickidebruyn
 */
public class CircleCollisionShape extends CollisionShape {

  private float radius;

  public CircleCollisionShape() {
    radius = 5f;
  }

  /**
   * creates a collision circle from the given size
   */
  public CircleCollisionShape(float radius) {
    this.radius = radius;
    initShape();
  }

  public void write(JmeExporter ex) throws IOException {
    super.write(ex);
    OutputCapsule capsule = ex.getCapsule(this);
    capsule.write(radius, "radius", 5f);
  }

  public void read(JmeImporter im) throws IOException {
    super.read(im);
    InputCapsule capsule = im.getCapsule(this);
    this.radius = capsule.readFloat("radius", 5.0f);
    initShape();
  }

  protected void createShape() {
    cShape = new Circle(radius);
  }

}
