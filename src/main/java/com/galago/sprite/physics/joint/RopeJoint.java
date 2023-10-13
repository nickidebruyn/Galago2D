package com.galago.sprite.physics.joint;

import com.galago.sprite.physics.RigidBodyControl2D;
import com.jme3.math.Vector3f;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.geometry.Vector2;

/**
 * @author nickidebruyn
 */
public class RopeJoint extends PhysicsJoint {

  private Vector2 point1;
  private Vector2 point2;

  public RopeJoint(RigidBodyControl2D rigidBodyControl1, RigidBodyControl2D rigidBodyControl2) {
    super(rigidBodyControl1, rigidBodyControl2);

    point1 = rigidBodyControl1.getBody().getTransform().getTranslation();
    point2 = rigidBodyControl2.getBody().getTransform().getTranslation();

  }

  public RopeJoint(RigidBodyControl2D rigidBodyControl1, RigidBodyControl2D rigidBodyControl2, Vector3f pos1, Vector3f pos2) {
    super(rigidBodyControl1, rigidBodyControl2);

    point1 = new Vector2(pos1.x, pos1.y);
    point2 = new Vector2(pos2.x, pos2.y);

  }

  @Override
  protected Joint createJoint() {
    org.dyn4j.dynamics.joint.DistanceJoint ropeJoint = new org.dyn4j.dynamics.joint.DistanceJoint(rigidBodyControl1.getBody(), rigidBodyControl2.getBody(), point1, point2);
    return ropeJoint;
  }

  public void setLimits(float lowerLimit, float upperLimit) {
    ((org.dyn4j.dynamics.joint.DistanceJoint) joint).setLimitsEnabled(lowerLimit, upperLimit);

  }

  public void setLimitsEnabled(boolean enable) {
    ((org.dyn4j.dynamics.joint.DistanceJoint) joint).setLimitsEnabled(enable);

  }

  public float getUpperLimit() {
    return (float) ((org.dyn4j.dynamics.joint.DistanceJoint) joint).getUpperLimit();
  }

  public float getLowerLimit() {
    return (float) ((org.dyn4j.dynamics.joint.DistanceJoint) joint).getLowerLimit();
  }
}
