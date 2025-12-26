package com.quest.helloworld;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.jme3.app.AndroidHarness;

/**
 * Main Activity that hosts the jMonkeyEngine VR application.
 * Uses AndroidHarness to integrate jME3 with Android lifecycle.
 */
public class MainActivity extends AndroidHarness {

    private static final String TAG = "QuestMainActivity";

    public MainActivity() {
        // Configure jME3 application class - instantiated via reflection
        appClass = QuestVRApplication.class.getName();
        
        // Exit dialog configuration
        exitDialogTitle = "Exit VR?";
        exitDialogMessage = "Are you sure you want to exit?";
        
        // OpenGL ES settings for Quest 3 - simple config
        eglBitsPerPixel = 24;
        eglAlphaBits = 0;
        eglDepthBits = 16;  // Lower depth bits for compatibility
        eglStencilBits = 0;
        eglSamples = 0;  // Disable MSAA completely
        
        // Frame rate
        frameRate = 72;
        
        // Input settings
        mouseEventsEnabled = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        // Call parent which creates the jME view
        super.onCreate(savedInstanceState);
        
        Log.i(TAG, "MainActivity created");
        
        // Log the view hierarchy for debugging
        if (view != null) {
            Log.i(TAG, "jME view created: " + view.getClass().getSimpleName());
            Log.i(TAG, "View dimensions: " + view.getWidth() + "x" + view.getHeight());
            
            // Ensure the view is visible and properly sized
            view.setVisibility(android.view.View.VISIBLE);
            
            // Request layout to ensure proper sizing
            view.requestLayout();
            view.invalidate();
            
            // If it's a GLSurfaceView, configure it properly
            if (view instanceof GLSurfaceView) {
                GLSurfaceView glView = (GLSurfaceView) view;
                glView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
                glView.setPreserveEGLContextOnPause(true);  // Keep GL context on pause
                Log.i(TAG, "GLSurfaceView configured: CONTINUOUS render, preserve context");
            }
        } else {
            Log.e(TAG, "WARNING: jME view is null!");
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && view != null) {
            Log.i(TAG, "Window focused - View size: " + view.getWidth() + "x" + view.getHeight());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "MainActivity resumed");
        
        if (view != null) {
            view.setVisibility(android.view.View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "MainActivity paused");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "MainActivity destroyed");
        super.onDestroy();
    }
}
