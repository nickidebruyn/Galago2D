package com.galago.sprite.tiles;

import com.galago.sprite.Sprite;
import com.galago.sprite.physics.PhysicsSpace;
import com.galago.sprite.physics.RigidBodyControl2D;
import com.galago.sprite.physics.shape.BoxCollisionShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.ArrayList;
import java.util.List;

public class TileSet {

  public static final float TILE_SIZE = 1f;

  private PhysicsSpace physicsSpace;

  private Material material;
  private int tilesWide;
  private int tilesHigh;

  private List<TileRule> tileRules = new ArrayList<>();

  public TileSet(PhysicsSpace physicsSpace, Material material, int tilesWide, int tilesHigh) {
    this.physicsSpace = physicsSpace;
    this.material = material;
    this.tilesWide = tilesWide;
    this.tilesHigh = tilesHigh;
  }

  public void loadRule(int index, String rule) {
    tileRules.add(new TileRule(index, rule.replaceAll("0", "[1x]")));

  }

  /**
   * Helper method that can help determine if there is already a tile at the given location
   *
   * @param layer
   * @param x
   * @param y
   * @return
   */
  public boolean hasTile(Node layer, float x, float y) {
    boolean hasTile = false;

    if (layer.getQuantity() > 0) {
      for (int i = 0; i < layer.getQuantity(); i++) {
        Spatial child = layer.getChild(i);
        if (child.getLocalTranslation().x == x && child.getLocalTranslation().y == y) {
          hasTile = true;
          break;
        }

      }
    }

    return hasTile;
  }

  /**
   * Return a tile at that location if it exist
   * @param layer
   * @param x
   * @param y
   * @return
   */
  public Geometry getTile(Node layer, float x, float y) {
    Geometry tile = null;

    if (layer.getQuantity() > 0) {
      for (int i = 0; i < layer.getQuantity(); i++) {
        Spatial child = layer.getChild(i);
        if (child.getLocalTranslation().x == x && child.getLocalTranslation().y == y) {
          tile = (Geometry) child;
          break;
        }

      }
    }

    return tile;
  }

  /**
   * Help method that will remove a tile on the tile system if a tile exist
   *
   * @param layer
   * @param x
   * @param y
   */
  public void removeTile(Node layer, float x, float y) {

    int index = -1;

    if (layer.getQuantity() > 0) {
      for (int i = 0; i < layer.getQuantity(); i++) {
        Spatial child = layer.getChild(i);
        if (child.getLocalTranslation().x == x && child.getLocalTranslation().y == y) {
          index = i;
          break;
        }
      }
    }

    if (index >= 0) {
      layer.detachChildAt(index);
    }
  }

  /**
   * Helper method which will add a tile to a given location.
   * This will work specifically for a tile based system
   *
   * @param layer
   * @param x
   * @param y
   * @param colorRGBA
   */
  public void addTile(Node layer, float x, float y, ColorRGBA colorRGBA, int index) {

    Geometry tileGeom = getTile(layer, x, y);

    if (tileGeom == null) {
      //Load the sprite
      Sprite sprite = new Sprite(TILE_SIZE, TILE_SIZE, getTilesWide(), getTilesHigh(), index);
      tileGeom = new Geometry("tile", sprite);
      tileGeom.setMaterial(getMaterial());
      tileGeom.setQueueBucket(RenderQueue.Bucket.Transparent);
      layer.attachChild(tileGeom);
      tileGeom.move(x, y, 0);
      //Load the physics body and add to the physics world
      RigidBodyControl2D rbc = new RigidBodyControl2D(new BoxCollisionShape(sprite.getWidth(), sprite.getHeight()), 0);
      tileGeom.addControl(rbc);
      physicsSpace.add(rbc);
      rbc.move(x, y);
    }

    //If no rule exist for the given tile then just paint the tile
    TileRule tileRule = getTileRuleByIndex(index);

    if (tileRule != null) {
      //TODO: Check the neighbouring tiles and update them

    }

  }

  /**
   * Get the tile rule for the index on the tile set
   * @param index
   * @return
   */
  public TileRule getTileRuleByIndex(int index) {
    for (TileRule tileRule : tileRules) {
      if (tileRule.getIndex() == index) {
        return tileRule;
      }
    }

    return null;
  }

  public Material getMaterial() {
    return material;
  }

  public void setMaterial(Material material) {
    this.material = material;
  }

  public int getTilesWide() {
    return tilesWide;
  }

  public void setTilesWide(int tilesWide) {
    this.tilesWide = tilesWide;
  }

  public int getTilesHigh() {
    return tilesHigh;
  }

  public void setTilesHigh(int tilesHigh) {
    this.tilesHigh = tilesHigh;
  }
}
