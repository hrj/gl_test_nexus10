package com.example.gltest;

import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;

public final class ShadyRenderer implements Renderer {
  private static final int NUM_TEXTURES = 2;
  private final int[] mTextureIDs = new int[NUM_TEXTURES];
  private final Paint textPaint = new Paint();

  private final long beginAngle = System.currentTimeMillis();
  private int startAngle = 0;

  private final static int NUM_POINTS = 5;
  private final float offset = 2f / NUM_POINTS;
  private final float offset2 = offset / 2;
  private final float offset4 = offset2 / 2;

  @Override
  public void onDrawFrame(final GL10 arg0) {
    GLES20.glClearColor(0, 0, 0, 0);
    GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

    mShader.beginDrawing();

    startAngle = (int) ((System.currentTimeMillis() - beginAngle) / 3.6);
    for (int i = 0; i < NUM_POINTS; i++) {
      for (int j = 0; j < NUM_POINTS; j++) {
        drawDynamic(i * offset + offset4, j * offset + offset4, offset2, offset2, textPaint, startAngle);
      }
    }
  }

  private final int internalFormat = GLES10.GL_RGBA;
  private static final int DYNAMIC_WIDTH = 64;
  private static final int DYNAMIC_HEIGHT = 64;
  private static final int TEXTURE_STATIC = 0;
  private static final int TEXTURE_DYNAMIC = 1;
  private final int[] pixelsDynamic = new int[DYNAMIC_WIDTH * DYNAMIC_HEIGHT];
  private final IntBuffer bufferDynamic = IntBuffer.wrap(pixelsDynamic);
  private Bitmap mBitmapDynamic;
  private Canvas mCanvasDynamic;

  void drawDynamic(final float x, final float y, final float width, final float height, final Paint textPaint, final float startAngle) {
    final int w = DYNAMIC_WIDTH;
    final int h = DYNAMIC_HEIGHT;

    mBitmapDynamic.eraseColor(0x00000000);
    mCanvasDynamic.drawArc(new RectF(0, 0, w, h), startAngle, 30, true, textPaint);

    mBitmapDynamic.getPixels(pixelsDynamic, 0, w, 0, 0, w, h);
    swapRB(pixelsDynamic, w * h);

    GLES20.glBindTexture(GL10.GL_TEXTURE_2D, mTextureIDs[TEXTURE_DYNAMIC]);
    GLES20.glTexSubImage2D(GL10.GL_TEXTURE_2D, 0, 0, 0, w, h, internalFormat, GL10.GL_UNSIGNED_BYTE, bufferDynamic);
    GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

    mShader.draw(x, y, width, height);

    GLES20.glBindTexture(GL10.GL_TEXTURE_2D, mTextureIDs[TEXTURE_STATIC]);
  }

  private MyShader mShader;

  private static void swapRB(final int[] pixelArray, final int len) {
    // Swap the R & B channels, to be compatible with OpenGL RGBA format 
    // Originally Bitmap.getPixels() returns A, R, G, B format
    for (int i = 0; i < len; i++) {
      final int value = pixelArray[i];
      pixelArray[i] = ((value << 16) & 0xff0000) | ((value >>> 16) & 0xff) | (value & 0xff00ff00);
    }
  }

  @Override
  public void onSurfaceChanged(final GL10 arg0, final int w, final int h) {
  }

  @Override
  public void onSurfaceCreated(final GL10 arg0, final EGLConfig econfig) {
    GLES20.glGenTextures(NUM_TEXTURES, mTextureIDs, 0);
    for (int i = 0; i < NUM_TEXTURES; i++) {
      GLES20.glBindTexture(GL10.GL_TEXTURE_2D, mTextureIDs[i]);

      // Use Nearest for performance.
      GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
      GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

      GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
      GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    }

    GLES20.glEnable(GL10.GL_BLEND);

    GLES20.glBindTexture(GL10.GL_TEXTURE_2D, mTextureIDs[TEXTURE_DYNAMIC]);
    GLES20.glTexImage2D(GL10.GL_TEXTURE_2D, 0, internalFormat, DYNAMIC_WIDTH, DYNAMIC_HEIGHT, 0, internalFormat, GL10.GL_UNSIGNED_BYTE, bufferDynamic);

    final Bitmap.Config config = Bitmap.Config.ARGB_4444;
    mBitmapDynamic = Bitmap.createBitmap(DYNAMIC_WIDTH, DYNAMIC_HEIGHT, config);
    mCanvasDynamic = new Canvas(mBitmapDynamic);
    mShader = new MyShader();
    textPaint.setColor(0xffff3333);
    textPaint.setTextSize(36);
  }

}
