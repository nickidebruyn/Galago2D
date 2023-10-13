package com.galago.sprite.examples;

import com.galago.sprite.Sprite;
import com.galago.sprite.camera.Camera2DState;
import com.galago.sprite.physics.Dyn4jAppState;
import com.galago.sprite.physics.RigidBodyControl2D;
import com.galago.sprite.physics.shape.BoxCollisionShape;
import com.galago.sprite.utils.SpriteUtils;
import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;

/**
 * This example will show you how to add multiple boxes onto the scene by clicking on the screen.
 *
 * @author NickideBruyn
 */
public class TestBoxes extends SimpleApplication implements ActionListener {

  private static final String LEFT_MOUSE_ACTION = "LEFT_MOUSE_ACTION";

  private Geometry background; //The background spatial
  private Geometry floor;      //The floor

  private Dyn4jAppState dyn4jAppState;

  private Camera2DState camera2DState;
  private BitmapText instuctions;

  /**
   * The main method for this java app when we run it.
   *
   * @param args
   */
  public static void main(String[] args) {
    TestBoxes app = new TestBoxes();
    app.start();
  }

  @Override
  public void simpleInitApp() {

    dyn4jAppState = new Dyn4jAppState();
    stateManager.attach(dyn4jAppState);

    //Load the background
    Sprite backgroundSprite = new Sprite(20, 16);
    background = new Geometry("background", backgroundSprite);
    Material material = SpriteUtils.loadMaterial(assetManager, ColorRGBA.Brown);
    background.setMaterial(material);
    rootNode.attachChild(background);
    background.move(0, 0, -1);

    //Load the floor
    Sprite floorSprite = new Sprite(20, 3);
    floor = new Geometry("floor", floorSprite);
    material = SpriteUtils.loadMaterial(assetManager, ColorRGBA.Green);
    floor.setMaterial(material);
    rootNode.attachChild(floor);
    floor.move(0, -6f, 0);

    //For the floor sprite it will be a static body with 0 mass
    RigidBodyControl2D rbcFloor = new RigidBodyControl2D(new BoxCollisionShape(floorSprite.getWidth(), floorSprite.getHeight()), 0);
    //Attach the control to the spatial
    floor.addControl(rbcFloor);
    //Add the rigid body to the physics space
    dyn4jAppState.getPhysicsSpace().add(rbcFloor);
    //Move the rigid body to the desired position
    rbcFloor.move(0, -6f);

    //Init the camera
    //Example4-Step1: Create the camera2Dstate and give it a distance and movement interpolation amount
    camera2DState = new Camera2DState(background, 6, 0.01f);
    //Set camera clipping which will block the camera from moving a certain min and max value on the x and y axis
    camera2DState.setCameraClipping(new Vector2f(-2f, 0), new Vector2f(2f, 1));
    //You can also set a camera to player offset. Move the camera to stick above the player 2f in the y axis
    camera2DState.setTargetOffset(new Vector2f(0, 0));
    //Attach the camera2Dstate to the state manager
    stateManager.attach(camera2DState);

    //Load the instructions
    instuctions = loadInstructions("Click anywhere on the screen to make boxes drop!", 500, 50, 20, ColorRGBA.Cyan);
    guiNode.attachChild(instuctions);
    alignTextFromCenter(instuctions, 0, 230);

    //Load input
    initializeInput();

  }

  private void initializeInput() {
// Test multiple inputs per mapping
    inputManager.addMapping(LEFT_MOUSE_ACTION, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));

    // Test multiple listeners per mapping
    inputManager.addListener(this, LEFT_MOUSE_ACTION);
  }

  private BitmapText loadInstructions(String text, float width, float height, float fontSize, ColorRGBA colorRGBA) {
    BitmapText bitmapText = guiFont.createLabel(text);
    bitmapText.setText(text);             // the text
    //The Rectangle box height value for bitmap text is not a physical height but half the height
    bitmapText.setBox(new Rectangle(-width * 0.5f, height * 0.5f, width, height * 0.5f));
    bitmapText.setSize(fontSize);      // font size
    bitmapText.setColor(colorRGBA);// font color
    bitmapText.setAlignment(BitmapFont.Align.Center);
    bitmapText.setVerticalAlignment(BitmapFont.VAlign.Center);
    return bitmapText;
  }

  private void alignTextFromCenter(BitmapText bitmapText, float x, float y) {
    float xCenter = this.settings.getWidth() * 0.5f;
    float yCenter = this.settings.getHeight() * 0.5f;

    bitmapText.setLocalTranslation(xCenter + x, yCenter + y, bitmapText.getLocalTranslation().z);
  }

  @Override
  public void onAction(String name, boolean isPressed, float tpf) {

    if (name.equals(LEFT_MOUSE_ACTION)) {

      //Check if the left mouse button is released
      if (!isPressed) {
        Vector3f worldPosition = cam.getWorldCoordinates(inputManager.getCursorPosition(), 0);
        addBox(1, 1, worldPosition.x, worldPosition.y, ColorRGBA.randomColor());

      }
    }

  }

  private void addBox(float width, float height, float x, float y, ColorRGBA colorRGBA) {

    //Load the sprite
    Sprite sprite = new Sprite(width, height);
    Geometry geom = new Geometry("box", sprite);
    Material material = SpriteUtils.loadMaterial(assetManager, colorRGBA);
    geom.setMaterial(material);
    geom.setQueueBucket(RenderQueue.Bucket.Transparent);
    rootNode.attachChild(geom);
    geom.move(x, y, 0);

    //Load the physics body and add to the physics world
    RigidBodyControl2D rbc = new RigidBodyControl2D(new BoxCollisionShape(sprite.getWidth(), sprite.getHeight()), 1);
    geom.addControl(rbc);
    dyn4jAppState.getPhysicsSpace().add(rbc);
    rbc.move(x, y);

  }
}
