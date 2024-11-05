package com.example;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.system.MemoryStack;
import java.util.*;

import static org.lwjgl.opengl.GL30.*;

public class Mesh {

  public String name;
  public String file;

  public int vertCount;
  public int vaoId;	

  

  public Mesh(float[] positions, float[] colours, int[] indices) {
    vertCount = indices.length;
    
    List<Integer> vboIdList;

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
			FloatBuffer colorsBuffer = stack.callocFloat(colours.length);
			colorsBuffer.put(0, colours);
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

	public Mesh(float[] positions, int[] indices) {
    vertCount = indices.length;
    
    List<Integer> vboIdList;

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
			glVertexAttribPointer(0, 3, GL_FLOAT, false, 6*Float.BYTES, 0);

			glEnableVertexAttribArray(1);
			glVertexAttribPointer(1, 3, GL_FLOAT, false, 6*Float.BYTES, 3*Float.BYTES);

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
	
	public int getMeshID() { return vaoId;}
  public int getVertexCount() { return vertCount;}

	

}
