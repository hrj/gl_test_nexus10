package com.example.gltest;

import android.opengl.GLES20;

abstract class Shader {
  final protected int mProgramId;

  Shader(final String vertShaderSrc, final String fragShaderSrc) {
    mProgramId = loadProgram(vertShaderSrc, fragShaderSrc);
  }

  protected void activate() {
    GLES20.glUseProgram(mProgramId);
  }

  private static int loadProgram(final String vertShaderSrc, final String fragShaderSrc) {
    // Load the vertex/fragment shaders
    final int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertShaderSrc);
    if (vertexShader == 0) {
      android.util.Log.e("TEST", "Error Loading Vertex shader");
      return 0;
    }

    final int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragShaderSrc);
    if (fragmentShader == 0) {
      android.util.Log.e("TEST", "Error Loading Frag shader");
      GLES20.glDeleteShader(vertexShader);
      return 0;
    }

    // Create the program object
    final int programObject = GLES20.glCreateProgram();

    if (programObject == 0)
      return 0;

    GLES20.glAttachShader(programObject, vertexShader);
    GLES20.glAttachShader(programObject, fragmentShader);

    // Link the program
    GLES20.glLinkProgram(programObject);

    // Check the link status
    final int[] linked = new int[1];
    GLES20.glGetProgramiv(programObject, GLES20.GL_LINK_STATUS, linked, 0);

    if (linked[0] == 0) {
      android.util.Log.e("TEST", "Error linking program:");
      android.util.Log.e("TEST", GLES20.glGetProgramInfoLog(programObject));
      GLES20.glDeleteProgram(programObject);
      return 0;
    }

    // Free up no longer needed shader resources
    GLES20.glDeleteShader(vertexShader);
    GLES20.glDeleteShader(fragmentShader);

    return programObject;
  }

  private static int loadShader(final int type, final String shaderSrc) {
    // Create the shader object
    final int shader = GLES20.glCreateShader(type);

    if (shader == 0)
      return 0;

    // Load the shader source
    GLES20.glShaderSource(shader, shaderSrc);

    // Compile the shader
    GLES20.glCompileShader(shader);

    // Check the compile status
    final int[] compiled = new int[1];
    GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);

    if (compiled[0] == 0) {
      android.util.Log.e("TEST", "Error compiling shader: " + GLES20.glGetShaderInfoLog(shader));
      GLES20.glDeleteShader(shader);
      return 0;
    }
    return shader;
  }

}
