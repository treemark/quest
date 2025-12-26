package com.quest.helloworld.di;

import com.quest.helloworld.QuestHelloWorldApp;
import com.quest.helloworld.QuestVRApplication;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Main Dagger component for the Quest Hello World application.
 * Provides dependency injection across the application.
 */
@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    /**
     * Injects dependencies into the main application class.
     */
    void inject(QuestHelloWorldApp app);

    /**
     * Injects dependencies into the jMonkeyEngine VR application.
     */
    void inject(QuestVRApplication vrApp);

    /**
     * Builder pattern for creating the component.
     */
    @Component.Builder
    interface Builder {
        AppComponent build();
    }
}
