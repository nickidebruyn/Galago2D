package com.galago.sprite.examples;

import com.galago.sprite.Sprite;
import com.galago.sprite.camera.Camera2DState;
import com.galago.sprite.physics.Dyn4jAppState;
import com.galago.sprite.physics.RigidBodyControl2D;
import com.galago.sprite.physics.characters.PlatformerCharacterControlState;
import com.galago.sprite.physics.shape.BoxCollisionShape;
import com.galago.sprite.physics.shape.CircleCollisionShape;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Vector2f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.texture.Texture;

/**
 * This example will show you how to create simple sprites for the background,
 * floor and player.
 *
 *
 * @author NickideBruyn
 */
public class Example1 extends SimpleApplication {

  private Geometry player;     //The player spatial object
  private Geometry background; //The background spatial
  private Geometry floor;      //The floor

  private Dyn4jAppState dyn4jAppState;
  private PlatformerCharacterControlState platformerCharacterControlState;
  private Camera2DState camera2DState;

  /**
   * The main method for this java app when we run it.
   * @param args
   */
  public static void main(String[] args) {
    Example1 app = new Example1();
    app.start();
  }

  @Override
  public void simpleInitApp() {

    dyn4jAppState = new Dyn4jAppState();
    stateManager.attach(dyn4jAppState);

    //Load the background
    Sprite backgroundSprite = new Sprite(20, 10);
    background = new Geometry("background", backgroundSprite);
    Material material = loadMaterial("Textures/colored_desert.png");
    background.setMaterial(material);
    backgroundSprite.scaleTextureCoordinates(new Vector2f(2, 1));
    rootNode.attachChild(background);
    background.move(0, 0, -1);

    //Load the floor
    Sprite floorSprite = new Sprite(20, 1);
    floor = new Geometry("floor", floorSprite);
    material = loadMaterial("Textures/dirtMid.png");
    floor.setMaterial(material);
    floorSprite.scaleTextureCoordinates(new Vector2f(20, 1));
    rootNode.attachChild(floor);
    floor.move(0, -4f, 0);

    //For the floor sprite it will be a static body with 0 mass
    RigidBodyControl2D rbcFloor = new RigidBodyControl2D(new BoxCollisionShape(floorSprite.getWidth(), floorSprite.getHeight()), 0);
    //Attach the control to the spatial
    floor.addControl(rbcFloor);
    //Add the rigid body to the physics space
    dyn4jAppState.getPhysicsSpace().add(rbcFloor);
    //Move the rigid body to the desired position
    rbcFloor.move(0, -4f);


    //Load the player
    Sprite playerSprite = new Sprite(1, 1);
    player = new Geometry("player", playerSprite);
    material = loadMaterial("Textures/slimeBlock.png");
    player.setMaterial(material);
    player.setQueueBucket(RenderQueue.Bucket.Transparent);
    rootNode.attachChild(player);
    player.move(0, 3f, 0);

    //The player rigid body will have mass so that it can be affected by gravity
    RigidBodyControl2D rbcPlayer = new RigidBodyControl2D(new CircleCollisionShape(0.5f), 1);
    //Add the control to the player spatial
    player.addControl(rbcPlayer);
    //Add it to the physics space
    dyn4jAppState.getPhysicsSpace().add(rbcPlayer);
    //Now we can move it to a start position
    rbcPlayer.move(0, 3);
    //Adjust the player's gravity
    rbcPlayer.setGravityScale(2);


    platformerCharacterControlState = new PlatformerCharacterControlState(dyn4jAppState, player);
    stateManager.attach(platformerCharacterControlState);

    //Example4-Step1: Create the camera2Dstate and give it a distance and movement interpolation amount
    camera2DState = new Camera2DState(player, 6, 0.01f);
    //Set camera clipping which will block the camera from moving a certain min and max value on the x and y axis
    camera2DState.setCameraClipping(new Vector2f(-2f, 0), new Vector2f(2f, 1));
    //You can also set a camera to player offset. Move the camera to stick above the player 2f in the y axis
    camera2DState.setTargetOffset(new Vector2f(0, 2));
    //Attach the camera2Dstate to the state manager
    stateManager.attach(camera2DState);

  }

  /**
   * Local method for reuse of loading a texture as an unshaded material
   *
   * @param file
   * @return
   */
  protected Material loadMaterial(String file) {
    Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    Texture tex = assetManager.loadTexture(new TextureKey(file, false));
    tex.setWrap(Texture.WrapMode.Repeat);
    mat.setTexture("ColorMap", tex);
    mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
    return mat;
  }
}
