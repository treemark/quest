/**
 * VR Renderer - Handles stereo rendering for Quest
 * 
 * This module would handle:
 * - Creating framebuffers for each eye
 * - Managing OpenXR swapchains
 * - Stereo projection matrices
 */

#include <jni.h>
#include <android/log.h>
#include <GLES3/gl3.h>
#include <cmath>

#define LOG_TAG "VRRenderer"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

// Quest 3 recommended render resolution per eye
static const int EYE_WIDTH = 1680;
static const int EYE_HEIGHT = 1760;

// IPD (Inter-Pupillary Distance) in meters
static const float IPD = 0.063f;

// Field of view
static const float FOV_LEFT = 45.0f;   // degrees
static const float FOV_RIGHT = 45.0f;
static const float FOV_UP = 50.0f;
static const float FOV_DOWN = 55.0f;

extern "C" {

/**
 * Get recommended eye texture width
 */
JNIEXPORT jint JNICALL
Java_com_quest_helloworld_vr_VRRenderer_nativeGetEyeWidth(
        JNIEnv *env,
        jobject thiz) {
    return EYE_WIDTH;
}

/**
 * Get recommended eye texture height
 */
JNIEXPORT jint JNICALL
Java_com_quest_helloworld_vr_VRRenderer_nativeGetEyeHeight(
        JNIEnv *env,
        jobject thiz) {
    return EYE_HEIGHT;
}

/**
 * Get eye offset for stereo rendering (x offset from center)
 */
JNIEXPORT jfloat JNICALL
Java_com_quest_helloworld_vr_VRRenderer_nativeGetEyeOffset(
        JNIEnv *env,
        jobject thiz,
        jint eye) {
    // eye 0 = left, eye 1 = right
    return eye == 0 ? -IPD / 2.0f : IPD / 2.0f;
}

/**
 * Get projection matrix for an eye
 * Returns a 16-element float array (4x4 matrix, column-major)
 */
JNIEXPORT jfloatArray JNICALL
Java_com_quest_helloworld_vr_VRRenderer_nativeGetProjectionMatrix(
        JNIEnv *env,
        jobject thiz,
        jint eye,
        jfloat nearClip,
        jfloat farClip) {
    
    // Asymmetric frustum for VR
    float left, right, top, bottom;
    
    // Convert FOV to radians
    const float DEG_TO_RAD = 3.14159265f / 180.0f;
    
    if (eye == 0) {  // Left eye
        left = -tanf(FOV_LEFT * DEG_TO_RAD) * nearClip;
        right = tanf(FOV_RIGHT * DEG_TO_RAD) * nearClip * 0.9f;  // Slightly less to right
    } else {  // Right eye
        left = -tanf(FOV_LEFT * DEG_TO_RAD) * nearClip * 0.9f;  // Slightly less to left
        right = tanf(FOV_RIGHT * DEG_TO_RAD) * nearClip;
    }
    
    top = tanf(FOV_UP * DEG_TO_RAD) * nearClip;
    bottom = -tanf(FOV_DOWN * DEG_TO_RAD) * nearClip;
    
    // Build projection matrix (column-major for OpenGL)
    float matrix[16] = {0};
    
    matrix[0] = (2.0f * nearClip) / (right - left);
    matrix[5] = (2.0f * nearClip) / (top - bottom);
    matrix[8] = (right + left) / (right - left);
    matrix[9] = (top + bottom) / (top - bottom);
    matrix[10] = -(farClip + nearClip) / (farClip - nearClip);
    matrix[11] = -1.0f;
    matrix[14] = -(2.0f * farClip * nearClip) / (farClip - nearClip);
    
    jfloatArray result = env->NewFloatArray(16);
    env->SetFloatArrayRegion(result, 0, 16, matrix);
    return result;
}

/**
 * Get IPD (Inter-Pupillary Distance)
 */
JNIEXPORT jfloat JNICALL
Java_com_quest_helloworld_vr_VRRenderer_nativeGetIPD(
        JNIEnv *env,
        jobject thiz) {
    return IPD;
}

} // extern "C"

