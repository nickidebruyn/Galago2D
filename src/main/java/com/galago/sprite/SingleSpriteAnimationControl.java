package com.galago.sprite;

import com.jme3.material.Material;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.control.AbstractControl;
import com.jme3.texture.Texture;

import java.util.HashMap;
import java.util.Map;

/**
 * The Sprite animation control will assist with the handling switching multiple textures over time
 * Add an animation by giving it a name and a sequence of image indexes
 *
 * @author NideBruyn
 */
public class SingleSpriteAnimationControl extends AbstractControl {

  private Sprite sprite;
  private Material spriteMaterial;
  private Map<String, Texture[]> animations = new HashMap<>();
  private Texture[] currentAnimation;
  private int currentIndex;
  private String currentAnimationName;
  private float animationFrameTime = 1f;
  private float elapsedeTime = 0f;

  public SingleSpriteAnimationControl(Sprite sprite) {
    this.sprite = sprite;
  }


  @Override
  protected void controlUpdate(float tpf) {

    if (spriteMaterial == null) {
      spriteMaterial = ((Geometry) spatial).getMaterial();
      if (currentAnimation != null) {
        playAnimation(currentAnimationName, animationFrameTime);

      }
    }

    if (sprite != null && currentAnimation != null) {

      elapsedeTime += tpf;
      if (elapsedeTime >= animationFrameTime) {
        currentIndex++;
        if (currentIndex >= currentAnimation.length) {
          currentIndex = 0;
        }
        elapsedeTime = 0f;
        spriteMaterial.setTexture("ColorMap", currentAnimation[currentIndex]);
//        sprite.showIndex(currentAnimation[currentIndex]);
      }

    }

  }

  @Override
  protected void controlRender(RenderManager rm, ViewPort vp) {

  }

  public void addAnimation(String name, Texture[] frames) {
    if (!animations.containsKey(name)) {
      animations.put(name, frames);
    }

  }

  public void playAnimation(String name, float timePerFrame) {
    Texture[] seq = animations.get(name);

    if (seq != null && !name.equals(currentAnimationName)) {
      currentAnimationName = name;

      if (spriteMaterial != null)
        spriteMaterial.setTexture("ColorMap", currentAnimation[0]);

      currentAnimation = seq;
      animationFrameTime = timePerFrame;
      currentIndex = 0;
      elapsedeTime = 0f;
    }

  }

}
