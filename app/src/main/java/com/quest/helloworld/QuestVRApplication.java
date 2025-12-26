package com.quest.helloworld;

import android.app.Activity;
import android.util.Log;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.FrameBuffer;
import com.quest.helloworld.vr.OpenXRBridge;
import com.quest.helloworld.vr.VRRenderer;

/**
 * VR-enabled jMonkeyEngine application for Quest 3.
 * Implements stereo rendering with head tracking.
 */
public class QuestVRApplication extends SimpleApplication {

    private static final String TAG = "QuestVRApp";
    
    // VR components
    private OpenXRBridge vrBridge;
    private VRRenderer vrRenderer;
    private boolean vrEnabled = false;
    
    // Stereo rendering
    private Camera leftEyeCam;
    private Camera rightEyeCam;
    private ViewPort leftEyeVP;
    private ViewPort rightEyeVP;
    
    // Scene objects
    private Geometry cube;
    private Node controllerLeft;
    private Node controllerRight;
    private float time = 0f;
    private int frameCount = 0;
    
    // Head tracking
    private Vector3f headPosition = new Vector3f(0, 1.6f, 0);
    private Quaternion headRotation = new Quaternion();

    public QuestVRApplication() {
        super();
        Log.i(TAG, "QuestVRApplication constructor");
    }

    @Override
    public void simpleInitApp() {
        Log.i(TAG, "=== simpleInitApp() STARTED ===");
        
        try {
            // Initialize VR
            initializeVR();
            
            // Set dark background
            viewPort.setBackgroundColor(new ColorRGBA(0.02f, 0.02f, 0.05f, 1.0f));
            
            // Disable default camera controls
            flyCam.setEnabled(false);
            
            // Hide stats
            setDisplayStatView(false);
            setDisplayFps(false);
            
            // Setup stereo cameras if VR is enabled
            if (vrEnabled) {
                setupStereoCameras();
            } else {
                // Position camera to see the scene (objects are at z=-2)
                cam.setLocation(new Vector3f(0, 1.6f, 2));
                cam.lookAt(new Vector3f(0, 1f, -2), Vector3f.UNIT_Y);
                Log.i(TAG, "Camera set at: " + cam.getLocation() + " looking at (0,1,-2)");
            }
            
            // Setup scene
            setupLighting();
            createScene();
            createControllerVisuals();
            
            Log.i(TAG, "=== simpleInitApp() COMPLETED ===");
            Log.i(TAG, "VR Enabled: " + vrEnabled);
            
        } catch (Exception e) {
            Log.e(TAG, "ERROR in simpleInitApp: " + e.getMessage(), e);
            e.printStackTrace();
        }
    }
    
    /**
     * Initialize VR system
     */
    private void initializeVR() {
        Log.i(TAG, "Initializing VR system...");
        
        vrBridge = new OpenXRBridge();
        vrRenderer = new VRRenderer();
        
        // Force mono rendering mode for 2D panel display
        // Stereo VR requires proper OpenXR integration which we don't have yet
        vrEnabled = false;
        Log.i(TAG, "Running in 2D panel mode - mono rendering");
    }
    
    /**
     * Setup stereo cameras for VR rendering
     */
    private void setupStereoCameras() {
        float ipd = vrRenderer.getIPD();
        float eyeOffset = ipd / 2.0f;
        
        // Create left eye camera
        leftEyeCam = cam.clone();
        leftEyeCam.setName("LeftEye");
        
        // Create right eye camera
        rightEyeCam = cam.clone();
        rightEyeCam.setName("RightEye");
        
        // Setup viewports for side-by-side rendering
        // Left eye renders to left half of screen
        viewPort.setEnabled(false);  // Disable main viewport
        
        leftEyeVP = renderManager.createMainView("LeftEye", leftEyeCam);
        leftEyeVP.setClearFlags(true, true, true);
        leftEyeVP.setBackgroundColor(new ColorRGBA(0.02f, 0.02f, 0.05f, 1.0f));
        leftEyeVP.attachScene(rootNode);
        
        rightEyeVP = renderManager.createMainView("RightEye", rightEyeCam);
        rightEyeVP.setClearFlags(true, true, true);
        rightEyeVP.setBackgroundColor(new ColorRGBA(0.02f, 0.02f, 0.05f, 1.0f));
        rightEyeVP.attachScene(rootNode);
        
        // Set viewport regions (side-by-side)
        leftEyeCam.setViewPort(0f, 0.5f, 0f, 1f);
        rightEyeCam.setViewPort(0.5f, 1f, 0f, 1f);
        
        Log.i(TAG, "Stereo cameras configured - IPD: " + ipd);
    }
    
    /**
     * Setup scene lighting
     */
    private void setupLighting() {
        // Ambient light
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(0.4f));
        rootNode.addLight(ambient);
        
        // Main directional light
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.5f, -1f, -0.5f).normalizeLocal());
        sun.setColor(ColorRGBA.White.mult(1.2f));
        rootNode.addLight(sun);
        
        // Fill light
        DirectionalLight fill = new DirectionalLight();
        fill.setDirection(new Vector3f(0.5f, 0.2f, 0.5f).normalizeLocal());
        fill.setColor(new ColorRGBA(0.4f, 0.4f, 0.6f, 1f));
        rootNode.addLight(fill);
        
        Log.i(TAG, "Lighting configured");
    }
    
    /**
     * Create the 3D scene
     */
    private void createScene() {
        // Create floor
        Box floorBox = new Box(5f, 0.05f, 5f);
        Geometry floor = new Geometry("Floor", floorBox);
        Material floorMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        floorMat.setBoolean("UseMaterialColors", true);
        floorMat.setColor("Diffuse", new ColorRGBA(0.2f, 0.2f, 0.25f, 1f));
        floorMat.setColor("Ambient", new ColorRGBA(0.1f, 0.1f, 0.12f, 1f));
        floor.setMaterial(floorMat);
        floor.setLocalTranslation(0, 0, 0);
        rootNode.attachChild(floor);
        
        // Create central cube
        Box box = new Box(0.3f, 0.3f, 0.3f);
        cube = new Geometry("Cube", box);
        Material cubeMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        cubeMat.setBoolean("UseMaterialColors", true);
        cubeMat.setColor("Diffuse", new ColorRGBA(1.0f, 0.4f, 0.1f, 1f));  // Orange
        cubeMat.setColor("Specular", ColorRGBA.White);
        cubeMat.setFloat("Shininess", 64f);
        cubeMat.setColor("Ambient", new ColorRGBA(0.3f, 0.1f, 0.03f, 1f));
        cube.setMaterial(cubeMat);
        cube.setLocalTranslation(0, 1f, -2f);
        rootNode.attachChild(cube);
        
        // Create floating spheres
        createSphere(-1.5f, 1.2f, -2.5f, 0.15f, new ColorRGBA(0.2f, 0.8f, 0.3f, 1f));  // Green
        createSphere(1.5f, 0.8f, -1.8f, 0.12f, new ColorRGBA(0.8f, 0.2f, 0.5f, 1f));   // Pink
        createSphere(0.5f, 1.5f, -3f, 0.1f, new ColorRGBA(0.3f, 0.5f, 1.0f, 1f));      // Blue
        createSphere(-0.8f, 0.6f, -1.5f, 0.08f, new ColorRGBA(1.0f, 0.9f, 0.2f, 1f));  // Yellow
        
        // Create text
        createText();
        
        Log.i(TAG, "Scene created");
    }
    
    private void createSphere(float x, float y, float z, float radius, ColorRGBA color) {
        Sphere sphere = new Sphere(24, 24, radius);
        Geometry geo = new Geometry("Sphere", sphere);
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Diffuse", color);
        mat.setColor("Specular", ColorRGBA.White.mult(0.5f));
        mat.setFloat("Shininess", 32f);
        mat.setColor("Ambient", color.mult(0.3f));
        geo.setMaterial(mat);
        geo.setLocalTranslation(x, y, z);
        rootNode.attachChild(geo);
    }
    
    private void createText() {
        try {
            BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");
            BitmapText text = new BitmapText(font, false);
            text.setSize(0.3f);
            text.setColor(new ColorRGBA(0.3f, 0.8f, 1.0f, 1f));  // Cyan
            text.setText("HELLO QUEST 3 VR!");
            
            float textWidth = text.getLineWidth();
            text.setLocalTranslation(-textWidth / 2f, 2.2f, -2f);
            rootNode.attachChild(text);
        } catch (Exception e) {
            Log.e(TAG, "Error creating text: " + e.getMessage());
        }
    }
    
    /**
     * Create visual representations of VR controllers
     */
    private void createControllerVisuals() {
        // Left controller
        controllerLeft = new Node("LeftController");
        Box controllerBox = new Box(0.03f, 0.02f, 0.1f);
        Geometry leftGeo = new Geometry("LeftControllerGeo", controllerBox);
        Material leftMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        leftMat.setColor("Color", new ColorRGBA(0.2f, 0.5f, 1.0f, 1f));
        leftGeo.setMaterial(leftMat);
        controllerLeft.attachChild(leftGeo);
        controllerLeft.setLocalTranslation(-0.3f, 1.0f, -0.5f);
        rootNode.attachChild(controllerLeft);
        
        // Right controller
        controllerRight = new Node("RightController");
        Geometry rightGeo = new Geometry("RightControllerGeo", controllerBox);
        Material rightMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        rightMat.setColor("Color", new ColorRGBA(1.0f, 0.5f, 0.2f, 1f));
        rightGeo.setMaterial(rightMat);
        controllerRight.attachChild(rightGeo);
        controllerRight.setLocalTranslation(0.3f, 1.0f, -0.5f);
        rootNode.attachChild(controllerRight);
        
        Log.i(TAG, "Controller visuals created");
    }

    @Override
    public void simpleUpdate(float tpf) {
        time += tpf;
        frameCount++;
        
        // Only do VR-specific updates if VR is enabled
        if (vrEnabled && vrBridge != null) {
            vrBridge.beginFrame();
            updateHeadTracking();
            updateControllers();
        }
        
        // Rotate the cube
        if (cube != null) {
            cube.rotate(0, tpf * 0.5f, 0);
            // Gentle bob
            float bob = FastMath.sin(time * 2f) * 0.05f;
            cube.setLocalTranslation(0, 1f + bob, -2f);
        }
        
        // Log every 300 frames
        if (frameCount % 300 == 0) {
            Log.i(TAG, "Frame " + frameCount + " - Camera at: " + cam.getLocation() + " looking at: " + cam.getDirection());
        }
    }
    
    /**
     * Update camera based on head tracking
     */
    private void updateHeadTracking() {
        if (vrBridge == null) return;
        
        float[] pos = vrBridge.getHeadPosition();
        float[] rot = vrBridge.getHeadRotation();
        
        headPosition.set(pos[0], pos[1], pos[2]);
        headRotation.fromAngles(rot[0], rot[1], rot[2]);
        
        if (vrEnabled && leftEyeCam != null && rightEyeCam != null) {
            // Update stereo cameras
            float eyeOffset = vrRenderer.getIPD() / 2.0f;
            
            // Left eye
            Vector3f leftPos = headPosition.add(headRotation.mult(new Vector3f(-eyeOffset, 0, 0)));
            leftEyeCam.setLocation(leftPos);
            leftEyeCam.setRotation(headRotation);
            
            // Right eye
            Vector3f rightPos = headPosition.add(headRotation.mult(new Vector3f(eyeOffset, 0, 0)));
            rightEyeCam.setLocation(rightPos);
            rightEyeCam.setRotation(headRotation);
        } else {
            // Mono camera
            cam.setLocation(headPosition);
            cam.setRotation(headRotation);
        }
    }
    
    /**
     * Update controller positions
     */
    private void updateControllers() {
        if (vrBridge == null) return;
        
        // Left controller
        float[] leftPos = vrBridge.getControllerPosition(0);
        if (controllerLeft != null) {
            controllerLeft.setLocalTranslation(leftPos[0], leftPos[1], leftPos[2]);
        }
        
        // Right controller
        float[] rightPos = vrBridge.getControllerPosition(1);
        if (controllerRight != null) {
            controllerRight.setLocalTranslation(rightPos[0], rightPos[1], rightPos[2]);
        }
    }
    
    @Override
    public void simpleRender(RenderManager rm) {
        // End VR frame after rendering
        if (vrEnabled && vrBridge != null) {
            vrBridge.endFrame();
        }
    }

    @Override
    public void destroy() {
        Log.i(TAG, "Destroying QuestVRApplication");
        
        if (vrBridge != null) {
            vrBridge.shutdown();
        }
        
        super.destroy();
    }
}
