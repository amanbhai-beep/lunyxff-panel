#include <jni.h>
#include <android/native_window.h>
#include <android/native_window_jni.h>
#include <GLES3/gl3.h>
#include <EGL/egl.h>
#include <android/log.h>
#include <unistd.h>

#define LOG_TAG "LunyxFF"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

class Engine {
    EGLDisplay display = EGL_NO_DISPLAY;
    EGLSurface surface = EGL_NO_SURFACE;
    EGLContext context = EGL_NO_CONTEXT;
    bool running = false;
public:
    bool Init(ANativeWindow* win, int w, int h) {
        display = eglGetDisplay(EGL_DEFAULT_DISPLAY);
        eglInitialize(display, 0, 0);
        const EGLint attr[] = { EGL_SURFACE_TYPE, EGL_WINDOW_BIT, EGL_RENDERABLE_TYPE, EGL_OPENGL_ES3_BIT, EGL_BLUE_SIZE, 8, EGL_GREEN_SIZE, 8, EGL_RED_SIZE, 8, EGL_ALPHA_SIZE, 8, EGL_NONE };
        EGLConfig config; int num;
        eglChooseConfig(display, attr, &config, 1, &num);
        const EGLint ctxAttr[] = { EGL_CONTEXT_CLIENT_VERSION, 3, EGL_NONE };
        context = eglCreateContext(display, config, EGL_NO_CONTEXT, ctxAttr);
        surface = eglCreateWindowSurface(display, config, win, 0);
        eglMakeCurrent(display, surface, surface, context);
        running = true;
        return true;
    }
    void Loop() {
        while(running) {
            glClearColor(0, 0, 0, 0);
            glClear(GL_COLOR_BUFFER_BIT);
            eglSwapBuffers(display, surface);
            usleep(16000);
        }
    }
    void Stop() { running = false; }
} engine;

extern "C" JNIEXPORT jboolean JNICALL
Java_com_lunyx_overlay_OverlayService_nativeInit(JNIEnv* env, jobject, jobject surf, jint w, jint h) {
    ANativeWindow* win = ANativeWindow_fromSurface(env, surf);
    return win ? engine.Init(win, w, h) : JNI_FALSE;
}
extern "C" JNIEXPORT void JNICALL Java_com_lunyx_overlay_OverlayService_nativeStartLoop(JNIEnv*, jobject) { engine.Loop(); }
extern "C" JNIEXPORT void JNICALL Java_com_lunyx_overlay_OverlayService_nativeShutdown(JNIEnv*, jobject) { engine.Stop(); }
