package com.galago.sprite.utils;

import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;

public class MaterialUtils {

  public static Material loadLitPixelatedMaterial(AssetManager assetManager, String texture, String glowTexture) {
    Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
    Texture tex = assetManager.loadTexture(new TextureKey(texture, false));
    tex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
    tex.setMagFilter(Texture.MagFilter.Nearest);
    tex.setWrap(Texture.WrapMode.Repeat);
    mat.setTexture("DiffuseMap", tex);
    if (glowTexture != null) {
      Texture glowtex = assetManager.loadTexture(new TextureKey(glowTexture, false));
      glowtex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
      glowtex.setMagFilter(Texture.MagFilter.Nearest);
      glowtex.setWrap(Texture.WrapMode.Repeat);
      mat.setTexture("GlowMap", glowtex);

    }
    mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
    mat.setFloat("AlphaDiscardThreshold", 0.55f);
    return mat;
  }

  public static Material loadUnlitPixelatedMaterial(AssetManager assetManager, String texture, String glowTexture) {
    Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    Texture tex = assetManager.loadTexture(new TextureKey(texture, false));
    tex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
    tex.setMagFilter(Texture.MagFilter.Nearest);
    tex.setWrap(Texture.WrapMode.Repeat);
    mat.setTexture("ColorMap", tex);
    if (glowTexture != null) {
      Texture glowtex = assetManager.loadTexture(new TextureKey(glowTexture, false));
      glowtex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
      glowtex.setMagFilter(Texture.MagFilter.Nearest);
      glowtex.setWrap(Texture.WrapMode.Repeat);
      mat.setTexture("GlowMap", glowtex);

    }

    mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
    mat.setFloat("AlphaDiscardThreshold", 0.15f);
    return mat;
  }

  public static void updateToPixelatedMaterial(Node node) {

    SceneGraphVisitor sceneGraphVisitor = new SceneGraphVisitor() {
      @Override
      public void visit(Spatial spatial) {
        if (spatial instanceof Geometry) {
          Geometry geometry = ((Geometry) spatial);
          if (geometry.getMaterial() != null) {
            Material layerMaterial = geometry.getMaterial();

            MatParam matParam = layerMaterial.getParam("DiffuseMap");
            Texture texture = (Texture) matParam.getValue();
            texture.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
            texture.setMagFilter(Texture.MagFilter.Nearest);
            layerMaterial.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            layerMaterial.setFloat("AlphaDiscardThreshold", 0.55f);
          }
          geometry.setQueueBucket(RenderQueue.Bucket.Transparent);
        }

      }
    };
    node.depthFirstTraversal(sceneGraphVisitor);

  }

}
