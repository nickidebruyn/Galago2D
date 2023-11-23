package com.galago.sprite.pyxeledit;

import com.galago.sprite.Sprite;
import com.galago.sprite.physics.Dyn4jAppState;
import com.galago.sprite.physics.RigidBodyControl2D;
import com.galago.sprite.physics.ThreadingType;
import com.galago.sprite.physics.debug.Dyn4JDebugAppState;
import com.galago.sprite.physics.shape.BoxCollisionShape;
import com.galago.sprite.utils.MaterialUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.BatchNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.system.JmeSystem;
import com.jme3.system.Platform;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public abstract class PyxelEditGame {

  public static final String TYPE = "type";
  public static final String TYPE_PLAYER = "player";
  public static final String TYPE_TERRAIN = "terrain";
  public static final String TYPE_ENEMY = "enemy";
  public static final String TYPE_OBSTACLE = "obstacle";
  public static final String TYPE_STATIC = "static";
  public static final String TYPE_PICKUP = "pickup";
  public static final String TYPE_START = "start";
  public static final String TYPE_END = "end";
  public static final String TYPE_VEGETATION = "vegetation";
  public static final String TYPE_BULLET = "bullet";

  protected SimpleApplication application;
  protected AppStateManager stateManager;
  protected Node rootNode;
  protected Node levelNode;
  protected PyxelEditLevel pyxelEditLevel;

  protected Dyn4jAppState dyn4jAppState;
  protected Dyn4JDebugAppState dyn4JDebugAppState;
  protected Material tilesetMaterial;
  protected int tileWidth;
  protected int tileHeight;
  protected int mapWidth;
  protected int mapHeight;
  protected int layerCount;
  protected PyxelEditPlayer player;
  protected List<Vector3f> spawnPoints = new ArrayList<>();
  protected boolean started = false;
  protected boolean paused = false;
  protected boolean gameOver = false;
  protected boolean loading = false;
  protected boolean postloadInd = false;
  protected PyxelEditGameListener gameListener;
  protected Spatial lastCollidedSpatial;
  protected Spatial lastColliderSpatial;

  private Random random;

  private boolean mobile = false;

  public PyxelEditGame(SimpleApplication application, Node rootNode, PyxelEditLevel pyxelEditLevel, boolean mobile) {
    this.application = application;
    this.rootNode = rootNode;
    this.pyxelEditLevel = pyxelEditLevel;
    this.random = new Random(pyxelEditLevel.getSeed());
    this.stateManager = application.getStateManager();
    this.mobile = mobile;
  }

  public Random getRandom() {
    return random;
  }

  /**
   * Next random float value between min and max. Min inclusive max excluded
   *
   * @param min
   * @param max
   * @return
   */
  public float nextFloat(float min, float max) {
    return random.nextFloat() * (max - min) + min;

//    return (random.nextInt() * (max - min) / Integer.MAX_VALUE) + min;
  }

  public void load() {
    preload();

    levelNode = new Node("world-node");
    rootNode.attachChild(levelNode);
    levelNode.addControl(new AbstractControl() {
      @Override
      protected void controlUpdate(float tpf) {
        if (!postloadInd) {
          postload();
          postloadInd = true;
        } else {
          gameUpdate(tpf);
        }
      }

      @Override
      protected void controlRender(RenderManager rm, ViewPort vp) {

      }
    });

    dyn4jAppState = new Dyn4jAppState(ThreadingType.SEQUENTIAL);
    stateManager.attach(dyn4jAppState);
    dyn4jAppState.getPhysicsSpace().setGravity(0, -30);

    dyn4JDebugAppState = new Dyn4JDebugAppState(dyn4jAppState);
    stateManager.attach(dyn4JDebugAppState);

    init();

//    postload();
  }

  public void close() {

    loading = false;
    started = false;
    paused = false;
    gameOver = false;

    if (player != null) {
      player.close();
    }

    levelNode.removeFromParent();
    rootNode.detachAllChildren();

    dyn4jAppState.cleanup();
    stateManager.detach(dyn4jAppState);
    stateManager.detach(dyn4JDebugAppState);

    player = null;
    System.gc(); //Force memory to be released;

  }

  public void pause() {
    paused = true;
    dyn4jAppState.setEnabled(false);
  }

  public void resume() {
    paused = false;
    dyn4jAppState.setEnabled(true);
  }

  protected abstract void preload();

  protected abstract void postload();

  protected abstract String parseSection(int c, int r);

  protected abstract void gameUpdate(float tpf);

  protected abstract void parseTile(Spatial spatial, int index);

  private void init() {
    if (pyxelEditLevel.getBackgroundColor() != null) {
      application.getViewPort().setBackgroundColor(pyxelEditLevel.getBackgroundColor());
    }

    //Load the tileset material
    if (pyxelEditLevel.isUnlitLevel()) {
      tilesetMaterial = MaterialUtils.loadUnlitPixelatedMaterial(application.getAssetManager(), pyxelEditLevel.getTilesetName(), pyxelEditLevel.getTilesetGlowName());
    } else {
      tilesetMaterial = MaterialUtils.loadLitPixelatedMaterial(application.getAssetManager(), pyxelEditLevel.getTilesetName(), pyxelEditLevel.getTilesetGlowName());
    }

    //Build the level
    for (int c = 0; c < pyxelEditLevel.getLevelSectionCountColumns(); c++) {
      for (int r = 0; r < pyxelEditLevel.getLevelSectionCountRows(); r++) {

        //Parse a specific section in the world.
        //If the section is not overridden then take random.
        String secName = parseSection(c, r);
        if (secName == null) {
          int randomIndex = (int) nextFloat(0, pyxelEditLevel.getSections().length);
          secName = pyxelEditLevel.getSections()[randomIndex];
        }

        System.out.println("Loading section: " + secName);
        loadPyxelEditSection(secName, c, r);


      }
    }

    //Load sky images
    if (pyxelEditLevel.getBackgrounds() != null) {
      int index = 1;
      for (String background : pyxelEditLevel.getBackgrounds()) {
        loadPyxelEditSky(background, pyxelEditLevel.getBackgrounds().length - index);
        index++;
      }
    }

    //Load light
    if (!pyxelEditLevel.isUnlitLevel()) {
//      AmbientLight ambientLight = new AmbientLight(ColorRGBA.DarkGray);
//      rootNode.addLight(ambientLight);
    }

  }

  private void loadPyxelEditSky(String background, int index) {
    System.out.println("Loading background sky: " + background);
    Material m = MaterialUtils.loadUnlitPixelatedMaterial(application.getAssetManager(), background, null);

    float parallaxEffectSpeed = 1f - (0.1f * index);
    System.out.println("Parallax effect speed = " + parallaxEffectSpeed);

    Sprite sky = new Sprite(mapWidth * pyxelEditLevel.getTileSize() * pyxelEditLevel.getBackgroundScale(), mapHeight * pyxelEditLevel.getTileSize() * pyxelEditLevel.getBackgroundScale());
    Geometry geometry = new Geometry("sky" + index, sky);
    geometry.setMaterial(m);
//    geometry.setQueueBucket(RenderQueue.Bucket.Translucent);
    geometry.setLocalTranslation(-mapWidth * pyxelEditLevel.getTileSize() * pyxelEditLevel.getBackgroundScale() * 0.3f, -sky.getHeight() * 0.5f, -10 + index);
    geometry.getMesh().scaleTextureCoordinates(new Vector2f(10, 1));
    geometry.setLocalScale(10, 1, 1);
    geometry.addControl(new AbstractControl() {
      @Override
      protected void controlUpdate(float tpf) {
        //Make the background stick to the camera
        spatial.setLocalTranslation(application.getCamera().getLocation().x * parallaxEffectSpeed, 0, spatial.getLocalTranslation().z);
      }

      @Override
      protected void controlRender(RenderManager rm, ViewPort vp) {
      }
    });

    levelNode.attachChild(geometry);

  }

  /**
   * A method which can be called to load a tilemap into the graphics world.
   * It will load the map at a specified grid index.
   *
   * @param sectionFileName
   * @param xIndex
   * @param yIndex
   */
  private void loadPyxelEditSection(String sectionFileName, int xIndex, int yIndex) {
    try {
      Platform platform = JmeSystem.getPlatform();
      InputStream levelInputStream = null;
      try {
        if (mobile) {
          levelInputStream = JmeSystem.getResourceAsStream("/assets/" + sectionFileName);

        } else {
          levelInputStream = JmeSystem.getResourceAsStream("/" + sectionFileName);
        }
      } catch (UnsupportedOperationException e) {
        Logger.getLogger(PyxelEditGame.class.getName()).log(java.util.logging.Level.INFO, null, e);
        //Load the default
        levelInputStream = JmeSystem.getResourceAsStream("/assets/" + sectionFileName);

      }

      if (levelInputStream != null) {
        InputStreamReader reader = new InputStreamReader(levelInputStream);
        JsonReader jsonReader = new JsonReader(reader);
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(jsonReader);

        parseTileMap(jsonElement, xIndex, yIndex);
      }

    } catch (UnsupportedOperationException e) {
      e.printStackTrace();
    }

  }

  private void parseTileMap(JsonElement jsonElement, int sectionXIndex, int sectionYIndex) {
    JsonObject parentObject = jsonElement.getAsJsonObject();

    //Get the map size
    tileWidth = parentObject.get("tilewidth").getAsInt();
    tileHeight = parentObject.get("tileheight").getAsInt();
    mapWidth = parentObject.get("tileswide").getAsInt();
    mapHeight = parentObject.get("tileshigh").getAsInt();

    JsonArray layersArray = parentObject.get("layers").getAsJsonArray();
    //System.out.println("Found Layers: " + layersArray.size());
    layerCount = layersArray.size();
//    if (layers != layerCount) {
//      throw new RuntimeException("Invalid amount of layers in section file!");
//    }

    if (layersArray != null && layersArray.size() > 0) {
      for (int i = 0; i < layersArray.size(); i++) {
        parsePyxelEditLayer(layersArray.get(i), sectionXIndex, sectionYIndex);
      }
    }

  }

  private void parsePyxelEditLayer(JsonElement jsonElement, int sectionXIndex, int sectionYIndex) {
    JsonObject parentObject = jsonElement.getAsJsonObject();

    //Get the map size
    String layerName = parentObject.get("name").getAsString();
    int layerNumber = parentObject.get("number").getAsInt();

    BatchNode layerNode = new BatchNode(layerName);
    levelNode.attachChild(layerNode);

    JsonArray tilesArray = parentObject.get("tiles").getAsJsonArray();
//    System.out.println("Found tiles: " + tilesArray.size());
    if (tilesArray != null && tilesArray.size() > 0) {
      for (int i = 0; i < tilesArray.size(); i++) {
        parsePyxelEditTile(layerNode, tilesArray.get(i), layerNumber, sectionXIndex, sectionYIndex);
      }
    }

    //If it is the terrain layer we batch the tiles else we don't
    if (layerName.equals(pyxelEditLevel.getTerrainName()) ||
            layerName.equals(pyxelEditLevel.getBackgroundName())) {
      layerNode.batch();
    }

//    fixMaterial(layerNode);

  }

  /**
   * Parse the specific tile on a pyxel edit tilemap
   *
   * @param parent
   * @param jsonElement
   * @param layerNumber
   * @param sectionXIndex
   * @param sectionYIndex
   */
  private void parsePyxelEditTile(Node parent, JsonElement jsonElement, int layerNumber, int sectionXIndex, int sectionYIndex) {
    JsonObject parentObject = jsonElement.getAsJsonObject();

    //Get tile parameters
    int tileIndex = parentObject.get("tile").getAsInt();
    int x = parentObject.get("x").getAsInt();
    int y = parentObject.get("y").getAsInt();
    boolean flipX = parentObject.get("flipX").getAsBoolean();
    int index = parentObject.get("index").getAsInt();
    int rot = parentObject.get("rot").getAsInt();

    //Only add the tile if it has a valid tile index. -1 is invalid and meas it is blank at the position
    if (tileIndex >= 0) {
//      System.out.println("Found tile " + tileIndex + " at pos (" + x + ", " + y + ") with rotation " + rot);

      float xPos = ((float) sectionXIndex * mapWidth) + ((float) x) * pyxelEditLevel.getTileSize();
      float yPos = (((float) sectionYIndex * mapHeight) + (((float) mapHeight) * pyxelEditLevel.getTileSize()) - (((float) y) * pyxelEditLevel.getTileSize())) - (pyxelEditLevel.getLevelSectionCountRows() * mapHeight) + (mapHeight * 0.5f);
      float zPos = (((float) layerCount) - ((float) layerNumber)) * 0.1f;
//      System.out.println("***********************************");
//      System.out.println("\t-sectionXIndex = " + sectionXIndex);
//      System.out.println("\t-sectionYIndex = " + sectionYIndex);

      if (pyxelEditLevel.isVerticalSectionIncrease()) {
        yPos = (((float) sectionYIndex * mapHeight) + (((float) mapHeight) * pyxelEditLevel.getTileSize())
                - (((float) y) * pyxelEditLevel.getTileSize()))
                - (mapHeight * 0.5f);
//                - (pyxelEditLevel.getLevelSectionCountRows() * mapHeight)


      }

      float angle = 0;
      if (rot == 1) angle = -90;
      if (rot == 2) angle = -180;
      if (rot == 3) angle = -270;

      //First we see if we have to load spawn point layer
      if (parent.getName().equals(pyxelEditLevel.getSpawnPointsName())) {
        loadPyxelEditSpawnPoint(parent, xPos, yPos, zPos);
        loadPyxelEditTile(parent, xPos, yPos, zPos, angle, flipX, tileIndex, false, TYPE_START);

      } else {
        //Load the normal tiles
        //Check for terrain
        if (parent.getName().equals(pyxelEditLevel.getTerrainName())) {
          loadPyxelEditTile(parent, xPos, yPos, zPos, angle, flipX, tileIndex, true, TYPE_TERRAIN);

        } else if (parent.getName().equals(pyxelEditLevel.getObstacleName())) {
          loadPyxelEditTile(parent, xPos, yPos, zPos, angle, flipX, tileIndex, true, TYPE_OBSTACLE);

        } else {
          loadPyxelEditTile(parent, xPos, yPos, zPos, angle, flipX, tileIndex, false, parent.getName());

        }

        //TODO: Parse the different type of tiles here

      }

    }

  }

  public SimpleApplication getApplication() {
    return application;
  }

  public Node getLevelNode() {
    return levelNode;
  }

  public Dyn4jAppState getDyn4jAppState() {
    return dyn4jAppState;
  }

  private void loadPyxelEditSpawnPoint(Node parent, float x, float y, float z) {
    spawnPoints.add(new Vector3f(x, y, z));
  }

  private void loadPyxelEditTile(Node parent, float x, float y, float z, float rotation, boolean flip, int tileIndex, boolean physics, String name) {

    int columnIndex = tileIndex % pyxelEditLevel.getTilesetColumns();
    int rowIndex = tileIndex / pyxelEditLevel.getTilesetColumns();
//
//    System.out.println("Loading tile " + tileIndex + " at pos (" + x + ", " + y + ") with rotation " + rotation);
//    System.out.println("Col Index: " + columnIndex);
//    System.out.println("Row Index: " + rowIndex);

    //Load the floor
    Sprite sprite = new Sprite(pyxelEditLevel.getTileSize(), pyxelEditLevel.getTileSize(), pyxelEditLevel.getTilesetColumns(), pyxelEditLevel.getTilesetRows(), columnIndex, rowIndex);
    Geometry geometry = new Geometry(name, sprite);
    geometry.setMaterial(tilesetMaterial);
    geometry.setUserData(TYPE, name);
    parent.attachChild(geometry);
    geometry.move(x, y, z);
    geometry.setQueueBucket(RenderQueue.Bucket.Transparent);
    geometry.rotate(0, 0, FastMath.DEG_TO_RAD * rotation);

    //For the floor sprite it will be a static body with 0 mass
    if (physics) {
      RigidBodyControl2D rbcFloor = new RigidBodyControl2D(new BoxCollisionShape(sprite.getWidth(), sprite.getHeight()), 0);
      geometry.addControl(rbcFloor);
      rbcFloor.move(x, y);
      rbcFloor.setPhysicsRotation(FastMath.DEG_TO_RAD * rotation);

      TileOptimizer tileOptimizer = new TileOptimizer(this, rbcFloor, pyxelEditLevel.getTileActiveDistance());
      geometry.addControl(tileOptimizer);
      rbcFloor.setFriction(0.5f);

    }

    if (flip) {
      sprite.flipCoords(true);
    }

    parseTile(geometry, tileIndex);

  }

  public void start(PyxelEditPlayer player) {
    this.player = player;
    loading = false;
    started = true;
    paused = false;
    gameOver = false;
    dyn4jAppState.setEnabled(true);
  }

  public PyxelEditPlayer getPlayer() {
    return player;
  }

  public void setDebugEnabled(boolean enabled) {
    this.dyn4JDebugAppState.setEnabled(enabled);
    this.application.setDisplayStatView(enabled);
  }

  public Node getRootNode() {
    return rootNode;
  }

  public List<Vector3f> getSpawnPoints() {
    return spawnPoints;
  }

  public void setSpawnPoints(List<Vector3f> spawnPoints) {
    this.spawnPoints = spawnPoints;
  }

  public Vector3f getRandomSpawnPoint() {
    if (spawnPoints != null && spawnPoints.size() > 0) {
      int spawnPointIndex = (int) nextFloat(0, spawnPoints.size());
      return spawnPoints.get(spawnPointIndex);
    } else {
      return new Vector3f(0, 0, 0);
    }
  }

  public void doGameOver() {
    started = true;
    paused = false;
    gameOver = true;
    fireGameOverListener();
  }

  public void doLevelCompleted() {
    started = false;
    paused = true;
    gameOver = true;
    fireGameCompletedListener();
  }

  public void addGameListener(PyxelEditGameListener gameListener) {
    this.gameListener = gameListener;
  }

  protected void fireGameOverListener() {
    if (gameListener != null) {
      gameListener.doGameOver();
    }
  }

  protected void fireGameCompletedListener() {
    if (gameListener != null) {
      gameListener.doGameCompleted();
    }
  }

  public void fireScoreChangedListener(int score) {
    if (gameListener != null) {
      gameListener.doScoreChanged(score);
    }
  }

  public boolean isStarted() {
    return started;
  }

  public boolean isPaused() {
    return paused;
  }

  public boolean isGameOver() {
    return gameOver;
  }

  public boolean isLoading() {
    return loading;
  }
}
