package com.galago.sprite.pyxeledit;

import com.galago.sprite.physics.RigidBodyControl2D;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class TileOptimizer extends AbstractControl {

  private PyxelEditGame pyxelEditGame;
  private RigidBodyControl2D rigidBodyControl2D;
  private float activeDistance = 3f;
  private boolean attached = false;

  public TileOptimizer(PyxelEditGame pyxelEditGame, RigidBodyControl2D rigidBodyControl2D, float activeDistance) {
    this.pyxelEditGame = pyxelEditGame;
    this.rigidBodyControl2D = rigidBodyControl2D;
    this.activeDistance = activeDistance;
  }

  @Override
  protected void controlUpdate(float tpf) {

    if (pyxelEditGame.getPlayer() != null) {

      if (attached) {
        if (rigidBodyControl2D.getPhysicsLocation().distance(pyxelEditGame.getPlayer().getPosition()) >= activeDistance) {
          pyxelEditGame.getDyn4jAppState().getPhysicsSpace().remove(rigidBodyControl2D);
          attached = false;
        }

      } else {
        if (rigidBodyControl2D.getPhysicsLocation().distance(pyxelEditGame.getPlayer().getPosition()) < activeDistance) {
          pyxelEditGame.getDyn4jAppState().getPhysicsSpace().add(rigidBodyControl2D);
          attached = true;
        }
      }

    }

  }

  @Override
  protected void controlRender(RenderManager rm, ViewPort vp) {

  }
}
