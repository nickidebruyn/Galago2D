package com.galago.sprite.pyxeledit;

import com.jme3.math.ColorRGBA;

public class PyxelEditLevel {

  private String tilesetName;
  private String tilesetGlowName;
  private int tilesetColumns = 6;
  private int tilesetRows = 6;
  private float tileSize = 1f;
  private float backgroundScale = 1f;
  private int levelSectionCountColumns = 1;
  private int levelSectionCountRows = 1;
  private String[] sections = new String[]{"Textures/section1.json"};
  private String[] backgrounds;
  private String foregroundName = "foreground";
  private String backgroundName = "background";
  private String terrainName = "terrain";
  private String obstacleName = "obstacle";
  private String spawnPointsName = "spawn";
  private boolean unlitLevel = true;
  private float tileActiveDistance = 5f;
  private ColorRGBA backgroundColor = ColorRGBA.Black;

  private int seed = 0;

  private boolean verticalSectionIncrease = false;

  public String getTilesetName() {
    return tilesetName;
  }

  public void setTilesetName(String tilesetName) {
    this.tilesetName = tilesetName;
  }

  public String getTilesetGlowName() {
    return tilesetGlowName;
  }

  public void setTilesetGlowName(String tilesetGlowName) {
    this.tilesetGlowName = tilesetGlowName;
  }

  public float getTileSize() {
    return tileSize;
  }

  public void setTileSize(float tileSize) {
    this.tileSize = tileSize;
  }

  public int getLevelSectionCountColumns() {
    return levelSectionCountColumns;
  }

  public void setLevelSectionCountColumns(int levelSectionCountColumns) {
    this.levelSectionCountColumns = levelSectionCountColumns;
  }

  public int getLevelSectionCountRows() {
    return levelSectionCountRows;
  }

  public void setLevelSectionCountRows(int leveSectionCountRows) {
    this.levelSectionCountRows = leveSectionCountRows;
  }

  public String[] getSections() {
    return sections;
  }

  public void setSections(String[] sections) {
    this.sections = sections;
  }

  public int getTilesetColumns() {
    return tilesetColumns;
  }

  public void setTilesetColumns(int tilesetColumns) {
    this.tilesetColumns = tilesetColumns;
  }

  public int getTilesetRows() {
    return tilesetRows;
  }

  public void setTilesetRows(int tilesetRows) {
    this.tilesetRows = tilesetRows;
  }

  public String getForegroundName() {
    return foregroundName;
  }

  public void setForegroundName(String foregroundName) {
    this.foregroundName = foregroundName;
  }

  public String getBackgroundName() {
    return backgroundName;
  }

  public void setBackgroundName(String backgroundName) {
    this.backgroundName = backgroundName;
  }

  public String getTerrainName() {
    return terrainName;
  }

  public void setTerrainName(String terrainName) {
    this.terrainName = terrainName;
  }

  public String[] getBackgrounds() {
    return backgrounds;
  }

  public void setBackgrounds(String[] backgrounds) {
    this.backgrounds = backgrounds;
  }

  public String getSpawnPointsName() {
    return spawnPointsName;
  }

  public void setSpawnPointsName(String spawnPointsName) {
    this.spawnPointsName = spawnPointsName;
  }

  public float getBackgroundScale() {
    return backgroundScale;
  }

  public void setBackgroundScale(float backgroundScale) {
    this.backgroundScale = backgroundScale;
  }

  public boolean isUnlitLevel() {
    return unlitLevel;
  }

  public void setUnlitLevel(boolean unlitLevel) {
    this.unlitLevel = unlitLevel;
  }

  public String getObstacleName() {
    return obstacleName;
  }

  public void setObstacleName(String obstacleName) {
    this.obstacleName = obstacleName;
  }

  public float getTileActiveDistance() {
    return tileActiveDistance;
  }

  public void setTileActiveDistance(float tileActiveDistance) {
    this.tileActiveDistance = tileActiveDistance;
  }

  public ColorRGBA getBackgroundColor() {
    return backgroundColor;
  }

  public void setBackgroundColor(ColorRGBA backgroundColor) {
    this.backgroundColor = backgroundColor;
  }

  public int getSeed() {
    return seed;
  }

  public void setSeed(int seed) {
    this.seed = seed;
  }

  public boolean isVerticalSectionIncrease() {
    return verticalSectionIncrease;
  }

  public void setVerticalSectionIncrease(boolean verticalSectionIncrease) {
    this.verticalSectionIncrease = verticalSectionIncrease;
  }
}
