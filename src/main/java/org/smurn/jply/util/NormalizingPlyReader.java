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
package org.smurn.jply.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.smurn.jply.DataType;
import org.smurn.jply.ElementReader;
import org.smurn.jply.ElementType;
import org.smurn.jply.PlyReader;
import org.smurn.jply.Property;

/**
 * Ply Reader that does some normalization on the data.
 * <p>This wrapper helps decoupling the user's code from the specifics of
 * individual PLY files.</p>
 */
public class NormalizingPlyReader implements PlyReader {

    private final RandomPlyReader plyReader;
    private final NormalMode normalMode;
    private final boolean generateNormals;
    private final List<ElementType> elementTypes;

    /**
     * Creates an instance.
     * @param plyReader The reader providing the data to be normalized.
     * @param tesslationMode    Tesslation operation.
     * @param normalMode Normal vector generation operation.
     */
    public NormalizingPlyReader(final PlyReader plyReader,
            final TesslationMode tesslationMode,
            final NormalMode normalMode) {

        if (plyReader == null) {
            throw new NullPointerException("plyReader must not be null.");
        }
        if (tesslationMode == null) {
            throw new NullPointerException("tesslationMode must not be null.");
        }
        if (normalMode == null) {
            throw new NullPointerException("normalMode must not be null.");
        }

        // Make a map to find the element types by name
        Map<String, ElementType> typeMap = new HashMap<String, ElementType>();
        for (ElementType type : plyReader.getElementTypes()) {
            typeMap.put(type.getName(), type);
        }

        if (!typeMap.containsKey("vertex")) {
            throw new IllegalArgumentException(
                    "PLY file contains no vertex data.");
        }

        if (!typeMap.containsKey("face")) {
            throw new IllegalArgumentException(
                    "PLY file contains no face data.");
        }

        List<WrappingPlyReader.WrapperFactory> wrappers =
                new LinkedList<WrappingPlyReader.WrapperFactory>();

        // Add the triangulation wrapper if required
        if (tesslationMode == TesslationMode.TRIANGLES) {
            wrappers.add(new WrappingPlyReader.WrapperFactory(
                    typeMap.get("face"), typeMap.get("face")) {

                @Override
                public ElementReader wrap(final ElementReader reader) {
                    return new TriangulatingFaceReader(reader);
                }
            });
        }

        // Add the type changer wrapper if we need to add vertex properties
        if (normalMode != NormalMode.DO_NOTHING) {
            final ElementType unwrapped = typeMap.get("vertex");
            final ElementType withNormal = addNormalProps(unwrapped);
            if (!unwrapped.equals(withNormal)) {
                generateNormals = true;
                typeMap.put("vertex", withNormal);
                wrappers.add(new WrappingPlyReader.WrapperFactory(
                        unwrapped, withNormal) {

                    @Override
                    public ElementReader wrap(final ElementReader reader) {
                        return new TypeChangingElementReader(
                                reader, withNormal);
                    }
                });
            } else {
                generateNormals = false;
            }
        } else {
            generateNormals = false;
        }

        this.normalMode = normalMode;
        this.plyReader = new RandomPlyReader(
                new WrappingPlyReader(plyReader, wrappers));


        // prepare the types we offer (in the order of the source)
        List<ElementType> types = new ArrayList<ElementType>();
        for (ElementType type : plyReader.getElementTypes()) {
            types.add(typeMap.get(type.getName()));
        }
        elementTypes = Collections.unmodifiableList(types);
    }

    /**
     * Adds properties for nx, ny and nz if they don't already exist.
     * @param sourceType Source vertex type.
     * @return Vertex type guaranteed to have nx, ny and nz.
     */
    private static ElementType addNormalProps(final ElementType sourceType) {
        List<Property> properties = new ArrayList<Property>();
        boolean foundNX = false;
        boolean foundNY = false;
        boolean foundNZ = false;
        for (Property property : sourceType.getProperties()) {
            if (property.getName().equals("nx")) {
                foundNX = true;
            }
            if (property.getName().equals("ny")) {
                foundNY = true;
            }
            if (property.getName().equals("nz")) {
                foundNZ = true;
            }
            properties.add(property);
        }
        if (foundNX && foundNY && foundNZ) {
            return sourceType;
        }
        if (!foundNX) {
            properties.add(new Property("nx", DataType.DOUBLE));
        }
        if (!foundNY) {
            properties.add(new Property("ny", DataType.DOUBLE));
        }
        if (!foundNZ) {
            properties.add(new Property("nz", DataType.DOUBLE));
        }
        return new ElementType("vertex", properties);
    }

    /**
     * Gets all element types in this PLY file.
     * <p>The order of the list
     * is the same in which the corresponding readers are returned
     * by {@link #nextElementReader()}.</p>
     * @return Immutable list with all element types.
     */
    @Override
    public List<ElementType> getElementTypes() {
        return elementTypes;
    }

    /**
     * Gets the number of elements for a given element type.
     * @param elementType Name of the element type.
     * @return Number of elements of the given type.
     * @throws NullPointerException if {@code elementType} is {@code null}.
     * @throws IllegalArgumentException if there is no such type in this
     * file.
     */
    @Override
    public int getElementCount(final String elementType) {
        return plyReader.getElementCount(elementType);
    }

    /**
     * Returns the reader to read the first group of elements.
     * <p>Each group corresponds to an element type. The groups are
     * returned in the order given in the file. This is also the
     * same order as in the list given by {@link #getElementTypes()}.</p>
     * <p>Each returned reader must be closed before the next reader
     * is requested with this method.</p>
     * @return Reader for the next group of elements or {@code null} if
     * there are no more groups.
     * @throws IOException if an error occurs during reading.
     */
    @Override
    public ElementReader nextElementReader() throws IOException {
        RandomElementReader reader = plyReader.nextElementReader();
        if (generateNormals
                && reader.getElementType().getName().equals("vertex")) {

            RandomElementReader faces = plyReader.getElementReader("face");

            NormalGenerator generator = new NormalGenerator();
            if (normalMode == NormalMode.ADD_NORMALS_CW) {
                generator.setCounterClockwise(true);
            }

            generator.generateNormals(reader.duplicate(), faces);
        }
        return reader;
    }

    /**
     * Closes the file.
     * @throws IOException if closing fails. 
     */
    @Override
    public void close() throws IOException {
        plyReader.close();
    }
}
