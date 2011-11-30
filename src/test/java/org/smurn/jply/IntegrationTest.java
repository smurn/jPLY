/*
 * Copyright 2011 stefan.
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
package org.smurn.jply;

import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 */
public class IntegrationTest {

    @Test
    public void cube() throws IOException {

        PlyReader reader = new PlyReader(
                ClassLoader.getSystemResourceAsStream("cube.ply"));
        ElementReader vertices = reader.nextElementReader();

        Element vertex;
        assertNotNull(vertex = vertices.readElement());
        assertEquals(0, vertex.getInt("x"));
        assertEquals(0, vertex.getInt("y"));
        assertEquals(0, vertex.getInt("z"));

        assertNotNull(vertex = vertices.readElement());
        assertEquals(0, vertex.getInt("x"));
        assertEquals(0, vertex.getInt("y"));
        assertEquals(1, vertex.getInt("z"));

        assertNotNull(vertex = vertices.readElement());
        assertNotNull(vertex);
        assertEquals(0, vertex.getInt("x"));
        assertEquals(1, vertex.getInt("y"));
        assertEquals(1, vertex.getInt("z"));

        assertNotNull(vertex = vertices.readElement());
        assertNotNull(vertex);
        assertEquals(0, vertex.getInt("x"));
        assertEquals(1, vertex.getInt("y"));
        assertEquals(0, vertex.getInt("z"));

        assertNotNull(vertex = vertices.readElement());
        assertNotNull(vertex);
        assertEquals(1, vertex.getInt("x"));
        assertEquals(0, vertex.getInt("y"));
        assertEquals(0, vertex.getInt("z"));

        assertNotNull(vertex = vertices.readElement());
        assertNotNull(vertex);
        assertEquals(1, vertex.getInt("x"));
        assertEquals(0, vertex.getInt("y"));
        assertEquals(1, vertex.getInt("z"));

        assertNotNull(vertex = vertices.readElement());
        assertNotNull(vertex);
        assertEquals(1, vertex.getInt("x"));
        assertEquals(1, vertex.getInt("y"));
        assertEquals(1, vertex.getInt("z"));

        assertNotNull(vertex = vertices.readElement());
        assertNotNull(vertex);
        assertEquals(1, vertex.getInt("x"));
        assertEquals(1, vertex.getInt("y"));
        assertEquals(0, vertex.getInt("z"));
        
        assertNull(vertex = vertices.readElement());
        vertices.close();

        ElementReader faces = reader.nextElementReader();
        Element face;
        assertNotNull(face = faces.readElement());
        assertArrayEquals(new int[]{0, 1, 2, 3}, face.getIntList("vertex_index"));
        
        assertNotNull(face = faces.readElement());
        assertArrayEquals(new int[]{7, 6, 5, 4}, face.getIntList("vertex_index"));
        
        assertNotNull(face = faces.readElement());
        assertArrayEquals(new int[]{0, 4, 5, 1}, face.getIntList("vertex_index"));
        
        assertNotNull(face = faces.readElement());
        assertArrayEquals(new int[]{1, 5, 6, 2}, face.getIntList("vertex_index"));
        
        assertNotNull(face = faces.readElement());
        assertArrayEquals(new int[]{2, 6, 7, 3}, face.getIntList("vertex_index"));
        
        assertNotNull(face = faces.readElement());
        assertArrayEquals(new int[]{3, 7, 4, 0}, face.getIntList("vertex_index"));
        
        assertNull(face = faces.readElement());
    }
}
