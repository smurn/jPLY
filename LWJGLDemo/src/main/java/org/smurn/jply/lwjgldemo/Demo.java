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
import java.nio.ByteOrder;
import org.smurn.jply.PlyReader;
import org.lwjgl.LWJGLException;
import static org.lwjgl.opengl.ARBVertexBufferObject.*;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.Util;
import org.smurn.jply.ElementReader;
import org.smurn.jply.PlyReaderImpl;
import org.smurn.jply.util.Axis;
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

        PlyReader plyReader = new PlyReaderImpl(ClassLoader.getSystemResourceAsStream("bunny.ply"));
        plyReader = new NormalizingPlyReader(plyReader,
                TesselationMode.TRIANGLES,
                NormalMode.DO_NOTHING,
                TextureMode.DO_NOTHING,
                Axis.X,
                Axis.Z,
                Axis.Y);



        int vertexCount = plyReader.getElementCount("vertex");
        //FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(vertexCount * 12).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer();

        int faceCount = plyReader.getElementCount("face");
        //ShortBuffer indexBuffer = ByteBuffer.allocateDirect(faceCount * 12).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();

        int vertexBufferId = glGenBuffersARB();
        glBindBufferARB(GL_ARRAY_BUFFER_ARB, vertexBufferId);
        //glBufferDataARB(GL_ARRAY_BUFFER_ARB, vertexBuffer, GL_STATIC_DRAW_ARB);
        glBufferDataARB(GL_ARRAY_BUFFER_ARB, vertexCount * 12, GL_STATIC_DRAW_ARB);
        FloatBuffer vertexBuffer = glMapBufferARB(GL_ARRAY_BUFFER_ARB, GL_WRITE_ONLY_ARB, null).asFloatBuffer();

        int indexBufferId = glGenBuffersARB();
        glBindBufferARB(GL_ELEMENT_ARRAY_BUFFER_ARB, indexBufferId);
        //glBufferDataARB(GL_ELEMENT_ARRAY_BUFFER_ARB, indexBuffer, GL_STATIC_DRAW_ARB);
        glBufferDataARB(GL_ELEMENT_ARRAY_BUFFER_ARB, faceCount * 12, GL_STATIC_DRAW_ARB);
        ShortBuffer indexBuffer = glMapBufferARB(GL_ELEMENT_ARRAY_BUFFER_ARB, GL_WRITE_ONLY_ARB, null).asShortBuffer();

        RectBounds bounds = fillBuffers(plyReader, vertexBuffer, indexBuffer);
        vertexBuffer.clear();
        indexBuffer.clear();

        glUnmapBufferARB(GL_ARRAY_BUFFER_ARB);
        glUnmapBufferARB(GL_ELEMENT_ARRAY_BUFFER_ARB);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        float f = 1;
        glOrtho(bounds.getMinX()*f, bounds.getMaxX()*f, bounds.getMinY()*f, bounds.getMaxY()*f, -100, 100);
        glMatrixMode(GL_MODELVIEW);
        
        Util.checkGLError();

        glMatrixMode(GL_MODELVIEW);
        while (!Display.isCloseRequested()) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            glEnableClientState(GL_VERTEX_ARRAY);
            glVertexPointer(3, GL_FLOAT, 0, 0);

            glDrawElements(GL_TRIANGLE_FAN, faceCount * 3, GL_UNSIGNED_SHORT, 0);

            Util.checkGLError();
            Display.update();
            Thread.yield();
        }

        Display.destroy();
    }

    private static RectBounds fillBuffers(PlyReader plyReader,
            FloatBuffer vertexBuffer, ShortBuffer indexBuffer) throws IOException {
        ElementReader reader = plyReader.nextElementReader();
        RectBounds bounds = null;
        while (reader != null) {

            if (reader.getElementType().getName().equals("vertex") && vertexBuffer != null) {
                bounds = fillVertexBuffer(reader, vertexBuffer);
            } else if (reader.getElementType().getName().equals("face") && indexBuffer != null) {
                fillFaceBuffer(reader, indexBuffer);
            }
            reader.close();
            reader = plyReader.nextElementReader();
        }
        return bounds;
    }

    private static RectBounds fillVertexBuffer(ElementReader reader, FloatBuffer vertexBuffer) throws IOException {
        Element element = reader.readElement();
        RectBounds bounds = new RectBounds();
        while (element != null) {

            double x = element.getDouble("x");
            double y = element.getDouble("y");
            double z = element.getDouble("z");
            bounds.addPoint(x, y, z);

            vertexBuffer.put((float) x);
            vertexBuffer.put((float) y);
            vertexBuffer.put((float) z);
            /*
            vertexBuffer.put((float) element.getDouble("nx"));
            vertexBuffer.put((float) element.getDouble("ny"));
            vertexBuffer.put((float) element.getDouble("nz"));
             */
            element = reader.readElement();
        }
        return bounds;
    }

    private static void fillFaceBuffer(ElementReader reader, ShortBuffer indexBuffer) throws IOException {
        Element element = reader.readElement();
        while (element != null) {

            int[] indices = element.getIntList("vertex_index");
            for (int index : indices) {
                indexBuffer.put((short) index);
            }

            element = reader.readElement();
        }
    }
}
