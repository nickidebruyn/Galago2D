package com.galago.sprite.utils;

import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;

public class SpriteUtils {

  /**
   * Local method for reuse of loading a texture as an unshaded material
   *
   * @param texture
   * @return
   */
  public static Material loadMaterial(AssetManager assetManager, String texture) {
    Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    Texture tex = assetManager.loadTexture(new TextureKey(texture, false));
    tex.setMagFilter(Texture.MagFilter.Nearest);
    tex.setWrap(Texture.WrapMode.Repeat);
    mat.setTexture("ColorMap", tex);
    mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
    return mat;
  }

  /**
   * Local method for reuse of loading a texture as an unshaded material
   *
   * @param colorRGBA
   * @return
   */
  public static Material loadMaterial(AssetManager assetManager, ColorRGBA colorRGBA) {
    Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat.setColor("Color", colorRGBA);
    mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
    return mat;
  }

  /**
   * Loads an unshaded texture
   * @param assetManager
   * @param file
   * @return
   */
  public static Texture loadTexture(AssetManager assetManager, String file) {
    Texture tex = assetManager.loadTexture(new TextureKey(file, false));
    tex.setMagFilter(Texture.MagFilter.Nearest);
    tex.setWrap(Texture.WrapMode.Repeat);
    return tex;
  }

}
