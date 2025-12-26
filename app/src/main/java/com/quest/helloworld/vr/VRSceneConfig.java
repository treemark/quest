package com.quest.helloworld.vr;

import com.jme3.math.ColorRGBA;

/**
 * Configuration class for VR scene settings.
 * Uses the builder pattern for flexible configuration.
 */
public class VRSceneConfig {

    private final String sceneName;
    private final ColorRGBA backgroundColor;
    private final float textScale;
    private final float textDistance;

    private VRSceneConfig(Builder builder) {
        this.sceneName = builder.sceneName;
        this.backgroundColor = builder.backgroundColor;
        this.textScale = builder.textScale;
        this.textDistance = builder.textDistance;
    }

    public String getSceneName() {
        return sceneName;
    }

    public ColorRGBA getBackgroundColor() {
        return backgroundColor;
    }

    public float getTextScale() {
        return textScale;
    }

    public float getTextDistance() {
        return textDistance;
    }

    /**
     * Builder for creating VRSceneConfig instances.
     */
    public static class Builder {
        private String sceneName = "VR Scene";
        private ColorRGBA backgroundColor = new ColorRGBA(0.1f, 0.1f, 0.2f, 1.0f);
        private float textScale = 1.0f;
        private float textDistance = 2.0f;

        public Builder setSceneName(String sceneName) {
            this.sceneName = sceneName;
            return this;
        }

        public Builder setBackgroundColor(float r, float g, float b, float a) {
            this.backgroundColor = new ColorRGBA(r, g, b, a);
            return this;
        }

        public Builder setTextScale(float textScale) {
            this.textScale = textScale;
            return this;
        }

        public Builder setTextDistance(float textDistance) {
            this.textDistance = textDistance;
            return this;
        }

        public VRSceneConfig build() {
            return new VRSceneConfig(this);
        }
    }
}

