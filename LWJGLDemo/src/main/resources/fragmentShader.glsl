#define LIGHT_COUNT 3

uniform vec3 lightDir[LIGHT_COUNT];
uniform vec4 diffuseColor[LIGHT_COUNT];
uniform vec4 specularColor[LIGHT_COUNT];
uniform float shininess[LIGHT_COUNT];
varying vec3 normal;
varying vec3 eyeVec;


void main(){
  vec4 color = vec4(0.0, 0.0, 0.0, 1.0);
  vec3 N = normalize(normal);
  int i;
  for (i=0; i<LIGHT_COUNT; ++i){
    vec3 L = normalize(lightDir[i]);
    float lambertTerm = dot(N,L);
    if (lambertTerm > 0.0)
    {
      color += diffuseColor[i] * lambertTerm;
      vec3 E = normalize(eyeVec);
      vec3 R = reflect(-L, N);
      float specular = pow(max(dot(R, E), 0.0), shininess[i]);
      color += specularColor[i] * specular;
    }
  }
  gl_FragColor = color;
}
