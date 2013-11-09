package com.example.gltest;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

import android.opengl.GLSurfaceView;
import android.util.Log;

public class MultisampleConfigChooser implements GLSurfaceView.EGLConfigChooser {
  static private final String kTag = "TEST";
  public EGLConfig chooseConfig(final EGL10 egl, final EGLDisplay display) {
      mValue = new int[1];

      // Try to find a normal multisample configuration first.
      int[] configSpec = {
        EGL10.EGL_RED_SIZE, 5,
        EGL10.EGL_GREEN_SIZE, 6,
        EGL10.EGL_BLUE_SIZE, 5,
        EGL10.EGL_DEPTH_SIZE, 16,
        // Requires that setEGLContextClientVersion(2) is called on the view.
        EGL10.EGL_RENDERABLE_TYPE, 4 /* EGL_OPENGL_ES2_BIT */,
        EGL10.EGL_SAMPLE_BUFFERS, 1 /* true */,
        EGL10.EGL_SAMPLES, 2,
        EGL10.EGL_NONE
      };

      if (!egl.eglChooseConfig(display, configSpec, null, 0, mValue)) {
        throw new IllegalArgumentException("eglChooseConfig failed");
      }
      int numConfigs = mValue[0];

      if (numConfigs <= 0) {
        // No normal multisampling config was found. Try to create a
        // converage multisampling configuration, for the nVidia Tegra2.
        // See the EGL_NV_coverage_sample documentation.

        final int EGL_COVERAGE_BUFFERS_NV = 0x30E0;
        final int EGL_COVERAGE_SAMPLES_NV = 0x30E1;

        configSpec = new int[]{
          EGL10.EGL_RED_SIZE, 5,
          EGL10.EGL_GREEN_SIZE, 6,
          EGL10.EGL_BLUE_SIZE, 5,
          EGL10.EGL_DEPTH_SIZE, 16,
          EGL10.EGL_RENDERABLE_TYPE, 4 /* EGL_OPENGL_ES2_BIT */,
          EGL_COVERAGE_BUFFERS_NV, 1 /* true */,
          EGL_COVERAGE_SAMPLES_NV, 2,  // always 5 in practice on tegra 2
          EGL10.EGL_NONE
        };

        if (egl.eglChooseConfig(display, configSpec, null, 0, mValue)) {
          // throw new IllegalArgumentException("2nd eglChooseConfig failed");
          numConfigs = mValue[0];
        } else {
          numConfigs = 0;
        }

        if (numConfigs <= 0) {
          // Give up, try without multisampling.
          configSpec = new int[]{
            EGL10.EGL_RED_SIZE, 5,
            EGL10.EGL_GREEN_SIZE, 6,
            EGL10.EGL_BLUE_SIZE, 5,
            EGL10.EGL_DEPTH_SIZE, 16,
            EGL10.EGL_RENDERABLE_TYPE, 4 /* EGL_OPENGL_ES2_BIT */,
            EGL10.EGL_NONE
          };

          if (!egl.eglChooseConfig(display, configSpec, null, 0, mValue)) {
            throw new IllegalArgumentException("3rd eglChooseConfig failed");
          }
          numConfigs = mValue[0];

          if (numConfigs <= 0) {
            throw new IllegalArgumentException("No configs match configSpec");
          }
        } else {
          Log.d(kTag, "Coverage sampling (Tegra device?)");
        }
      }

      // Get all matching configurations.
      final EGLConfig[] configs = new EGLConfig[numConfigs];
      if (!egl.eglChooseConfig(display, configSpec, configs, numConfigs, mValue)) {
          throw new IllegalArgumentException("data eglChooseConfig failed");
      }

      // CAUTION! eglChooseConfigs returns configs with higher bit depth
      // first: Even though we asked for rgb565 configurations, rgb888
      // configurations are considered to be "better" and returned first.
      // You need to explicitly filter the data returned by eglChooseConfig!
      int index = -1;
      for (int i = 0; i < configs.length; ++i) {
        if (findConfigAttrib(egl, display, configs[i], EGL10.EGL_RED_SIZE, 0) == 5) {
          index = i;
          break;
        }
      }
      if (index == -1) {
        Log.w(kTag, "Did not find sane config, using first");
      }
      final EGLConfig config = configs.length > 0 ? configs[index] : null;
      if (config == null) {
        throw new IllegalArgumentException("No config chosen");
      }
      return config;
  }

  private int findConfigAttrib(final EGL10 egl, final EGLDisplay display,
          final EGLConfig config, final int attribute, final int defaultValue) {
    if (egl.eglGetConfigAttrib(display, config, attribute, mValue)) {
      return mValue[0];
    }
    return defaultValue;
  }

  private int[] mValue;
}
