package com.example;
import org.joml.Matrix4f;
import org.joml.Vector3f;
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
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColorPointer;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glRotated;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glTranslatef;
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

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;


import com.example.*;


public class Main {

	// The window handle
	private long window;
	public int programID;
	public Map<String, Integer> uniforms;

	public ArrayList<Mesh> meshObjects;
	public Mesh cube;
	public Mesh ground;


	public float rotateX;
	public float rotateY;
	public float rotateZ;

	public boolean KEY_A_DOWN = false;
	public boolean KEY_D_DOWN = false;
	public boolean KEY_W_DOWN = false;
	public boolean KEY_S_DOWN = false;
	public boolean KEY_LEFT_DOWN = false;
	public boolean KEY_RIGHT_DOWN = false;
	public boolean KEY_UP_DOWN = false;
	public boolean KEY_DOWN_DOWN = false;
	public boolean KEY_I_DOWN = false;
	public boolean KEY_K_DOWN = false;
	public boolean KEY_J_DOWN = false;
	public boolean KEY_L_DOWN = false;




	private static final float FOV = (float) Math.toRadians(60.0f);
	private static final float Z_NEAR = 0.01f;
	private static final float Z_FAR = 1000.f;
	private Matrix4f projectionMatrix;
	private Matrix4f worldMatrix;

	public int WIDTH = 1000;
	public int HEIGHT = 1000;

	Vector3f offset;
	Vector3f rotation;
	float scale;
	
	public static void main(String[] args) throws Exception {
		new Main().run();
	}

	public void run() throws Exception {
		

		

		init_window();

		initialiseWorldView();
		initialiseGraphics();
		makeObjects();
	
		loop();

		cleanup();
	}


	private void loop() {
		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while ( !glfwWindowShouldClose(window) ) {		
			do_key_stuff();

			Render();

			glfwSwapBuffers(window); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}
	}

	public void Render() {

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

		glUseProgram(programID);
		if (glGetProgrami(programID, GL_LINK_STATUS) == 0) {			
			throw new RuntimeException("Error linking Shader code: " + glGetProgramInfoLog(programID, 1024));
		}
		setUniform("projectionMatrix", projectionMatrix);

		Matrix4f localWorldMatrix = getWorldMatrix();


		for (Mesh aMesh : meshObjects){
			Vector3f aPos = aMesh.getPos();
			localWorldMatrix = getWorldMatrix();
			localWorldMatrix.translate(aPos);
			setUniform("worldMatrix", localWorldMatrix);			
			glBindVertexArray(aMesh.getMeshID());
			
    		glDrawElements(GL_TRIANGLES, aMesh.getVertexCount(), GL_UNSIGNED_INT, 0);	
		}
		glBindVertexArray(0);
		glUseProgram(0);

		
	}

	private void do_key_stuff(){

		if (KEY_D_DOWN){
			rotation.y += 1f;
			if (rotation.y>360f) rotation.y -= 360f;
		}
		if (KEY_A_DOWN){
			rotation.y -= 1f;
			if (rotation.y<0) rotation.y += 360f;
		}
		if (KEY_W_DOWN){
			rotation.x += 1f;
			if (rotation.z>360f) rotation.z -= 360f;
		}
		if (KEY_S_DOWN){
			rotation.x -= 1f;
			if (rotation.z<0f) rotation.z += 360f;
		}


		if (KEY_LEFT_DOWN){
			offset.x += 0.01f;
		}
		if (KEY_RIGHT_DOWN){
			offset.x -= 0.01f;
		}
		if (KEY_DOWN_DOWN){
			offset.z -= 0.01f;
		}
		if (KEY_UP_DOWN){
			offset.z += 0.01f;
		}

		if (KEY_I_DOWN){			
			meshObjects.getFirst().setPosZ(-0.01f);
		}
		if (KEY_K_DOWN){			
			meshObjects.getFirst().setPosZ(0.01f);
		}

		if (KEY_J_DOWN){			
			meshObjects.getFirst().setPosX(-0.01f);
		}
		if (KEY_L_DOWN){			
			meshObjects.getFirst().setPosX(0.01f);
		}
		
	}

	private void init_window() {
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
		window = glfwCreateWindow(WIDTH, HEIGHT, "Hello World!", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			keyCallBack(key, action);
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
		
		float aspectRatio = (float) WIDTH / HEIGHT;
		projectionMatrix = new Matrix4f().perspective(FOV, aspectRatio,	Z_NEAR, Z_FAR);
		
		glMatrixMode(GL_MODELVIEW);
		glClearColor( 0.0F, 0.0F, 0.0F, 1 );
		glEnable(GL_DEPTH_TEST);
	}


	




	public void initialiseWorldView(){

		rotation = new Vector3f(0,0,0);
		offset = new Vector3f(0,0,-5f);
		scale = 1;
		}

	public void initialiseGraphics() throws Exception{

		uniforms = new HashMap<>();
		programID = Shaders.makeShaders();
		worldMatrix = new Matrix4f();
		createUniform("projectionMatrix");
		createUniform("worldMatrix");

		}

	public void makeObjects(){
		meshObjects = new ArrayList<Mesh>();
		meshObjects.add(makeCube(1f, new Vector3f(0f,0.5f,0f)));
		meshObjects.add(makeCube(0.5f, new Vector3f(2f,0.25f,0f)));

		float[] col1 = {0.25f,0f,0f};
		float[] col2 = {0f,0.25f,0f};

		int count=0;
		for (int x=0; x<11; x++){
			for (int z=0; z<11; z++){
				if (count%2==0)
					meshObjects.add(makeSquare(x-5f, z-5f, 1.0f, col1));
				else
					meshObjects.add(makeSquare(x-5f, z-5f, 1.0f, col2));
				count++;
			}
		}

	}


	public Mesh makeCube(float size, Vector3f translate){

		float offset = size / 2f;

		float[] positions = new float[]{
			// VO
			-offset,  offset,  offset,
			// V1
			-offset, -offset,  offset,
			// V2
			offset, -offset,  offset,
			// V3
			offset,  offset,  offset,
			// V4
			-offset,  offset, -offset,
			// V5
			offset,  offset, -offset,
			// V6
			-offset, -offset, -offset,
			// V7
			offset, -offset, -offset,
};

		float[] colors = new float[]{
			0.5f, 0.0f, 0.0f,
			0.0f, 0.5f, 0.0f,
			0.0f, 0.0f, 0.5f,
			0.0f, 0.5f, 0.5f,
			0.5f, 0.0f, 0.0f,
			0.0f, 0.5f, 0.0f,
			0.0f, 0.0f, 0.5f,
			0.0f, 0.5f, 0.5f,
		};
		int[] indices = new int[]{
		 // Front face
		 0, 1, 3, 3, 1, 2,
		 // Top Face
		 4, 0, 3, 5, 4, 3,
		 // Right face
		 3, 2, 7, 5, 3, 7,
		 // Left face
		 6, 1, 0, 6, 0, 4,
		 // Bottom face
		 2, 1, 6, 2, 6, 7,
		 // Back face
		 7, 6, 4, 7, 4, 5,
		};

		return new Mesh("Cube", positions, colors, indices, translate);

	}

	

	public Mesh makeSquare(float x, float z, float size, float[] col){
		float[] positions = new float[]{
			x, 0f, z,
			x+size, 0f, z,
			x+size, 0f, z+size,
			x, 0f, z+size
		};

		float[] colors = new float[]{
				col[0], col[1], col[2],
				col[0], col[1], col[2],
				col[0], col[1], col[2],
				col[0], col[1], col[2]
		};

		int[] indices = new int[]{
			0, 1, 2, // first triangle
			0, 2, 3  // second triangle
		};

		return new Mesh("Square", positions, colors, indices);
	}


	
	

	public void cleanup() {
		//vboIdList.forEach(GL30::glDeleteBuffers);
		for (Mesh aMesh : meshObjects){
			glDeleteVertexArrays(aMesh.getMeshID());
		}
		
		
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
}

	public Matrix4f getWorldMatrix() {
		worldMatrix.identity().translate(offset).
			rotateX((float)Math.toRadians(rotation.x)).
			rotateY((float)Math.toRadians(rotation.y)).
			rotateZ((float)Math.toRadians(rotation.z)).
			scale(scale);
		return worldMatrix;
	}

	public void createUniform(String uniformName) throws Exception {
    int uniformLocation = glGetUniformLocation(programID, uniformName);
    if (uniformLocation < 0) {
			System.out.println("createUniform error");
      throw new Exception("Could not find uniform:" + uniformName);				
    }
    uniforms.put(uniformName, uniformLocation);
	}

	public void setUniform(String uniformName, Matrix4f value) {
		// Dump the matrix into a float buffer
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer fb = stack.mallocFloat(16);
			value.get(fb);
			glUniformMatrix4fv(uniforms.get(uniformName), false, fb);
		}
	}


	public void keyCallBack(int key, int action) {

		if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop				
		}

		if (key == GLFW_KEY_W && action == GLFW_PRESS) KEY_W_DOWN = true;
		else if (key == GLFW_KEY_W && action == GLFW_RELEASE) KEY_W_DOWN = false;

		if (key == GLFW_KEY_A && action == GLFW_PRESS) KEY_A_DOWN = true;
		else if (key == GLFW_KEY_A && action == GLFW_RELEASE) KEY_A_DOWN = false;

		if (key == GLFW_KEY_S && action == GLFW_PRESS) KEY_S_DOWN = true;
		else if (key == GLFW_KEY_S && action == GLFW_RELEASE) KEY_S_DOWN = false;

		if (key == GLFW_KEY_D && action == GLFW_PRESS) KEY_D_DOWN = true;
		else if (key == GLFW_KEY_D && action == GLFW_RELEASE) KEY_D_DOWN = false;

		if (key == GLFW_KEY_LEFT && action == GLFW_PRESS) KEY_LEFT_DOWN = true;
		else if (key == GLFW_KEY_LEFT && action == GLFW_RELEASE) KEY_LEFT_DOWN = false;

		if (key == GLFW_KEY_RIGHT && action == GLFW_PRESS) KEY_RIGHT_DOWN = true;
		else if (key == GLFW_KEY_RIGHT && action == GLFW_RELEASE) KEY_RIGHT_DOWN = false;

		if (key == GLFW_KEY_UP && action == GLFW_PRESS) KEY_UP_DOWN = true;
		else if (key == GLFW_KEY_UP && action == GLFW_RELEASE) KEY_UP_DOWN = false;

		if (key == GLFW_KEY_DOWN && action == GLFW_PRESS) KEY_DOWN_DOWN = true;
		else if (key == GLFW_KEY_DOWN && action == GLFW_RELEASE) KEY_DOWN_DOWN = false;

		if (key == GLFW_KEY_I && action == GLFW_PRESS) KEY_I_DOWN = true;
		else if (key == GLFW_KEY_I && action == GLFW_RELEASE) KEY_I_DOWN = false;

		if (key == GLFW_KEY_K && action == GLFW_PRESS) KEY_K_DOWN = true;
		else if (key == GLFW_KEY_K && action == GLFW_RELEASE) KEY_K_DOWN = false;

		if (key == GLFW_KEY_J && action == GLFW_PRESS) KEY_J_DOWN = true;
		else if (key == GLFW_KEY_J && action == GLFW_RELEASE) KEY_J_DOWN = false;

		if (key == GLFW_KEY_L && action == GLFW_PRESS) KEY_L_DOWN = true;
		else if (key == GLFW_KEY_L && action == GLFW_RELEASE) KEY_L_DOWN = false;
	}


}