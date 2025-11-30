#version 120

uniform vec3 color;
uniform float time;
uniform vec2 resolution;

void main() {
    gl_FragColor = vec4(color, 1.0);
}
