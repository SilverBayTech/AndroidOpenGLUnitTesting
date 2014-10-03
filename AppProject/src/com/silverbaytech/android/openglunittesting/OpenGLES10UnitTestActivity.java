/*
 * Copyright (c) 2013 Kevin Hunter
 *  http://www.silverbaytech.com/
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.silverbaytech.android.openglunittesting;

import java.util.concurrent.CountDownLatch;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

/**
 * This is an Activity that you include into your application in order to
 * support OpenGL unit tests.
 * 
 * @author Kevin Hunter
 * 
 */
public class OpenGLES10UnitTestActivity extends Activity
{
	/*
	 * GLSurfaceView that's going to run our tests for us
	 */
	private GLSurfaceView surfaceView;
	
	/*
	 * Renderer
	 */
	private EmptyRenderer renderer;

	/*
	 * Latch that's used to wait until the thread started by the GLSurfaceView
	 * has gotten to the point where the OpenGL environment has been set up.
	 */
	private CountDownLatch latch;

	public OpenGLES10UnitTestActivity()
	{
		latch = new CountDownLatch(1);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		renderer = new EmptyRenderer(latch);
		
		surfaceView = new GLSurfaceView(this);
		surfaceView.setEGLContextClientVersion(1);
		surfaceView.setRenderer(renderer);
		setContentView(surfaceView);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		surfaceView.onResume();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		surfaceView.onPause();
	}

	/**
	 * Retrieve the GLSurfaceView. Waits until the GLSurfaceView has set up the
	 * OpenGL environment and is calling the renderer methods.
	 * 
	 * @return GLSurfaceView instance.
	 * @throws InterruptedException
	 */
	public GLSurfaceView getSurfaceView() throws InterruptedException
	{
		/*
		 * If we haven't gotten the view before, wait until the renderer is
		 * called.
		 * 
		 * Nulling out the latch isn't technically necessary, since subsequent
		 * calls to await() should be benign, but...
		 */
		if (latch != null)
		{
			latch.await();
			latch = null;
		}
		return surfaceView;
	}
	
	public GL10 getGL10() throws InterruptedException
	{
		/*
		 * If we haven't gotten the GL10 before, wait until the renderer is
		 * called.
		 * 
		 * Nulling out the latch isn't technically necessary, since subsequent
		 * calls to await() should be benign, but...
		 */
		if (latch != null)
		{
			latch.await();
			latch = null;
		}
		return renderer.getGL10();
	}

	private static class EmptyRenderer implements GLSurfaceView.Renderer
	{
		private CountDownLatch latch;
		private GL10 gl10;

		public EmptyRenderer(CountDownLatch latch)
		{
			this.latch = latch;
		}
		
		public GL10 getGL10()
		{
			synchronized(this)
			{
				return gl10;
			}
		}

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config)
		{
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height)
		{
		}

		@Override
		public void onDrawFrame(GL10 gl)
		{
			/*
			 * Trigger the latch only the first time we get here.
			 * Ensuring we only do it once isn't technically necessary, since
			 * counting down a CountDownLatch that's already at zero is supposed
			 * to be benign, but...
			 */
			if (latch != null)
			{
				latch.countDown();
				latch = null;
			}
			
			synchronized(this)
			{
				this.gl10 = gl;
			}
		}
	}
}
