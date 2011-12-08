varying vec3 normal;
varying vec3 eyeVec;
varying vec3 lightDir;

void main(){
    gl_Position = ftransform();
    normal = gl_NormalMatrix * gl_Normal;
    vec4 vVertex = gl_ModelViewMatrix * gl_Vertex;
    eyeVec = vec3(0.0,0.0,1.0);
}
