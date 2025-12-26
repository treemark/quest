package com.quest.helloworld.di;

import com.quest.helloworld.vr.VRSceneConfig;
import com.quest.helloworld.vr.HelloWorldScene;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module providing application-level dependencies.
 * Configures VR scene settings and shared resources.
 */
@Module
public class AppModule {

    @Provides
    @Singleton
    public VRSceneConfig provideVRSceneConfig() {
        return new VRSceneConfig.Builder()
                .setSceneName("Quest Hello World")
                .setBackgroundColor(0.05f, 0.05f, 0.15f, 1.0f)  // Deep space blue
                .setTextScale(1.5f)
                .setTextDistance(3.0f)
                .build();
    }

    @Provides
    public HelloWorldScene provideHelloWorldScene(VRSceneConfig config) {
        return new HelloWorldScene(config);
    }
}

