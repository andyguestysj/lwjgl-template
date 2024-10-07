package com.example;
import org.joml.Matrix4f;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;

import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_ARRAY;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColorPointer;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glRotated;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;


public class Main {

	public boolean VAO;
	// The window handle
	private long window;

	public int count;
	
	public int numVertices;
	public int vaoId;	
  public List<Integer> vboIdList;

	public int programID;
	public int vertexShaderID;
	public int fragmentShaderID;	
	public Map<String, Integer> uniforms;

	

	public FloatBuffer cubeCoordBuffer ;
	public FloatBuffer cubeFaceColorBuffer ;

	public float rotateX;
	public float rotateY;
	public float rotateZ;
	

	public void run() {
		count=0;
		VAO = true;
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");

		init();
				
		makeShader();
		
		if (VAO)
			makeShapes();
		else
			makeShapes2();

		loop();
		cleanup();
	}


	private void makeShader(){ 
	
		String vshaderSource[ ] =
		{ 
			"#version 330 \n",
			"layout (location=0) in vec3 position; \n",
			"layout (location=1) in vec3 color; \n",
			"out vec3 outColor; \n",
			"void main() \n",
			"{ gl_Position = vec4(position, 1.0); \n",
			" outColor = color;}"
			
		};

		String fshaderSource[ ] =
		{ "#version 330 \n",
			"in vec3 outColor; \n",
			"out vec4 fragColor; \n",
			"void main(void) \n",
			"{ fragColor = vec4(outColor, 1.0); }"
		};

    
		int count=0;
		
		programID = glCreateProgram();

		vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
    glShaderSource(vertexShaderID, vshaderSource);
    glCompileShader(vertexShaderID);
		if (vertexShaderID == 0) {
			throw new RuntimeException("Error creating shader. Type: " + GL_VERTEX_SHADER);
		}
		System.out.println(glGetShaderInfoLog(vertexShaderID, glGetShaderi(vertexShaderID, GL_INFO_LOG_LENGTH)));
    glAttachShader(programID, vertexShaderID);

		fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);
    glShaderSource(fragmentShaderID, fshaderSource);
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
	
	}

	private void makeShapes2(){
		
		 rotateX = 0;
		 rotateY = 0;
		 rotateZ = 0;


		 float[] cubeCoords = {
			1,1,1,    -1,1,1,   -1,-1,1,   1,-1,1,      // face #1
			1,1,1,     1,-1,1,   1,-1,-1,  1,1,-1,      // face #2
			1,1,1,     1,1,-1,  -1,1,-1,  -1,1,1,       // face #3
			-1,-1,-1, -1,1,-1,   1,1,-1,   1,-1,-1,     // face #4
			-1,-1,-1, -1,-1,1,  -1,1,1,   -1,1,-1,      // face #5
			-1,-1,-1,  1,-1,-1,  1,-1,1,   -1,-1,1  };  // face #6

		float[] cubeFaceColors = {
			1,0,0,  1,0,0,  1,0,0,  1,0,0,      // face #1 is red
			0,1,0,  0,1,0,  0,1,0,  0,1,0,      // face #2 is green
			0,0,1,  0,0,1,  0,0,1,  0,0,1,      // face #3 is blue
			1,1,0,  1,1,0,  1,1,0,  1,1,0,      // face #4 is yellow
			0,1,1,  0,1,1,  0,1,1,  0,1,1,      // face #5 is cyan
			1,0,1,  1,0,1,  1,0,1,  1,0,1,   }; // face #6 is red
	
			try (MemoryStack stack = MemoryStack.stackPush()){
				cubeCoordBuffer = stack.callocFloat(cubeCoords.length);
				cubeCoordBuffer.put(0,cubeCoords);
				cubeFaceColorBuffer  = stack.callocFloat(cubeFaceColors.length);
				cubeFaceColorBuffer.put(0,cubeFaceColors);

			 }

		}

	private void makeShapes(){

		float[] positions = new float[]{
			-0.5f, 0.5f, 0.0f,
			-0.5f, -0.5f, 0.0f,
			0.5f, -0.5f, 0.0f,
			0.5f, 0.5f, 0.0f,
};

		float[] colors = new float[]{
				0.5f, 0.0f, 0.0f,
				0.5f, 0.5f, 0.0f,
				0.5f, 0.0f, 0.5f,
				0.0f, 0.0f, 0.5f
		};
		int[] indices = new int[]{
				0, 1, 2, // first triangle
				0, 2, 3  // second triangle
		};

		numVertices = indices.length;

		try (MemoryStack stack = MemoryStack.stackPush()) {

			vboIdList = new ArrayList<>();

			vaoId = glGenVertexArrays();
			glBindVertexArray(vaoId);

			int vboId = glGenBuffers();
			vboIdList.add(vboId);
			FloatBuffer positionsBuffer = stack.callocFloat(positions.length);
			positionsBuffer.put(0,positions);
			glBindBuffer(GL_ARRAY_BUFFER, vboId);
			glBufferData(GL_ARRAY_BUFFER, positionsBuffer, GL_STATIC_DRAW);
			glEnableVertexAttribArray(0);
			glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            // Color VBO
			vboId = glGenBuffers();
			vboIdList.add(vboId);
			FloatBuffer colorsBuffer = stack.callocFloat(colors.length);
			colorsBuffer.put(0, colors);
			glBindBuffer(GL_ARRAY_BUFFER, vboId);
			glBufferData(GL_ARRAY_BUFFER, colorsBuffer, GL_STATIC_DRAW);
			glEnableVertexAttribArray(1);
			glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);

			// Index VBO
			vboId = glGenBuffers();
			vboIdList.add(vboId);
			IntBuffer indicesBuffer = stack.callocInt(indices.length);
			indicesBuffer.put(0, indices);
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

			glBindBuffer(GL_ARRAY_BUFFER, 0);
			glBindVertexArray(0);
			
			
			

		}
	}


	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		// Create the window
		window = glfwCreateWindow(1000, 800, "Hello World!", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		});

		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
		GL.createCapabilities();

		glMatrixMode(GL_PROJECTION);
		glOrtho(-4, 4, -2, 2, -2, 2);  // simple orthographic projection
		glMatrixMode(GL_MODELVIEW);
		glClearColor( 0.5F, 0.5F, 0.5F, 1 );
		glEnable(GL_DEPTH_TEST);
	}

	public void DrawStuff2() {
//		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

		

		glLoadIdentity();
		glTranslated(-2, 0, 0);     // Move cube to left half of window.
        
		glRotated(rotateZ,0,0,1);     // Apply rotations.
		glRotated(rotateY,0,1,0);
		glRotated(rotateX,1,0,0);

			
		glVertexPointer( 3, GL_FLOAT, 0, cubeCoordBuffer  );  // Set data type and location, second cube.
		glColorPointer( 3, GL_FLOAT, 0, cubeFaceColorBuffer  );
		
		glEnableClientState( GL_VERTEX_ARRAY );
		glEnableClientState( GL_COLOR_ARRAY );

 		glDrawArrays( GL_QUADS, 0, 24 ); // Draw the first cube!

	}

public void DrawStuff() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
 		glViewport(0, 0, 1000,800);
		

		glUseProgram(programID);
		if (glGetProgrami(programID, GL_LINK_STATUS) == 0) {			
			throw new RuntimeException("Error linking Shader code: " + glGetProgramInfoLog(programID, 1024));
		}

		glBindVertexArray(vaoId);
    glDrawElements(GL_TRIANGLES, numVertices, GL_UNSIGNED_INT, 0);

		glBindVertexArray(0);
		glUseProgram(0);

		
	}

	
	private void loop() {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.

		

		// Set the clear color
		

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while ( !glfwWindowShouldClose(window) ) {
			
			//glViewport(0, 0, 1000,800);

			rotateX += 2f;
			rotateY += 2f;
			if (rotateX>360) rotateX -= 360f;
			if (rotateY>360) rotateY -= 360f;

			if (VAO)
				DrawStuff();
			else
				DrawStuff2();

			glfwSwapBuffers(window); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}
	}

	public void cleanup() {
		//vboIdList.forEach(GL30::glDeleteBuffers);
		glDeleteVertexArrays(vaoId);
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
}

	public static void main(String[] args) {
		new Main().run();
	}

}