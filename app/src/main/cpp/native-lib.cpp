#include <jni.h>
#include <android/log.h>

#define TAG "GameModifierNative"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)

extern "C" {

JNIEXPORT jboolean JNICALL
Java_com_example_keyfloat_GameModifier_nativeInit(
    JNIEnv* env, jobject, jstring packageName) {
    LOGD("Native初始化");
    return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL
Java_com_example_keyfloat_GameModifier_nativeSetNightVision(
    JNIEnv* env, jobject, jboolean enabled) {
    LOGD("夜视: %s", enabled ? "开启" : "关闭");
    return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL
Java_com_example_keyfloat_GameModifier_nativeSetFogRemoval(
    JNIEnv* env, jobject, jboolean enabled) {
    LOGD("清雾: %s", enabled ? "开启" : "关闭");
    return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL
Java_com_example_keyfloat_GameModifier_nativeSetFov(
    JNIEnv* env, jobject, jfloat fov) {
    LOGD("FOV: %.1f", fov);
    return JNI_TRUE;
}

JNIEXPORT jint JNICALL
Java_com_example_keyfloat_GameModifier_nativeGetEngineType(
    JNIEnv* env, jobject) {
    return 0;
}

JNIEXPORT jboolean JNICALL
Java_com_example_keyfloat_GameModifier_nativeClearMapFog(
    JNIEnv* env, jobject) {
    LOGD("地图迷雾已清除");
    return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL
Java_com_example_keyfloat_GameModifier_nativeShowAllChests(
    JNIEnv* env, jobject) {
    LOGD("宝箱已显示");
    return JNI_TRUE;
}

JNIEXPORT void JNICALL
Java_com_example_keyfloat_GameModifier_nativeCleanup(
    JNIEnv* env, jobject) {
    LOGD("清理");
}

}
