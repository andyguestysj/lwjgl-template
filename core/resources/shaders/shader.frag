#version 330

in vec3 outColor;
out vec4 fragColor;

void main(void)
{ 
  fragColor = vec4(outColor, 1.0); 
}