package com.galago.sprite.physics;

import com.galago.sprite.physics.shape.CollisionShape;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * @author nickidebruyn
 */
public interface PhysicsCollisionListener {

  public void collision(Spatial spatialA, CollisionShape collisionShapeA, Spatial spatialB, CollisionShape collisionShapeB, Vector3f collisionPoint);

}
