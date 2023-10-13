package com.galago.sprite.physics.joint;

import com.galago.sprite.physics.Converter;
import com.galago.sprite.physics.RigidBodyControl2D;
import com.jme3.math.Vector3f;
import org.dyn4j.dynamics.joint.Joint;

/**
 * @author nickidebruyn
 */
public class DistanceJoint extends PhysicsJoint {

  protected Vector3f anchor1;
  protected Vector3f anchor2;

  public DistanceJoint(RigidBodyControl2D rigidBodyControl1, RigidBodyControl2D rigidBodyControl2, Vector3f anchor1, Vector3f anchor2) {
    super(rigidBodyControl1, rigidBodyControl2);
    this.anchor1 = anchor1;
    this.anchor2 = anchor2;
  }

  @Override
  protected Joint createJoint() {
    org.dyn4j.dynamics.joint.DistanceJoint distanceJoint = new org.dyn4j.dynamics.joint.DistanceJoint(rigidBodyControl1.getBody(), rigidBodyControl2.getBody(), Converter.toVector2(anchor1), Converter.toVector2(anchor2));
    return distanceJoint;
  }

  public void setDampingRatio(float ratio) {
    ((org.dyn4j.dynamics.joint.DistanceJoint) joint).setSpringDampingRatio(ratio);
  }

  public float getDampingRatio() {
    return Converter.toFloat(((org.dyn4j.dynamics.joint.DistanceJoint) joint).getSpringDampingRatio());
  }

  public void setDistance(float distance) {
    ((org.dyn4j.dynamics.joint.DistanceJoint) joint).setRestDistance(distance);
  }

  public float getDistance() {
    return Converter.toFloat(((org.dyn4j.dynamics.joint.DistanceJoint) joint).getRestDistance());
  }

  public float getCurrentDistance() {
    return Converter.toFloat(((org.dyn4j.dynamics.joint.DistanceJoint) joint).getCurrentDistance());
  }

  public void setFrequency(float frequency) {
    ((org.dyn4j.dynamics.joint.DistanceJoint) joint).setSpringFrequency(frequency);
  }

  public float getFrequency() {
    return Converter.toFloat(((org.dyn4j.dynamics.joint.DistanceJoint) joint).getSpringFrequency());
  }
}
