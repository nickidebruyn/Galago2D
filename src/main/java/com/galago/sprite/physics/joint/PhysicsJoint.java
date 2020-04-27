package com.galago.sprite.physics.joint;

import com.galago.sprite.physics.RigidBodyControl2D;
import org.dyn4j.dynamics.joint.Joint;

/**
 * @author nickidebruyn
 */
public abstract class PhysicsJoint {

  protected RigidBodyControl2D rigidBodyControl1;
  protected RigidBodyControl2D rigidBodyControl2;
  protected Joint joint;

  public PhysicsJoint(RigidBodyControl2D rigidBodyControl1, RigidBodyControl2D rigidBodyControl2) {
    this.rigidBodyControl1 = rigidBodyControl1;
    this.rigidBodyControl2 = rigidBodyControl2;

  }

  protected abstract Joint createJoint();

  public RigidBodyControl2D getRigidBodyControl1() {
    return rigidBodyControl1;
  }

  public RigidBodyControl2D getRigidBodyControl2() {
    return rigidBodyControl2;
  }

  public Joint getJoint() {
    if (joint == null) {
      joint = createJoint();
    }
    return joint;
  }

}
