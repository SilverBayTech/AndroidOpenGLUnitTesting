/*
 *  Copyright (c) 1013 Kevin Hunter
 *  http://www.silverbaytech.com/
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 *  you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at 
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0 
 *  
 *  Unless required by applicable law or agreed to in writing, software 
 *  distributed under the License is distributed on an "AS IS" BASIS, 
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 *  See the License for the specific language governing permissions and 
 *  limitations under the License. 
 */

package com.silverbaytech.android.openglunittesting;
import java.util.concurrent.CountDownLatch;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;
import android.test.ActivityInstrumentationTestCase2;

public class BaseOpenGLES10UnitTest extends ActivityInstrumentationTestCase2<OpenGLES10UnitTestActivity>
{
    private OpenGLES10UnitTestActivity activity;
    
    public BaseOpenGLES10UnitTest()
    {
        super(OpenGLES10UnitTestActivity.class);
    }
    
    @Override
    public void setUp()
    {
        activity = getActivity();   // force start
    }
    
    @Override
    public void tearDown()
    {
        activity.finish();
    }

    public void runOnGLThread(final TestWrapper test) throws Throwable
    {
        final CountDownLatch latch = new CountDownLatch(1);
        
        GLSurfaceView surfaceView = activity.getSurfaceView();
        final GL10 gl10 = activity.getGL10();

        surfaceView.queueEvent(new Runnable()
        {
            public void run()
            {
                test.executeWrapper(gl10);
                latch.countDown();
            }
        });

        latch.await();
        test.rethrowExceptions();
    }

    public static abstract class TestWrapper
    {
        private Error error = null;
        private Throwable throwable = null;

        public TestWrapper()
        {
        }

        public void executeWrapper(GL10 gl)
        {
            try
            {
                executeTest(gl);
            }
            catch (Error e)
            {
                synchronized (this)
                {
                    error = e;
                }
            }
            catch (Throwable t)
            {
                synchronized (this)
                {
                    throwable = t;
                }
            }
        }

        public void rethrowExceptions()
        {
            synchronized (this)
            {
                if (error != null)
                {
                    throw error;
                }

                if (throwable != null)
                {
                    throw new RuntimeException("Unexpected exception", throwable);
                }
            }
        }

        public abstract void executeTest(GL10 gl) throws Throwable;
    }

}

