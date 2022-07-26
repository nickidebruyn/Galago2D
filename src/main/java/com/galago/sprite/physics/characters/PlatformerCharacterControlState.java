package com.galago.sprite.physics.characters;

import com.galago.sprite.Sprite;
import com.galago.sprite.physics.*;
import com.galago.sprite.physics.shape.CollisionShape;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;

/**
 * @author nickidebruyn
 */
public class PlatformerCharacterControlState extends BaseAppState implements ActionListener, PhysicsTickListener, PhysicsCollisionListener {

  private static final String CHARACTER_LEFT = "CHARACTER_LEFT";
  private static final String CHARACTER_RIGHT = "CHARACTER_RIGHT";
  private static final String CHARACTER_UP = "CHARACTER_UP";
  private static final String CHARACTER_DOWN = "CHARACTER_DOWN";
  private static final String CHARACTER_JUMP = "CHARACTER_JUMP";
  private static final String CHARACTER_ATTACK = "CHARACTER_ATTACK";
  private static final String CHARACTER_INTERACT = "CHARACTER_INTERACT";

  private Dyn4jAppState dyn4jAppState;
  private Spatial spatial;
  private RigidBodyControl2D rigidBodyControl;
  private Sprite sprite;

  private SimpleApplication application;
  private InputManager inputManager;
  private float moveSpeed = 6;
  private Vector2f movementDirection = new Vector2f(0, 0);
  private boolean left;
  private boolean right;
  private boolean up;
  private boolean down;
  private boolean jump;
  private boolean onGround;
  private int jumpCount = 0;
  private float jumpForce = 10;
  private PlatformerCharacterListener platformerCharacterListener;

  public PlatformerCharacterControlState(Dyn4jAppState dyn4jAppState, Spatial spatial) {
    this(dyn4jAppState, spatial, null);
  }

  public PlatformerCharacterControlState(Dyn4jAppState dyn4jAppState, Spatial spatial, Sprite sprite) {
    this.dyn4jAppState = dyn4jAppState;
    this.spatial = spatial;
    this.sprite = sprite;
  }

  @Override
  protected void initialize(Application app) {
    this.application = (SimpleApplication) app;
    this.inputManager = application.getInputManager();

  }

  @Override
  protected void cleanup(Application app) {
    unregisterInput();

  }

  @Override
  protected void onEnable() {
    movementDirection.setX(0);
    movementDirection.setY(0);
    left = false;
    right = false;
    up = false;
    down = false;
    jump = false;
    onGround = true;
    jumpCount = 0;

    if (dyn4jAppState != null && spatial.getControl(RigidBodyControl2D.class) != null) {
      rigidBodyControl = spatial.getControl(RigidBodyControl2D.class);
      dyn4jAppState.getPhysicsSpace().addPhysicsTickListener(this);
      dyn4jAppState.getPhysicsSpace().addPhysicsCollisionListener(this);

    }

    if (sprite == null) {
      sprite = (Sprite) ((Geometry) spatial).getMesh();
    }

    registerWithInput(inputManager);

  }

  @Override
  protected void onDisable() {
    unregisterInput();
    if (dyn4jAppState != null) {
      dyn4jAppState.getPhysicsSpace().removePhysicsTickListener(this);
      dyn4jAppState.getPhysicsSpace().removePhysicsCollisionListener(this);

    }

  }

  /**
   * Register this controller to receive input events from the specified input
   * manager.
   *
   * @param inputManager
   */
  public void registerWithInput(InputManager inputManager) {
    this.inputManager = inputManager;

    // keyboard only WASD for movement and WZ for rise/lower height
    inputManager.addMapping(CHARACTER_LEFT, new KeyTrigger(KeyInput.KEY_A), new KeyTrigger(KeyInput.KEY_LEFT));
    inputManager.addMapping(CHARACTER_RIGHT, new KeyTrigger(KeyInput.KEY_D), new KeyTrigger(KeyInput.KEY_RIGHT));
    inputManager.addMapping(CHARACTER_UP, new KeyTrigger(KeyInput.KEY_W), new KeyTrigger(KeyInput.KEY_UP));
    inputManager.addMapping(CHARACTER_DOWN, new KeyTrigger(KeyInput.KEY_S), new KeyTrigger(KeyInput.KEY_DOWN));
    inputManager.addMapping(CHARACTER_ATTACK, new KeyTrigger(KeyInput.KEY_LCONTROL));
    inputManager.addMapping(CHARACTER_JUMP, new KeyTrigger(KeyInput.KEY_SPACE));
    inputManager.addMapping(CHARACTER_INTERACT, new KeyTrigger(KeyInput.KEY_E));

    inputManager.addListener(this, new String[]{CHARACTER_LEFT, CHARACTER_RIGHT, CHARACTER_DOWN, CHARACTER_UP, CHARACTER_ATTACK, CHARACTER_JUMP, CHARACTER_INTERACT});
    inputManager.setCursorVisible(false);

  }

  public void unregisterInput() {
    if (inputManager == null) {
      return;
    }

    if (inputManager.hasMapping(CHARACTER_LEFT)) {
      inputManager.deleteMapping(CHARACTER_LEFT);
      inputManager.deleteMapping(CHARACTER_RIGHT);
      inputManager.deleteMapping(CHARACTER_UP);
      inputManager.deleteMapping(CHARACTER_DOWN);
      inputManager.deleteMapping(CHARACTER_ATTACK);
      inputManager.deleteMapping(CHARACTER_JUMP);
      inputManager.deleteMapping(CHARACTER_INTERACT);
    }

    inputManager.removeListener(this);
    inputManager.setCursorVisible(true);

  }

  public void setPlatformerCharacterListener(PlatformerCharacterListener platformerCharacterListener) {
    this.platformerCharacterListener = platformerCharacterListener;
  }

  protected void fireIdleEvent() {
    if (this.platformerCharacterListener != null) {
      this.platformerCharacterListener.doCharacterIdleEvent();
    }
  }

  protected void fireWalkEvent(float dir) {
    if (this.platformerCharacterListener != null) {
      this.platformerCharacterListener.doCharacterWalkEvent(dir);
    }
  }

  protected void fireJumpEvent(int count) {
    if (this.platformerCharacterListener != null) {
      this.platformerCharacterListener.doCharacterJumpEvent(count);
    }
  }

  @Override
  public void onAction(String name, boolean isPressed, float tpf) {

    if (name.equals(CHARACTER_JUMP) && isPressed) {

      if (!jump && (onGround || jumpCount < 2)) {
        jump = true;
        onGround = false;
        fireJumpEvent(jumpCount);
        jumpCount++;

      }

    }

    if (name.equals(CHARACTER_LEFT)) {
      movementDirection.x = isPressed ? -1 : 0;
      if (movementDirection.x == 0) {
        movementDirection.x = right ? 1 : 0;
      }
      left = isPressed;

    }
    if (name.equals(CHARACTER_RIGHT)) {
      movementDirection.x = isPressed ? 1 : 0;
      if (movementDirection.x == 0) {
        movementDirection.x = left ? -1 : 0;
      }
      right = isPressed;

    }

    if (name.equals(CHARACTER_UP) && isPressed) {
//            movementDirection.y = isPressed ? 1 : 0;
//            if (movementDirection.y == 0) {
//                movementDirection.y = down ? -1 : 0;
//            }
//            up = isPressed;

      if (!jump && (onGround || jumpCount < 2)) {
        jump = true;
        onGround = false;
        fireJumpEvent(jumpCount);

      }

    }
    if (name.equals(CHARACTER_DOWN)) {
      movementDirection.y = isPressed ? -1 : 0;
      if (movementDirection.y == 0) {
        movementDirection.y = up ? 1 : 0;
      }
      down = isPressed;

    }

  }

  @Override
  public void update(float tpf) {

    if (spatial != null && rigidBodyControl == null) {
      movementDirection.normalizeLocal();
      spatial.move(tpf * movementDirection.x * moveSpeed, 0, 0);

    }

    if (movementDirection.x == 0) {
      fireIdleEvent();

    } else {
      fireWalkEvent(movementDirection.x);

    }

    if (movementDirection.x < 0) {
      sprite.flipCoords(true);

    } else if (movementDirection.x > 0) {
      sprite.flipCoords(false);

    } else {
//      sprite.flipCoords(false);

    }

  }

  @Override
  public void prePhysicsTick(PhysicsSpace space, float tpf) {

    rigidBodyControl.move(tpf * movementDirection.x * moveSpeed, 0);
    rigidBodyControl.setAngularVelocity(0);
    rigidBodyControl.setPhysicsRotation(0);

    if (jump) {
      rigidBodyControl.setLinearVelocity(0, jumpForce);
      jump = false;
      jumpCount++;
    }
  }

  @Override
  public void physicsTick(PhysicsSpace space, float tpf) {

  }

  //    @Override
//    public void collision(PhysicsCollisionEvent event) {
//
//        if (event.getNodeA().equals(spatial) && event.getNodeB().getControl(RigidBodyControl.class).getMass() == 0) {
//            System.out.println("Player collided with something A");
//            onGround = true;
//
//        } else if (event.getNodeB().equals(spatial) && event.getNodeA().getControl(RigidBodyControl.class).getMass() == 0) {
//            System.out.println("Player collided with something B");
//            onGround = true;
//
//        }
//
//    }
  @Override
  public void collision(Spatial spatialA, CollisionShape collisionShapeA, Spatial spatialB, CollisionShape collisionShapeB, Vector3f collisionPoint) {

    if (spatialA.equals(spatial) && spatialB.getControl(RigidBodyControl2D.class).getMass() == 0) {
//            System.out.println("Player collided with something A");
      onGround = true;
      jumpCount = 0;

    } else if (spatialB.equals(spatial) && spatialA.getControl(RigidBodyControl2D.class).getMass() == 0) {
//            System.out.println("Player collided with something B");
      onGround = true;
      jumpCount = 0;

    }

  }

}
