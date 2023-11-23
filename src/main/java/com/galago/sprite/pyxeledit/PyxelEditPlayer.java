package com.galago.sprite.pyxeledit;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public abstract class PyxelEditPlayer {

  protected PyxelEditGame game;
  protected Node playerNode;
  protected Vector3f startPosition;
  protected int health = 0;
  protected int score = 0;
  protected int maximumHealth = 3;

  public PyxelEditPlayer(PyxelEditGame game, int health) {
    this.game = game;
    this.maximumHealth = health;
    this.health = health;
  }

  public void load() {
    this.startPosition = game.getRandomSpawnPoint().clone();

    //Load the player models
    playerNode = new Node(PyxelEditGame.TYPE_PLAYER);
    playerNode.setLocalTranslation(this.startPosition);
    game.getLevelNode().attachChild(playerNode);

    init();
  }

  protected abstract void init();

  protected abstract float getSize();

  public void start() {

  }

  public void log(String text) {
    System.out.println(text);
  }

  public void close() {
    playerNode.removeFromParent();
  }

  public PyxelEditGame getGame() {
    return game;
  }

  public Node getPlayerNode() {
    return playerNode;
  }

  public abstract Vector3f getPosition();

  public void doDamage(int hits) {
    health -= hits;

    if (health <= 0) {
      health = 0;
      game.doGameOver();
      doDie();
    }
  }

  public int getHealth() {
    return health;
  }

  public void setHealth(int health) {
    this.health = health;
  }

  public int getMaximumHealth() {
    return maximumHealth;
  }

  public void setMaximumHealth(int maximumHealth) {
    this.maximumHealth = maximumHealth;
  }

  public boolean addHealth() {
    if (health < maximumHealth) {
      health++;
      return true;
    }
    return false;
  }

  public int getScore() {
    return score;
  }

  public void addScore(int score) {
    this.score += score;
    game.fireScoreChangedListener(this.score);
  }

  public abstract void doDie();

}
