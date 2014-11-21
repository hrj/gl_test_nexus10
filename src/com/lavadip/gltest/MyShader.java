package com.lavadip.gltest;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.util.Log;

final class MyShader extends Shader {

  final int mPosLoc, mSamplerLoc, mTexCoordLoc;

  private static final String vShaderStr =
      "attribute vec2 a_position; "
          + "attribute vec2 a_texcoords; "
          + "varying vec2 v_texcoords; "
          + "void main() {"
          + "  gl_Position.xy = a_position;"
          + "  gl_Position.z = 0.0;"
          + "  gl_Position.w = 1.0;"
          + "  v_texcoords = a_texcoords;"
          + "}";

  private static final String fShaderStr =
      "precision mediump float; "
          + "uniform sampler2D s_texture; "
          + "varying vec2 v_texcoords; "
          + "void main() {"
          + "  gl_FragColor = texture2D( s_texture, v_texcoords);"
          + "}";

  MyShader() {
    super(vShaderStr, fShaderStr);

    if (mProgramId == 0) {
      Log.e("TEST", "Error Loading TextShader");
      mTexCoordLoc = mPosLoc = mSamplerLoc = -1;
    } else {

      // Get the attribute locations
      mPosLoc = GLES20.glGetAttribLocation(mProgramId, "a_position");
      mTexCoordLoc = GLES20.glGetAttribLocation(mProgramId, "a_texcoords");
      mSamplerLoc = GLES20.glGetUniformLocation(mProgramId, "s_texture");
    }
  }

  void beginDrawing() {
    activate();
  }

  private final static int
      POSITION_SIZE_FLOATS = 2,
      POSITION_SIZE_BYTES = POSITION_SIZE_FLOATS * 4,
      TEX_COORD_BYTES = 8;

  private final FloatBuffer vertexPosBuffer =
      ByteBuffer.allocateDirect((POSITION_SIZE_BYTES * 4))
          .order(ByteOrder.nativeOrder()).asFloatBuffer();
  private final FloatBuffer texCoordBuffer =
      ByteBuffer.allocateDirect((4 * TEX_COORD_BYTES))
          .order(ByteOrder.nativeOrder()).asFloatBuffer();

  void draw(final float x, final float y, final float w, final float h) {
    final float scrLX = x - 1f;
    final float scrBY = y - 1f;
    final float scrRX = x + w - 1f;
    final float scrTY = y + h - 1f;

    vertexPosBuffer.position(0);
    vertexPosBuffer.put(scrLX);
    vertexPosBuffer.put(scrBY);
    vertexPosBuffer.put(scrRX);
    vertexPosBuffer.put(scrBY);
    vertexPosBuffer.put(scrLX);
    vertexPosBuffer.put(scrTY);
    vertexPosBuffer.put(scrRX);
    vertexPosBuffer.put(scrTY);
    vertexPosBuffer.position(0);

    GLES20.glVertexAttribPointer(mPosLoc, 2, GLES20.GL_FLOAT, false, POSITION_SIZE_BYTES, vertexPosBuffer);
    GLES20.glEnableVertexAttribArray(mPosLoc);

    // Set the sampler texture unit to 0
    GLES20.glUniform1i(mSamplerLoc, 0);

    final float texLX = 0;
    final float texRX = 1;
    final float texBY = 1;
    final float texTY = 0;

    texCoordBuffer.position(0);
    texCoordBuffer.put(texLX);
    texCoordBuffer.put(texBY);
    texCoordBuffer.put(texRX);
    texCoordBuffer.put(texBY);
    texCoordBuffer.put(texLX);
    texCoordBuffer.put(texTY);
    texCoordBuffer.put(texRX);
    texCoordBuffer.put(texTY);
    texCoordBuffer.position(0);

    GLES20.glVertexAttribPointer(mTexCoordLoc, 2, GLES20.GL_FLOAT, false, TEX_COORD_BYTES, texCoordBuffer);
    GLES20.glEnableVertexAttribArray(mTexCoordLoc);

    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexPosBuffer.capacity() / POSITION_SIZE_FLOATS);
  }

}