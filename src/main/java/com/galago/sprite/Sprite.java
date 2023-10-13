package com.galago.sprite;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;

/**
 *
 * This is a sprite mesh and will handle the rendering of sprite image with there uv coords in 2d.
 *
 * @author NideBruyn
 */
public class Sprite extends Mesh {

    private float width;
    private float height;
    private int rows = 1;
    private int columns = 1;
    private int colPosition = 0;
    private int rowPosition = 0;
    private boolean flipCoords = false;
    private float uvSpacing = 0.001f;

    /**
     * Serialization only. Do not use.
     */
    public Sprite() {
    }

    public Sprite(float width, float height) {
        this(width, height, 1, 1, 0, 0);
    }

    public Sprite(float width, float height, int columns, int rows, int colPosition, int rowPosition) {

        this.width = width;
        this.height = height;
        this.columns = columns;
        this.rows = rows;
        this.colPosition = colPosition;
        this.rowPosition = rowPosition;

        initializeMesh();

    }

    public Sprite(float width, float height, int columns, int rows, int index) {

        this.width = width;
        this.height = height;
        this.columns = columns;
        this.rows = rows;
        this.colPosition = index % columns;
        this.rowPosition = index / columns;

        initializeMesh();

    }

    private void initializeMesh() {
        // Vertex positions in space
        Vector3f[] vertices = new Vector3f[4];
        vertices[0] = new Vector3f(-width * 0.5f, -height * 0.5f, 0f);
        vertices[1] = new Vector3f(width * 0.5f, -height * 0.5f, 0f);
        vertices[2] = new Vector3f(-width * 0.5f, height * 0.5f, 0f);
        vertices[3] = new Vector3f(width * 0.5f, height * 0.5f, 0f);

        float colSize = 1f / (float) columns;
        float rowSize = 1f / (float) rows;

        // Texture coordinates
        Vector2f[] texCoord = new Vector2f[4];
        if (flipCoords) {
            texCoord[0] = new Vector2f(colSize * colPosition + colSize - uvSpacing, rowSize * rowPosition + uvSpacing);
            texCoord[1] = new Vector2f(colSize * colPosition + uvSpacing, rowSize * rowPosition + uvSpacing);
            texCoord[2] = new Vector2f(colSize * colPosition + colSize - uvSpacing, rowSize * rowPosition + rowSize - uvSpacing);
            texCoord[3] = new Vector2f(colSize * colPosition + uvSpacing, rowSize * rowPosition + rowSize - uvSpacing);

        } else {
            texCoord[0] = new Vector2f(colSize * colPosition + uvSpacing, rowSize * rowPosition + rowSize - uvSpacing);
            texCoord[1] = new Vector2f(colSize * colPosition + colSize - uvSpacing, rowSize * rowPosition + rowSize - uvSpacing);
            texCoord[2] = new Vector2f(colSize * colPosition + uvSpacing, rowSize * rowPosition + uvSpacing);
            texCoord[3] = new Vector2f(colSize * colPosition + colSize - uvSpacing, rowSize * rowPosition + uvSpacing);
        }

        // Indexes. We define the order in which mesh should be constructed
        short[] indexes = {2, 0, 1, 1, 3, 2};

        // Setting buffers
        setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        setBuffer(VertexBuffer.Type.Normal, 3, new float[]{0, 0, 1,
            0, 0, 1,
            0, 0, 1,
            0, 0, 1});
        setBuffer(VertexBuffer.Type.Index, 1, BufferUtils.createShortBuffer(indexes));
        updateBound();

    }

    /**
     * This method can be called if the user wants to flip the coordinates.
     *
     * @param flip
     */
    public void flipCoords(boolean flip) {
        this.flipCoords = flip;
        updateTextureCoords(colPosition, rowPosition);
    }

    public void updateTextureCoords(int colPosition, int rowPosition) {
        this.colPosition = colPosition;
        this.rowPosition = rowPosition;

        float colSize = 1f / (float) columns;
        float rowSize = 1f / (float) rows;

        // Texture coordinates
        Vector2f[] texCoord = new Vector2f[4];

        if (flipCoords) {
            texCoord[2] = new Vector2f(colSize * colPosition + colSize - uvSpacing, rowSize * rowPosition + uvSpacing);
            texCoord[3] = new Vector2f(colSize * colPosition + uvSpacing, rowSize * rowPosition + uvSpacing);
            texCoord[0] = new Vector2f(colSize * colPosition + colSize - uvSpacing, rowSize * rowPosition + rowSize - uvSpacing);
            texCoord[1] = new Vector2f(colSize * colPosition + uvSpacing, rowSize * rowPosition + rowSize - uvSpacing);

        } else {
            texCoord[0] = new Vector2f(colSize * colPosition + uvSpacing, rowSize * rowPosition + rowSize - uvSpacing);
            texCoord[1] = new Vector2f(colSize * colPosition + colSize - uvSpacing, rowSize * rowPosition + rowSize - uvSpacing);
            texCoord[2] = new Vector2f(colSize * colPosition + uvSpacing, rowSize * rowPosition + uvSpacing);
            texCoord[3] = new Vector2f(colSize * colPosition + colSize - uvSpacing, rowSize * rowPosition + uvSpacing);
        }
        
            
        // [2, 0, 1]  [1, 3, 2]
        
        //  
        //
        //

        setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
    }

    public boolean isFlipCoords() {
        return flipCoords;
    }

    public void showIndex(int index) {
        updateTextureCoords(index % columns, index / columns);
    }

    public void showIndex(int colPosition, int rowPosition) {
        updateTextureCoords(colPosition, rowPosition);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public int getColPosition() {
        return colPosition;
    }

    public int getRowPosition() {
        return rowPosition;
    }
}
