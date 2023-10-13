package com.galago.sprite.examples;

import com.galago.sprite.Sprite;
import com.galago.sprite.camera.Camera2DState;
import com.galago.sprite.physics.Dyn4jAppState;
import com.galago.sprite.tiles.TileSet;
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
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;

/**
 * This example will show you how to add multiple boxes onto the scene by clicking on the screen.
 *
 * @author NickideBruyn
 */
public class TestTilePainting extends SimpleApplication implements ActionListener {

  private static final String LEFT_MOUSE_ACTION = "LEFT_MOUSE_ACTION";
  private static final String RIGHT_MOUSE_ACTION = "RIGHT_MOUSE_ACTION";

  private Geometry background; //The background spatial

  private Node terrainNode;

  private Spatial gizmo;
  private Spatial outerGizmo;

  private Material gizmoMaterial;

  private TileSet tileSet;

  private float tileSize = 1f; //This should always be 1 in size

  private Dyn4jAppState dyn4jAppState;

  private Camera2DState camera2DState;
  private BitmapText instuctions;

  private boolean painting = false;
  private boolean deleting = false;

  private Vector3f worldPosition = new Vector3f(0, 0, 0);
  private Vector2f cursorGridPosition = new Vector2f(0, 0);

  /**
   * The main method for this java app when we run it.
   *
   * @param args
   */
  public static void main(String[] args) {
    TestTilePainting app = new TestTilePainting();
    AppSettings settings = new AppSettings(true);
    settings.setTitle("Tilemap painting");
    settings.setWidth(1280);
    settings.setHeight(720);
    settings.setVSync(false);
    settings.setUseJoysticks(true);
    settings.setSettingsDialogImage(null);
    settings.setGammaCorrection(false);
//    settings.setRenderer(AppSettings.LWJGL_OPENGL2);
    app.setSettings(settings);
    app.setPauseOnLostFocus(false);
    app.start(JmeContext.Type.Display);
  }

  @Override
  public void simpleInitApp() {

    dyn4jAppState = new Dyn4jAppState();
    stateManager.attach(dyn4jAppState);

    //Load the tileset
    tileSet = new TileSet(dyn4jAppState.getPhysicsSpace(), SpriteUtils.loadMaterial(assetManager, "Textures/jungle.png"), 8, 8);

    //Single
    tileSet.loadRule(27, "xxx-x1x-xxx");

    //Horizontal
    tileSet.loadRule(24, "0x0-x11-0x0");
    tileSet.loadRule(25, "0x0-111-0x0");
    tileSet.loadRule(26, "0x0-11x-0x0");

    //Load the background
    float backgroundWidth = 40;
    float backgroundHeight = 30;
    Sprite backgroundSprite = new Sprite(backgroundWidth, backgroundHeight);
    background = new Geometry("background", backgroundSprite);
    Material material = SpriteUtils.loadMaterial(assetManager, "Textures/outlined-box.png");
    material.setColor("Color", ColorRGBA.DarkGray);
    background.setMaterial(material);
    rootNode.attachChild(background);
    backgroundSprite.scaleTextureCoordinates(new Vector2f(backgroundWidth, backgroundHeight));
    background.move(0, 0, -1);

    //load the terrain node
    terrainNode = new Node("terrain");
    rootNode.attachChild(terrainNode);

    //Load the gizmo
    Sprite gizmoSprite = new Sprite(1, 1);
    gizmo = new Geometry("gizmo", gizmoSprite);
    gizmoMaterial = SpriteUtils.loadMaterial(assetManager, "Textures/outlined-box.png");
    gizmoMaterial.setColor("Color", ColorRGBA.Blue);
    gizmo.setQueueBucket(RenderQueue.Bucket.Translucent);
    gizmo.setMaterial(gizmoMaterial);
    rootNode.attachChild(gizmo);

    gizmoSprite = new Sprite(3, 3);
    outerGizmo = new Geometry("outerGizmo", gizmoSprite);
    Material gizmoMaterial2 = SpriteUtils.loadMaterial(assetManager, "Textures/outlined-box.png");
    gizmoMaterial2.setColor("Color", ColorRGBA.White);
    outerGizmo.setQueueBucket(RenderQueue.Bucket.Translucent);
    outerGizmo.setMaterial(gizmoMaterial2);
//    gizmoSprite.scaleTextureCoordinates(new Vector2f(3, 3));
    rootNode.attachChild(outerGizmo);

    //Init the camera
    camera2DState = new Camera2DState(terrainNode, 10, 0.01f);
    camera2DState.setCameraClipping(new Vector2f(-2f, 0), new Vector2f(2f, 1));
    camera2DState.setTargetOffset(new Vector2f(0, 0));
    stateManager.attach(camera2DState);

    //Load the instructions
    instuctions = loadInstructions("Click and drag on tile grid in order to paint tiles", 800, 50, 16, ColorRGBA.Cyan);
    guiNode.attachChild(instuctions);
    alignTextFromCenter(instuctions, 0, 320);

    //Load input
    initializeInput();

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

  private Vector2f getCursorPointOnGrid() {
    worldPosition = cam.getWorldCoordinates(inputManager.getCursorPosition(), 0);
    float xAdd = 0.5f;
    float yAdd = 0.5f;
    if (worldPosition.x < 0) xAdd = -xAdd;
    if (worldPosition.y < 0) yAdd = -yAdd;

    cursorGridPosition.set(((int) worldPosition.x) + xAdd, ((int) worldPosition.y) + yAdd);
    return cursorGridPosition;
  }

  @Override
  public void simpleUpdate(float tpf) {

    Vector2f pos = getCursorPointOnGrid();

    gizmo.setLocalTranslation(pos.x, pos.y, gizmo.getLocalTranslation().z);
    outerGizmo.setLocalTranslation(pos.x, pos.y, gizmo.getLocalTranslation().z);

    if (painting) {
      tileSet.addTile(terrainNode, pos.x, pos.y, ColorRGBA.Green, 27);

    }

    if (deleting) {
      tileSet.removeTile(terrainNode, pos.x, pos.y);

    }

  }

  @Override
  public void onAction(String name, boolean isPressed, float tpf) {

    if (name.equals(LEFT_MOUSE_ACTION)) {
      painting = isPressed;
      deleting = false;
      gizmoMaterial.setColor("Color", isPressed ? ColorRGBA.Green : ColorRGBA.Blue);

    }

    if (name.equals(RIGHT_MOUSE_ACTION)) {
      deleting = isPressed;
      painting = false;
      gizmoMaterial.setColor("Color", isPressed ? ColorRGBA.Red : ColorRGBA.Blue);
    }

  }

}
