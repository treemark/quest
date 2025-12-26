package com.quest.helloworld;

import android.app.Application;
import android.util.Log;

import com.quest.helloworld.di.AppComponent;
import com.quest.helloworld.di.DaggerAppComponent;

/**
 * Main Application class for the Quest Hello World VR experience.
 * Initializes Dagger dependency injection on startup.
 */
public class QuestHelloWorldApp extends Application {

    private static final String TAG = "QuestHelloWorld";
    
    private static QuestHelloWorldApp instance;
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        
        Log.i(TAG, "Initializing Quest Hello World Application");
        
        // Initialize Dagger component
        appComponent = DaggerAppComponent.builder()
                .build();
        
        // Inject dependencies into this application instance
        appComponent.inject(this);
        
        Log.i(TAG, "Dagger dependency injection initialized successfully");
    }

    /**
     * Returns the singleton instance of the application.
     * @return The QuestHelloWorldApp instance
     */
    public static QuestHelloWorldApp getInstance() {
        return instance;
    }

    /**
     * Returns the application's Dagger component for dependency injection.
     * @return The main AppComponent instance
     */
    public AppComponent getAppComponent() {
        return appComponent;
    }
}
