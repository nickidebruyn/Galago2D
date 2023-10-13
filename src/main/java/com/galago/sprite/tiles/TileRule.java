package com.galago.sprite.tiles;

/**
 * A tile rule represents a specific rule for a tile on the tileset.
 */
public class TileRule {

  private int index; //Index is normally from 0 to (tileWide x tileHigh)

  //rule example: xxx-x1x-xxx, 0x0-x11-0x0;
  //x = is blank
  //1 = not blank
  //0 = anything/any tile
  private String rule;

  public TileRule(int index, String rule) {
    this.index = index;
    this.rule = rule;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public String getRule() {
    return rule;
  }

  public void setRule(String rule) {
    this.rule = rule;
  }
}
