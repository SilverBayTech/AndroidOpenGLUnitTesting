/*
 * Copyright (c) 2014 Kevin Hunter
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

package com.silverbaytech.android.openglunittesting.opengl;

import android.opengl.GLES20;
import android.util.Log;

/**
 * This is a sample OpenGL-based class that we want to unit test.
 * 
 * @author Kevin Hunter
 * 
 */
public class ShaderUtils
{
	/**
	 * Utility to compile code for a vertex or fragment shader.
	 * 
	 * @param type One of <code>GLES20.GL_VERTEX_SHADER</code> or
	 *            <code>GLES20.GL_FRAGMENT_SHADER</code>
	 * @param shaderCode The code for the shader
	 * @return OpenGL handle for the shader. Returns zero if the shader failed
	 *         to compile.
	 */
	public static int compileShader(int type, String shaderCode)
	{
		int shaderHandle = GLES20.glCreateShader(type);
		GLES20.glShaderSource(shaderHandle, shaderCode);
		GLES20.glCompileShader(shaderHandle);

		int[] compileStatus = new int[1];
		GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

		if (compileStatus[0] == 0)
		{
			StringBuilder builder = new StringBuilder();
			builder.append("Error compiling ");
			builder.append(type == GLES20.GL_VERTEX_SHADER ? "vertex" : "fragment");
			builder.append(" shader: ");
			builder.append(GLES20.glGetShaderInfoLog(shaderHandle));
			Log.e("ShaderUtils", builder.toString());
			GLES20.glDeleteShader(shaderHandle);
			return 0;
		}

		return shaderHandle;
	}
}
