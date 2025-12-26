package com.quest.helloworld.vr;

import android.app.Activity;
import android.util.Log;

/**
 * JNI bridge to native OpenXR functionality.
 * Provides head tracking, controller input, and VR session management.
 */
public class OpenXRBridge {
    
    private static final String TAG = "OpenXRBridge";
    
    // Load native library
    static {
        try {
            System.loadLibrary("questvr");
            Log.i(TAG, "Native library 'questvr' loaded successfully");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Failed to load native library: " + e.getMessage());
        }
    }
    
    private boolean initialized = false;
    
    /**
     * Initialize the VR system.
     * @param activity The Android activity
     * @return true if initialization succeeded
     */
    public boolean initialize(Activity activity) {
        if (initialized) {
            Log.w(TAG, "Already initialized");
            return true;
        }
        
        try {
            initialized = nativeInitialize(activity);
            if (initialized) {
                Log.i(TAG, "VR system initialized");
            } else {
                Log.e(TAG, "VR initialization failed");
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception during VR init: " + e.getMessage());
            initialized = false;
        }
        
        return initialized;
    }
    
    /**
     * Shutdown the VR system.
     */
    public void shutdown() {
        if (initialized) {
            nativeShutdown();
            initialized = false;
            Log.i(TAG, "VR system shut down");
        }
    }
    
    /**
     * Begin a new VR frame. Call at start of render loop.
     * @return true if frame should be rendered
     */
    public boolean beginFrame() {
        return initialized && nativeBeginFrame();
    }
    
    /**
     * End the VR frame. Call at end of render loop.
     */
    public void endFrame() {
        if (initialized) {
            nativeEndFrame();
        }
    }
    
    /**
     * Get the current head position.
     * @return float array [x, y, z] in meters
     */
    public float[] getHeadPosition() {
        if (!initialized) return new float[]{0, 1.6f, 0};
        return nativeGetHeadPosition();
    }
    
    /**
     * Get the current head rotation.
     * @return float array [pitch, yaw, roll] in radians
     */
    public float[] getHeadRotation() {
        if (!initialized) return new float[]{0, 0, 0};
        return nativeGetHeadRotation();
    }
    
    /**
     * Get controller position.
     * @param hand 0 for left, 1 for right
     * @return float array [x, y, z] in meters
     */
    public float[] getControllerPosition(int hand) {
        if (!initialized) {
            return hand == 0 ? new float[]{-0.3f, 1.0f, -0.5f} : new float[]{0.3f, 1.0f, -0.5f};
        }
        return nativeGetControllerPosition(hand);
    }
    
    /**
     * Check if trigger is pressed.
     * @param hand 0 for left, 1 for right
     * @return true if pressed
     */
    public boolean isTriggerPressed(int hand) {
        return initialized && nativeIsTriggerPressed(hand);
    }
    
    /**
     * Get thumbstick values.
     * @param hand 0 for left, 1 for right
     * @return float array [x, y] ranging from -1 to 1
     */
    public float[] getThumbstick(int hand) {
        if (!initialized) return new float[]{0, 0};
        return nativeGetThumbstick(hand);
    }
    
    /**
     * Update head pose (for testing/simulation).
     */
    public void updateHeadPose(float px, float py, float pz, float rx, float ry, float rz) {
        if (initialized) {
            nativeUpdateHeadPose(px, py, pz, rx, ry, rz);
        }
    }
    
    /**
     * Check if VR is initialized.
     */
    public boolean isInitialized() {
        return initialized && nativeIsInitialized();
    }
    
    // Native method declarations
    private native boolean nativeInitialize(Activity activity);
    private native void nativeShutdown();
    private native boolean nativeBeginFrame();
    private native void nativeEndFrame();
    private native float[] nativeGetHeadPosition();
    private native float[] nativeGetHeadRotation();
    private native float[] nativeGetControllerPosition(int hand);
    private native boolean nativeIsTriggerPressed(int hand);
    private native float[] nativeGetThumbstick(int hand);
    private native void nativeUpdateHeadPose(float px, float py, float pz, float rx, float ry, float rz);
    private native boolean nativeIsInitialized();
}

