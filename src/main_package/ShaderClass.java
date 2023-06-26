package main_package;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL46;
import org.lwjgl.opengl.GL11;

public class ShaderClass {
	
	static public int loadShaderSource_And_Compile(String filePath, int type)
	{
		int sh = -1;
		
		switch(type)
		{
		case 0: // VS
			sh = GL46.glCreateShader(GL46.GL_VERTEX_SHADER);
			break;
		case 1:
			sh = GL46.glCreateShader(GL46.GL_GEOMETRY_SHADER);
			break;
		case 2:
			sh = GL46.glCreateShader(GL46.GL_FRAGMENT_SHADER);
			break;
		case 3:
			sh = GL46.glCreateShader(GL46.GL_COMPUTE_SHADER);
			break;
		default:
			return -1;
		}
		
		// get shader source
		try {
			// open file
			String shStr = new String(Files.readAllBytes(Paths.get(filePath)));
			
			// supply shader source
			GL46.glShaderSource(sh, shStr);
			
			// compile shader
			GL46.glCompileShader(sh);
			
			// error check
			int logLen = GL46.glGetShaderi(sh, GL46.GL_INFO_LOG_LENGTH);
			
			if( logLen > 1 )
			{
				System.out.println("compile ERROR:" + GL46.glGetShaderInfoLog(sh));
			}
			else
			{
				System.out.println("compile SUCCESS!");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sh;
	}
	
	
	static public int createProgram_And_AttachShader(int vs, int gs, int fs)
	{
		int PRG = GL46.glCreateProgram();
		
		if( vs != -1) { GL46.glAttachShader(PRG, vs);}
		if( gs != -1) { GL46.glAttachShader(PRG, gs);}
		if( fs != -1) { GL46.glAttachShader(PRG, fs);}
		
		GL46.glLinkProgram(PRG);
		
		// check error
		int status = GL46.glGetProgrami(PRG, GL46.GL_LINK_STATUS);
		if(status == GL46.GL_FALSE)
		{
			System.out.println(GL46.glGetProgramInfoLog(PRG));
		}
		else if(status == GL46.GL_TRUE)
		{
			System.out.println("PROGRAM LINK SUCCESS!");
		}
		
		return PRG;
	}
	
	static public int getUniformLocation(int PRG, String name)
	{
		int UNF = GL46.glGetUniformLocation(PRG, name);
		System.out.println("PRG/"+PRG+" - " + name+"/"+UNF);
		return UNF;
	}
	
}
