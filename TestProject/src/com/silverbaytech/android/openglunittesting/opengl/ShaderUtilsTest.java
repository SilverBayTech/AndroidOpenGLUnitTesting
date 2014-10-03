/*
 *  Copyright (c) 2014 Kevin Hunter
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

package com.silverbaytech.android.openglunittesting.opengl;

import android.opengl.GLES20;

import com.silverbaytech.android.openglunittesting.BaseOpenGLES20UnitTest;

public class ShaderUtilsTest extends BaseOpenGLES20UnitTest
{
    private static final String validFragmentShaderCode =
        "precision mediump float;                            \n" +
        "void main()                                         \n" +
        "{                                                   \n" +
        "  gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);          \n" +
        "}                                                   \n";

    private static final String invalidFragmentShaderCode =
        "precision mediump float;                            \n" +
        "void main()                                         \n" +
        "{                                                   \n" +
        "  syntax error                                      \n" +
        "}                                                   \n";
    
	public ShaderUtilsTest()
	{
	}
	
    public void test_compile_withValidCode_compiles() throws Throwable
    {
        runOnGLThread(new TestWrapper()
        {
            @Override
            public void executeTest() throws Throwable
            {
            	int handle = ShaderUtils.compileShader(GLES20.GL_FRAGMENT_SHADER, validFragmentShaderCode);
                assertTrue(handle != 0);
                assertTrue(GLES20.glIsShader(handle));
            }
        });
    }
	
    public void test_compile_withInvalidCode_fails() throws Throwable
    {
        runOnGLThread(new TestWrapper()
        {
            @Override
            public void executeTest() throws Throwable
            {
            	int handle = ShaderUtils.compileShader(GLES20.GL_FRAGMENT_SHADER, invalidFragmentShaderCode);
                assertTrue(handle == 0);
            }
        });
    }
}

