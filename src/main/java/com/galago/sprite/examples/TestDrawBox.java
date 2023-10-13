package com.galago.sprite.examples;

import com.galago.sprite.Sprite;
import com.galago.sprite.camera.Camera2DState;
import com.galago.sprite.physics.Dyn4jAppState;
import com.galago.sprite.physics.RigidBodyControl2D;
import com.galago.sprite.physics.debug.Dyn4JDebugAppState;
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
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Line;

/**
 * This example will show you how to add multiple boxes onto the scene by clicking, dragging and then releasing the mouse
 * in order to draw a different size box to the screen.
 * Left click draws static boxes and right click draws dynamic boxes
 *
 * @author NickideBruyn
 */
public class TestDrawBox extends SimpleApplication implements ActionListener {

  private static final String LEFT_MOUSE_ACTION = "LEFT_MOUSE_ACTION";
  private static final String RIGHT_MOUSE_ACTION = "RIGHT_MOUSE_ACTION";

  private Geometry background; //The background spatial
  private Geometry floor;      //The floor

  private Vector3f startPoint;
  private Vector3f endPoint;
  private Geometry topLine, bottomLine, leftLine, rightLine;

  private boolean drawing = false;

  private Dyn4jAppState dyn4jAppState;

  private Dyn4JDebugAppState dyn4JDebugAppState;

  private Camera2DState camera2DState;
  private BitmapText instuctions;

  /**
   * The main method for this java app when we run it.
   *
   * @param args
   */
  public static void main(String[] args) {
    TestDrawBox app = new TestDrawBox();
    app.start();
  }

  @Override
  public void simpleInitApp() {

    dyn4jAppState = new Dyn4jAppState();
    stateManager.attach(dyn4jAppState);

    dyn4JDebugAppState = new Dyn4JDebugAppState(dyn4jAppState);
    stateManager.attach(dyn4JDebugAppState);

    //Load the background
    Sprite backgroundSprite = new Sprite(24, 16);
    background = new Geometry("background", backgroundSprite);
    Material material = SpriteUtils.loadMaterial(assetManager, ColorRGBA.LightGray);
    background.setMaterial(material);
    rootNode.attachChild(background);
    background.move(0, 0, -1);

    //Load the floor
    addBox(24, 3, 0, -6, ColorRGBA.Brown, 0);

    //Load the sample lines
    topLine = addLine();
    bottomLine = addLine();
    leftLine = addLine();
    rightLine = addLine();

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
    instuctions = loadInstructions("Left click and drag to draw static box and right click and drag to draw dynamic box.", 500, 50, 18, ColorRGBA.Brown);
    guiNode.attachChild(instuctions);
    alignTextFromCenter(instuctions, 0, 300);

    //Load input
    initializeInput();

  }

  @Override
  public void simpleUpdate(float tpf) {

    if (drawing) {
      endPoint = cam.getWorldCoordinates(inputManager.getCursorPosition(), 0);
      updateLineBox();

    }

  }

  private void updateLineBox() {
    if (startPoint != null && endPoint != null) {
      topLine.setCullHint(Spatial.CullHint.Never);
      bottomLine.setCullHint(Spatial.CullHint.Never);
      leftLine.setCullHint(Spatial.CullHint.Never);
      rightLine.setCullHint(Spatial.CullHint.Never);

      ((Line) topLine.getMesh()).updatePoints(startPoint, new Vector3f(endPoint.x, startPoint.y, 1));
      ((Line) bottomLine.getMesh()).updatePoints(new Vector3f(startPoint.x, endPoint.y, 1), new Vector3f(endPoint.x, endPoint.y, 1));
      ((Line) leftLine.getMesh()).updatePoints(startPoint, new Vector3f(startPoint.x, endPoint.y, 1));
      ((Line) rightLine.getMesh()).updatePoints(new Vector3f(endPoint.x, startPoint.y, 1), new Vector3f(endPoint.x, endPoint.y, 1));

    } else {
      topLine.setCullHint(Spatial.CullHint.Always);
      bottomLine.setCullHint(Spatial.CullHint.Always);
      leftLine.setCullHint(Spatial.CullHint.Always);
      rightLine.setCullHint(Spatial.CullHint.Always);

    }
  }

  private void initializeInput() {
// Test multiple inputs per mapping
    inputManager.addMapping(LEFT_MOUSE_ACTION, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
    inputManager.addMapping(RIGHT_MOUSE_ACTION, new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));

    // Test multiple listeners per mapping
    inputManager.addListener(this, LEFT_MOUSE_ACTION, RIGHT_MOUSE_ACTION);
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

    if (name.equals(LEFT_MOUSE_ACTION) || name.equals(RIGHT_MOUSE_ACTION)) {

      //Check if the left mouse button is pressed
      if (isPressed) {
        startPoint = cam.getWorldCoordinates(inputManager.getCursorPosition(), 0);
        endPoint = startPoint.clone();
        drawing = true;
        updateLineBox();

      } else {
        //Call code when left mouse is released
        float width = endPoint.x - startPoint.x;
        float height = endPoint.y - startPoint.y;
        addBox(FastMath.abs(width),
                FastMath.abs(height),
                endPoint.x - width/2,
                endPoint.y - height/2,
                name.equals(RIGHT_MOUSE_ACTION) ? ColorRGBA.fromRGBA255(162, 155, 254,255) : ColorRGBA.fromRGBA255(85, 239, 196,255),
                name.equals(RIGHT_MOUSE_ACTION) ? 1 : 0);

        drawing = false;
        startPoint = null;
        endPoint = null;
        updateLineBox(); //Do this to hide the outlined box

        dyn4JDebugAppState.setEnabled(true);

      }
    }

  }

  private void addBox(float width, float height, float x, float y, ColorRGBA colorRGBA, float mass) {

    //Load the sprite
    Sprite sprite = new Sprite(width, height);
    Geometry geom = new Geometry("box", sprite);
    Material material = SpriteUtils.loadMaterial(assetManager, colorRGBA);
    geom.setMaterial(material);
    geom.setQueueBucket(RenderQueue.Bucket.Transparent);
    rootNode.attachChild(geom);
    geom.move(x, y, 0);

    //Load the physics body and add to the physics world
    RigidBodyControl2D rbc = new RigidBodyControl2D(new BoxCollisionShape(sprite.getWidth(), sprite.getHeight()), mass);
    geom.addControl(rbc);
    dyn4jAppState.getPhysicsSpace().add(rbc);
    rbc.move(x, y);

  }

  private Geometry addLine() {
    Line line = new Line(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
    Geometry lineGeom = new Geometry("line", line);
    Material material = SpriteUtils.loadMaterial(assetManager, ColorRGBA.Green);
    lineGeom.setMaterial(material);
    lineGeom.setCullHint(Spatial.CullHint.Always);
    rootNode.attachChild(lineGeom);
    return lineGeom;
  }
}
