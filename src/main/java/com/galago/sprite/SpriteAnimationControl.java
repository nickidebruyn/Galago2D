package com.galago.sprite;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

import java.util.HashMap;
import java.util.Map;

/**
 * The Sprite animation control will assist with the handling of multiple tile sheet frames per sprite.
 * Add an animation by giving it a name and a sequence of image indexes
 *
 * @author NideBruyn
 */
public class SpriteAnimationControl extends AbstractControl {

  private Sprite sprite;
  private Map<String, int[]> animations = new HashMap<>();
  private int[] currentAnimation;
  private int currentIndex;
  private String currentAnimationName;
  private float animationFrameTime = 1f;
  private float elapsedeTime = 0f;

  private boolean loop = true;

  private AnimationCallback animationCallback;

  public SpriteAnimationControl(Sprite sprite) {
    this.sprite = sprite;
  }

  public AnimationCallback getAnimationCallback() {
    return animationCallback;
  }

  public void setAnimationCallback(AnimationCallback animationCallback) {
    this.animationCallback = animationCallback;
  }

  protected void fireAnimationCallback(Spatial s) {
    if (animationCallback != null) {
      animationCallback.done(s);

    }
  }

  @Override
  protected void controlUpdate(float tpf) {

    if (sprite != null && currentAnimation != null) {

      elapsedeTime += tpf;
      if (elapsedeTime >= animationFrameTime) {
        currentIndex++;
        if (currentIndex >= currentAnimation.length) {
          if (loop) {
            currentIndex = 0;
          } else {
            currentIndex = currentAnimation.length-1;
          }

          fireAnimationCallback(getSpatial());
        }
        elapsedeTime = 0f;
        sprite.showIndex(currentAnimation[currentIndex]);
      }

    }

  }

  @Override
  protected void controlRender(RenderManager rm, ViewPort vp) {

  }

  public void addAnimation(String name, int[] frames) {
    if (!animations.containsKey(name)) {
      animations.put(name, frames);
    }

  }

  public void playAnimation(String name, float timePerFrame) {
    playAnimation(name, timePerFrame, true);

  }

  public void playAnimation(String name, float timePerFrame, boolean looped) {
    int[] seq = animations.get(name);

    if (seq != null && !name.equals(currentAnimationName)) {
      loop = looped;
      currentAnimationName = name;
      sprite.showIndex(seq[0]);
      currentAnimation = seq;
      animationFrameTime = timePerFrame;
      currentIndex = 0;
      elapsedeTime = 0f;
    }

  }

  public void stopAnimation() {
    currentAnimation = null;
  }

  public Map<String, int[]> getAnimations() {
    return animations;
  }

  public int[] getCurrentAnimation() {
    return currentAnimation;
  }

  public String getCurrentAnimationName() {
    return currentAnimationName;
  }
}
