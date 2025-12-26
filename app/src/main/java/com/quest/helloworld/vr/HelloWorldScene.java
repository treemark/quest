package com.quest.helloworld.vr;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;

import javax.inject.Inject;

/**
 * The main VR scene for the Hello World experience.
 * Creates a 3D environment with floating "Hello Quest 3!" text
 * and decorative elements suitable for VR viewing.
 */
public class HelloWorldScene extends AbstractAppState {

    private final VRSceneConfig config;
    
    private Node sceneNode;
    private AssetManager assetManager;
    private BitmapText helloText;
    private Geometry floatingCube;
    private float time = 0f;

    @Inject
    public HelloWorldScene(VRSceneConfig config) {
        this.config = config;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        this.assetManager = app.getAssetManager();
        this.sceneNode = new Node("HelloWorldScene");
        
        // Setup the scene
        setupLighting();
        setupHelloText();
        setupFloatingObjects();
        setupEnvironment();
        
        // Attach scene to root node
        ((com.jme3.app.SimpleApplication) app).getRootNode().attachChild(sceneNode);
    }

    /**
     * Sets up ambient and directional lighting for the VR scene.
     */
    private void setupLighting() {
        // Ambient light for overall illumination
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(0.4f));
        sceneNode.addLight(ambient);

        // Main directional light (simulates sun)
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.5f, -1f, -0.5f).normalizeLocal());
        sun.setColor(ColorRGBA.White.mult(0.8f));
        sceneNode.addLight(sun);

        // Fill light from opposite direction
        DirectionalLight fill = new DirectionalLight();
        fill.setDirection(new Vector3f(0.5f, 0.5f, 0.5f).normalizeLocal());
        fill.setColor(new ColorRGBA(0.4f, 0.4f, 0.6f, 1f));
        sceneNode.addLight(fill);
    }

    /**
     * Creates the main "Hello Quest 3!" 3D text.
     */
    private void setupHelloText() {
        BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");
        
        helloText = new BitmapText(font, false);
        helloText.setSize(config.getTextScale());
        helloText.setColor(new ColorRGBA(0.2f, 0.8f, 1.0f, 1.0f));  // Cyan glow
        helloText.setText("Hello Quest 3!");
        
        // Center the text and position it in front of the user
        float textWidth = helloText.getLineWidth();
        float textHeight = helloText.getLineHeight();
        helloText.setLocalTranslation(
            -textWidth / 2f,
            textHeight / 2f,
            -config.getTextDistance()
        );
        
        sceneNode.attachChild(helloText);
    }

    /**
     * Creates floating decorative 3D objects around the scene.
     */
    private void setupFloatingObjects() {
        // Main floating cube
        Box box = new Box(0.3f, 0.3f, 0.3f);
        floatingCube = new Geometry("FloatingCube", box);
        
        Material cubeMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        cubeMat.setBoolean("UseMaterialColors", true);
        cubeMat.setColor("Diffuse", new ColorRGBA(1.0f, 0.4f, 0.2f, 1.0f));  // Orange
        cubeMat.setColor("Specular", ColorRGBA.White);
        cubeMat.setFloat("Shininess", 64f);
        cubeMat.setColor("Ambient", new ColorRGBA(0.2f, 0.1f, 0.05f, 1.0f));
        floatingCube.setMaterial(cubeMat);
        
        floatingCube.setLocalTranslation(2f, 0.5f, -3f);
        sceneNode.attachChild(floatingCube);

        // Create orbital spheres
        createOrbitalSphere(-2f, 0f, -4f, 0.15f, new ColorRGBA(0.2f, 1.0f, 0.4f, 1.0f));  // Green
        createOrbitalSphere(1.5f, 1.5f, -5f, 0.2f, new ColorRGBA(1.0f, 0.2f, 0.6f, 1.0f));  // Pink
        createOrbitalSphere(-1f, -0.5f, -3.5f, 0.12f, new ColorRGBA(1.0f, 1.0f, 0.2f, 1.0f));  // Yellow
    }

    /**
     * Creates a decorative sphere at the specified position.
     */
    private void createOrbitalSphere(float x, float y, float z, float radius, ColorRGBA color) {
        Sphere sphere = new Sphere(32, 32, radius);
        Geometry sphereGeo = new Geometry("Sphere", sphere);
        
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Diffuse", color);
        mat.setColor("Specular", ColorRGBA.White.mult(0.5f));
        mat.setFloat("Shininess", 32f);
        mat.setColor("Ambient", color.mult(0.3f));
        sphereGeo.setMaterial(mat);
        
        sphereGeo.setLocalTranslation(x, y, z);
        sceneNode.attachChild(sphereGeo);
    }

    /**
     * Sets up the ground plane and environment elements.
     */
    private void setupEnvironment() {
        // Create a ground reference plane
        Box groundBox = new Box(10f, 0.05f, 10f);
        Geometry ground = new Geometry("Ground", groundBox);
        
        Material groundMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        groundMat.setBoolean("UseMaterialColors", true);
        groundMat.setColor("Diffuse", new ColorRGBA(0.15f, 0.15f, 0.2f, 1.0f));
        groundMat.setColor("Ambient", new ColorRGBA(0.05f, 0.05f, 0.08f, 1.0f));
        ground.setMaterial(groundMat);
        
        ground.setLocalTranslation(0, -1.5f, -5f);
        sceneNode.attachChild(ground);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        time += tpf;

        // Animate the floating cube - gentle rotation and bob
        if (floatingCube != null) {
            // Rotation
            Quaternion rotation = new Quaternion();
            rotation.fromAngles(time * 0.5f, time * 0.7f, time * 0.3f);
            floatingCube.setLocalRotation(rotation);
            
            // Gentle vertical bobbing
            float bobOffset = FastMath.sin(time * 1.5f) * 0.2f;
            floatingCube.setLocalTranslation(2f, 0.5f + bobOffset, -3f);
        }

        // Subtle text color pulsing
        if (helloText != null) {
            float pulse = (FastMath.sin(time * 2f) + 1f) / 2f;
            float r = 0.2f + pulse * 0.3f;
            float g = 0.7f + pulse * 0.3f;
            float b = 1.0f;
            helloText.setColor(new ColorRGBA(r, g, b, 1.0f));
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();
        if (sceneNode != null) {
            sceneNode.removeFromParent();
        }
    }

    public VRSceneConfig getConfig() {
        return config;
    }
}

