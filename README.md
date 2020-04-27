# Galago2D
Galago 2D is a set of classes that can be used to develop a 2D game in jMonkeyEngine3.3.
It is a mapping library for jME3.3 to Dyn4J. 

It allows the user to create sprite images and add them to
the scene as well as adding a RigidBodyControl2D to a sprite for giving that sprite physics properties.


**Some features this library provides:**
1. Sprite Mesh
2. Sprite Animation Control
3. Dyn4jAppState for 2D physics world
4. Variety of collision shapes (BoxCollisionShape, CircleCollisionShape, EllipseCollisionShape, etc.)
5. Joints (Connect RigidBodies to one another with joints, RopeJoint, HingeJoint, FrictionJoint, DistanceJoint)
6. Vehicle support
7. Camera2DState which will set the camera in a 2D mode allowing Orthographical rendering
8. PlatformerCharacterControlState for controlling a character in 2D space.

## Example1:
In Example 1 I will show you how to create sprite meshes and add them to the root scene. 
You will also see the best way of how to apply an unshaded material to a sprite.

```java
package com.galago.sprite.example1;

import com.galago.sprite.Sprite;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Vector2f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.texture.Texture;

/**
 * This example will show you how to create simple sprites for the background,
 * floor and player.
 *
 *
 * @author NickideBruyn
 */
public class Example1 extends SimpleApplication {

    private Geometry player;     //The player spatial object
    private Geometry background; //The background spatial
    private Geometry floor;      //The floor

    /**
     * The main method for this java app when we run it.
     * @param args 
     */
    public static void main(String[] args) {
        Example1 app = new Example1();
        app.start();
    }

    @Override
    public void simpleInitApp() {

        //Load the background
        Sprite backgroundSprite = new Sprite(20, 10);
        background = new Geometry("background", backgroundSprite);
        Material material = loadMaterial("Textures/colored_desert.png");
        background.setMaterial(material);
        backgroundSprite.scaleTextureCoordinates(new Vector2f(2, 1));
        rootNode.attachChild(background);
        background.move(0, 0, -1);

        //Load the floor
        Sprite floorSprite = new Sprite(20, 1);
        floor = new Geometry("floor", floorSprite);
        material = loadMaterial("Textures/dirtMid.png");
        floor.setMaterial(material);
        floorSprite.scaleTextureCoordinates(new Vector2f(20, 1));
        rootNode.attachChild(floor);
        floor.move(0, -4f, 0);

        //Load the player
        Sprite playerSprite = new Sprite(1, 1);
        player = new Geometry("player", playerSprite);
        material = loadMaterial("Textures/slimeBlock.png");
        player.setMaterial(material);
        player.setQueueBucket(RenderQueue.Bucket.Transparent);
        rootNode.attachChild(player);
        player.move(0, -3f, 0);
    }

    /**
     * Local method for reuse of loading a texture as an unshaded material
     *
     * @param file
     * @return
     */
    protected Material loadMaterial(String file) {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex = assetManager.loadTexture(new TextureKey(file, false));
        tex.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("ColorMap", tex);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        return mat;
    }
}

```

