/*
 * Copyright 2011 Stefan C. Mueller.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smurn.jply.lwjgldemo;

import java.nio.ShortBuffer;
import org.smurn.jply.Element;
import java.nio.FloatBuffer;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.smurn.jply.PlyReader;
import org.lwjgl.LWJGLException;
import static org.lwjgl.opengl.ARBVertexBufferObject.*;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.smurn.jply.ElementReader;
import org.smurn.jply.PlyReaderImpl;
import org.smurn.jply.util.NormalMode;
import org.smurn.jply.util.NormalizingPlyReader;
import org.smurn.jply.util.TesselationMode;
import org.smurn.jply.util.TextureMode;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 */
public class Demo {

    public static void main(String[] args) throws LWJGLException, IOException {
        Display.setDisplayMode(new DisplayMode(640, 480));
        Display.create();

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(-1, 1, -1, 1, -100, 100);
        glMatrixMode(GL_MODELVIEW);

        PlyReader reader = new PlyReaderImpl(ClassLoader.getSystemResourceAsStream("cube.ply"));
        reader = new NormalizingPlyReader(reader, 
                TesselationMode.TRIANGLES, 
                NormalMode.ADD_NORMALS_CCW, 
                TextureMode.DO_NOTHING);
        
        int vertexCount = reader.getElementCount("vertex");
        int faceCount = reader.getElementCount("face");

        int vertexBufferId = glGenBuffersARB();
        glBindBufferARB(
                GL_ARRAY_BUFFER_ARB, vertexBufferId);
        glBufferDataARB(GL_ARRAY_BUFFER_ARB,
                vertexCount * 24, GL_STATIC_DRAW_ARB);
        ByteBuffer vertexBuffer = glMapBufferARB(
                GL_ARRAY_BUFFER_ARB, GL_WRITE_ONLY_ARB, null);

        int indexBufferId = glGenBuffersARB();
        glBindBufferARB(
                GL_ELEMENT_ARRAY_BUFFER_ARB, indexBufferId);
        glBufferDataARB(GL_ELEMENT_ARRAY_BUFFER_ARB,
                faceCount * 12, GL_STATIC_DRAW_ARB);
        ByteBuffer indexBuffer = glMapBufferARB(
                GL_ELEMENT_ARRAY_BUFFER_ARB, GL_WRITE_ONLY_ARB, null);

        fillBuffers(reader, vertexBuffer, indexBuffer);
        
        glUnmapBufferARB(GL_ARRAY_BUFFER_ARB);
        glUnmapBufferARB(GL_ELEMENT_ARRAY_BUFFER_ARB);
        
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_NORMAL_ARRAY);
        
        glVertexPointer(3, GL_FLOAT, 24, 0);
        glNormalPointer(GL_FLOAT, 24, 12);

        glMatrixMode(GL_MODELVIEW);
        while (!Display.isCloseRequested()) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            glDrawElements(GL_TRIANGLES, faceCount * 3, GL_SHORT, 0);
            
            Display.update();
        }

        Display.destroy();
    }

    private static void fillBuffers(PlyReader plyReader, ByteBuffer vertexBuffer, ByteBuffer indexBuffer) throws IOException {
        ElementReader reader = plyReader.nextElementReader();
        while (reader != null) {

            if (reader.getElementType().getName().equals("vertex")) {
                fillVertexBuffer(reader, vertexBuffer.asFloatBuffer());
            } else if (reader.getElementType().getName().equals("face")) {
                fillFaceBuffer(reader, vertexBuffer.asShortBuffer());
            }
            reader.close();
            reader = plyReader.nextElementReader();
        }
    }

    private static void fillVertexBuffer(ElementReader reader, FloatBuffer vertexBuffer) throws IOException {
        Element element = reader.readElement();
        while (element != null) {

            vertexBuffer.put((float) element.getDouble("x"));
            vertexBuffer.put((float) element.getDouble("y"));
            vertexBuffer.put((float) element.getDouble("z"));
            vertexBuffer.put((float) element.getDouble("nx"));
            vertexBuffer.put((float) element.getDouble("ny"));
            vertexBuffer.put((float) element.getDouble("nz"));

            element = reader.readElement();
        }
    }

    private static void fillFaceBuffer(ElementReader reader, ShortBuffer faceBuffer) throws IOException {
        Element element = reader.readElement();
        while (element != null) {

            int[] indices = element.getIntList("vertex_index");
            for (int index : indices) {
                faceBuffer.put((short) index);
            }

            element = reader.readElement();
        }
    }
}
