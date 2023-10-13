package com.galago.sprite.physics.shape;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Vector2f;
import org.dyn4j.geometry.Link;
import org.dyn4j.geometry.Vector2;

import java.io.IOException;

/**
 * @author nickidebruyn
 */
public class LinkCollisionShape extends CollisionShape {

  private Vector2f point1;
  private Vector2f point2;

  public LinkCollisionShape() {

  }

  /**
   * creates a link collision shape with 2 points
   */
  public LinkCollisionShape(Vector2f point1, Vector2f point2) {
    this.point1 = point1;
    this.point2 = point2;

    initShape();
  }

  public Vector2f getPoint1() {
    return point1;
  }

  public Vector2f getPoint2() {
    return point2;
  }

  public void updateShape(Vector2f point1, Vector2f point2) {
    this.point1 = point1;
    this.point2 = point2;
  }

  public void write(JmeExporter ex) throws IOException {
    super.write(ex);
    OutputCapsule capsule = ex.getCapsule(this);

  }

  public void read(JmeImporter im) throws IOException {
    super.read(im);
    InputCapsule capsule = im.getCapsule(this);

    initShape();
  }

  protected void createShape() {
    if (point1 == null || point2 == null) {
      throw new RuntimeException("Link collision shape could not be created, p1 or p2 is null");

    }
    this.cShape = new Link(new Vector2(point1.x, point1.y), new Vector2(point2.x, point2.y));

  }
}
