package com.galago.sprite.physics;

import com.jme3.scene.control.Control;
import org.dyn4j.dynamics.Body;

/**
 * @author nickidebruyn
 */
public interface PhysicsControl extends Control {

  public void setPhysicsSpace(PhysicsSpace space);

  public PhysicsSpace getPhysicsSpace();

  public void setEnabled(boolean state);

  public Body getBody();

  public void setBody(Body body);
}
