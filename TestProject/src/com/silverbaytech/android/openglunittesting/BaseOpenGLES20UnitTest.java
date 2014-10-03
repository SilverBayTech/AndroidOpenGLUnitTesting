/*
 *  Copyright (c) 2013 Kevin Hunter
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

import android.test.ActivityInstrumentationTestCase2;

public class BaseOpenGLES20UnitTest extends ActivityInstrumentationTestCase2<OpenGLES20UnitTestActivity>
{
    private OpenGLES20UnitTestActivity activity;
    
    public BaseOpenGLES20UnitTest()
    {
        super(OpenGLES20UnitTestActivity.class);
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

        activity.getSurfaceView().queueEvent(new Runnable()
        {
            public void run()
            {
                test.executeWrapper();
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

        public void executeWrapper()
        {
            try
            {
                executeTest();
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

        public abstract void executeTest() throws Throwable;
    }

}

