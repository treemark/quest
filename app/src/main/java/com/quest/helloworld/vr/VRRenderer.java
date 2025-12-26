package com.quest.helloworld.vr;

import android.util.Log;

/**
 * VR Renderer configuration and stereo projection utilities.
 */
public class VRRenderer {
    
    private static final String TAG = "VRRenderer";
    
    public static final int LEFT_EYE = 0;
    public static final int RIGHT_EYE = 1;
    
    /**
     * Get recommended eye texture width.
     */
    public int getEyeWidth() {
        try {
            return nativeGetEyeWidth();
        } catch (UnsatisfiedLinkError e) {
            Log.w(TAG, "Native not available, using default");
            return 1680;  // Quest 3 default
        }
    }
    
    /**
     * Get recommended eye texture height.
     */
    public int getEyeHeight() {
        try {
            return nativeGetEyeHeight();
        } catch (UnsatisfiedLinkError e) {
            return 1760;  // Quest 3 default
        }
    }
    
    /**
     * Get eye offset (x translation) for stereo rendering.
     * @param eye LEFT_EYE or RIGHT_EYE
     * @return offset in meters
     */
    public float getEyeOffset(int eye) {
        try {
            return nativeGetEyeOffset(eye);
        } catch (UnsatisfiedLinkError e) {
            float ipd = 0.063f;  // Average IPD
            return eye == LEFT_EYE ? -ipd / 2.0f : ipd / 2.0f;
        }
    }
    
    /**
     * Get projection matrix for an eye.
     * @param eye LEFT_EYE or RIGHT_EYE
     * @param nearClip near clipping plane distance
     * @param farClip far clipping plane distance
     * @return 16-element float array (4x4 column-major matrix)
     */
    public float[] getProjectionMatrix(int eye, float nearClip, float farClip) {
        try {
            return nativeGetProjectionMatrix(eye, nearClip, farClip);
        } catch (UnsatisfiedLinkError e) {
            // Return a simple perspective matrix as fallback
            return createPerspectiveMatrix(90.0f, 1.0f, nearClip, farClip);
        }
    }
    
    /**
     * Get IPD (Inter-Pupillary Distance).
     * @return IPD in meters
     */
    public float getIPD() {
        try {
            return nativeGetIPD();
        } catch (UnsatisfiedLinkError e) {
            return 0.063f;  // Average human IPD
        }
    }
    
    /**
     * Create a simple perspective projection matrix.
     */
    private float[] createPerspectiveMatrix(float fovY, float aspect, float near, float far) {
        float[] matrix = new float[16];
        float f = 1.0f / (float) Math.tan(Math.toRadians(fovY) / 2.0f);
        
        matrix[0] = f / aspect;
        matrix[5] = f;
        matrix[10] = (far + near) / (near - far);
        matrix[11] = -1.0f;
        matrix[14] = (2.0f * far * near) / (near - far);
        
        return matrix;
    }
    
    // Native method declarations
    private native int nativeGetEyeWidth();
    private native int nativeGetEyeHeight();
    private native float nativeGetEyeOffset(int eye);
    private native float[] nativeGetProjectionMatrix(int eye, float nearClip, float farClip);
    private native float nativeGetIPD();
}

