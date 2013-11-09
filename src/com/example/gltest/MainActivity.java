package com.example.gltest;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class MainActivity extends Activity {

  private GLSurfaceView gl3d;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    gl3d = (GLSurfaceView) findViewById(R.id.gl3d);
    gl3d.setEGLContextClientVersion(2);
    gl3d.setEGLConfigChooser(new MultisampleConfigChooser());
    gl3d.setRenderer(new MyRenderer());

  }

}
