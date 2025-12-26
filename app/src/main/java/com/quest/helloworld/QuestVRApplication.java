package com.quest.helloworld;

import android.util.Log;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;

/**
 * Main jMonkeyEngine application for Quest 3 VR.
 * Simplified version with high-contrast visible scene.
 */
public class QuestVRApplication extends SimpleApplication {

    private static final String TAG = "QuestVRApp";
    
    private Geometry cube;
    private float time = 0f;

    public QuestVRApplication() {
        super();
        Log.i(TAG, "QuestVRApplication constructor called");
    }

    @Override
    public void simpleInitApp() {
        Log.i(TAG, "=== simpleInitApp() STARTED ===");
        
        try {
            // BRIGHT BLUE background so we can see if rendering works
            viewPort.setBackgroundColor(new ColorRGBA(0.2f, 0.4f, 0.8f, 1.0f));
            Log.i(TAG, "Background color set to BRIGHT BLUE");
            
            // Disable fly camera
            flyCam.setEnabled(false);
            Log.i(TAG, "FlyCam disabled");
            
            // Hide stats
            setDisplayStatView(false);
            setDisplayFps(false);
            
            // Setup camera - further back to see more
            cam.setLocation(new Vector3f(0, 2, 10));
            cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
            cam.setFrustumPerspective(60f, (float)cam.getWidth() / cam.getHeight(), 0.1f, 1000f);
            Log.i(TAG, "Camera at (0,2,10) looking at origin, FOV=60");
            
            // Add BRIGHT lights
            setupLighting();
            
            // Create a LARGE visible cube
            createCube();
            
            // Create large text
            createText();
            
            // Create a floor for reference
            createFloor();
            
            Log.i(TAG, "=== simpleInitApp() COMPLETED ===");
            Log.i(TAG, "Viewport size: " + cam.getWidth() + "x" + cam.getHeight());
            
        } catch (Exception e) {
            Log.e(TAG, "ERROR in simpleInitApp: " + e.getMessage(), e);
            e.printStackTrace();
        }
    }
    
    private void setupLighting() {
        // Very bright ambient light
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(0.8f));
        rootNode.addLight(ambient);
        
        // Bright directional light
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.5f, -1f, -0.5f).normalizeLocal());
        sun.setColor(ColorRGBA.White.mult(1.5f));
        rootNode.addLight(sun);
        
        Log.i(TAG, "Bright lighting configured");
    }
    
    private void createCube() {
        // Create a LARGE 2x2x2 cube
        Box box = new Box(1f, 1f, 1f);
        cube = new Geometry("Cube", box);
        
        // BRIGHT ORANGE unlit material for maximum visibility
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(1.0f, 0.5f, 0.0f, 1.0f));  // Bright orange
        cube.setMaterial(mat);
        
        // Position at origin
        cube.setLocalTranslation(0, 1, 0);
        
        rootNode.attachChild(cube);
        Log.i(TAG, "Large orange cube created at (0,1,0)");
    }
    
    private void createFloor() {
        // Create a large floor
        Box floorBox = new Box(10f, 0.1f, 10f);
        Geometry floor = new Geometry("Floor", floorBox);
        
        // Gray unshaded material
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(0.3f, 0.3f, 0.3f, 1.0f));
        floor.setMaterial(mat);
        
        floor.setLocalTranslation(0, -0.1f, 0);
        rootNode.attachChild(floor);
        Log.i(TAG, "Gray floor created");
    }
    
    private void createText() {
        try {
            BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");
            BitmapText text = new BitmapText(font, false);
            text.setSize(1.0f);  // Large text
            text.setColor(ColorRGBA.Yellow);  // Bright yellow
            text.setText("HELLO QUEST 3!");
            
            // Position above the cube
            float textWidth = text.getLineWidth();
            text.setLocalTranslation(-textWidth / 2f, 4f, 0);
            
            rootNode.attachChild(text);
            Log.i(TAG, "Yellow text 'HELLO QUEST 3!' created above cube");
        } catch (Exception e) {
            Log.e(TAG, "Error creating text: " + e.getMessage(), e);
        }
    }

    private int frameCount = 0;
    
    @Override
    public void simpleUpdate(float tpf) {
        time += tpf;
        frameCount++;
        
        // Log every 100 frames to verify render loop is running
        if (frameCount % 100 == 0) {
            Log.i(TAG, "Frame " + frameCount + " rendered, time=" + time);
        }
        
        // Rotate the cube slowly
        if (cube != null) {
            cube.rotate(0, tpf * 0.3f, 0);
        }
    }

    @Override
    public void destroy() {
        Log.i(TAG, "QuestVRApplication destroy() called");
        super.destroy();
    }
}
