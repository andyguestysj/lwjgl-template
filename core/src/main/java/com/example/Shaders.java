package com.example;

import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.lwjgl.opengl.GL30.*;
import com.example.Utils;

public class Shaders {


	public static int makeShaders(){ 
	
		int programID = glCreateProgram();

		int vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
		String vertexShaderCode = Utils.readFile("core/resources/shaders/shader.vert");    
		glShaderSource(vertexShaderID, vertexShaderCode);
    glCompileShader(vertexShaderID);
		if (vertexShaderID == 0) {
			throw new RuntimeException("Error creating shader. Type: " + GL_VERTEX_SHADER);
		}
		System.out.println(glGetShaderInfoLog(vertexShaderID, glGetShaderi(vertexShaderID, GL_INFO_LOG_LENGTH)));
    glAttachShader(programID, vertexShaderID);

		int fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);
		String fragShaderCode = Utils.readFile("core/resources/shaders/shader.frag");
    glShaderSource(fragmentShaderID, fragShaderCode);
    glCompileShader(fragmentShaderID);
		if (fragmentShaderID == 0) {
			throw new RuntimeException("Error creating shader. Type: " + GL_FRAGMENT_SHADER);
		}
		System.out.println(glGetShaderInfoLog(fragmentShaderID, glGetShaderi(fragmentShaderID, GL_INFO_LOG_LENGTH)));
    glAttachShader(programID, fragmentShaderID);

    glLinkProgram(programID);
		if (glGetProgrami(programID, GL_LINK_STATUS) == 0) {
			throw new RuntimeException("Error linking Shader code: " + glGetProgramInfoLog(programID, 1024));
		}
		System.out.println(glGetProgramInfoLog(programID, glGetProgrami(programID, GL_INFO_LOG_LENGTH)));

    glDeleteShader(vertexShaderID);
    glDeleteShader(fragmentShaderID);
	
    return programID;

	}
}
