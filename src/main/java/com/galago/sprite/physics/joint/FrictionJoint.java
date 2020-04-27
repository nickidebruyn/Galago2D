package com.galago.sprite.physics.joint;

import com.galago.sprite.physics.Converter;
import com.galago.sprite.physics.RigidBodyControl2D;
import com.jme3.math.Vector3f;
import org.dyn4j.dynamics.joint.Joint;

/**
 * @author nickidebruyn
 */
public class FrictionJoint extends PhysicsJoint {

  protected Vector3f anchor1;

  public FrictionJoint(RigidBodyControl2D rigidBodyControl1, RigidBodyControl2D rigidBodyControl2, Vector3f anchor1) {
    super(rigidBodyControl1, rigidBodyControl2);
    this.anchor1 = anchor1;
  }

  @Override
  protected Joint createJoint() {
    org.dyn4j.dynamics.joint.FrictionJoint frictionJoint = new org.dyn4j.dynamics.joint.FrictionJoint(rigidBodyControl1.getBody(), rigidBodyControl2.getBody(), Converter.toVector2(anchor1));
    return frictionJoint;
  }

  public void setMaximumForce(float maxForce) {
    ((org.dyn4j.dynamics.joint.FrictionJoint) joint).setMaximumForce(maxForce);
  }

  public float getMaximumForce() {
    return Converter.toFloat(((org.dyn4j.dynamics.joint.FrictionJoint) joint).getMaximumForce());
  }

  public void setMaximumTorque(float torque) {
    ((org.dyn4j.dynamics.joint.FrictionJoint) joint).setMaximumTorque(torque);
  }

  public float getMaximumTorque() {
    return Converter.toFloat(((org.dyn4j.dynamics.joint.FrictionJoint) joint).getMaximumTorque());
  }

}
