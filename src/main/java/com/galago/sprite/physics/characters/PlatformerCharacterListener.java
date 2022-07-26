package com.galago.sprite.physics.characters;

public interface PlatformerCharacterListener {

  public void doCharacterIdleEvent();

  public void doCharacterWalkEvent(float direction);

  public void doCharacterJumpEvent(int count);

}
