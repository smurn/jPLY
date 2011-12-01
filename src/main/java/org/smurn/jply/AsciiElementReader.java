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
package org.smurn.jply;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Reads elements from a PLY file in ASCII format.
 */
class AsciiElementReader implements ElementReader {

    /** Type of the elements we read. */
    private final ElementType type;

    /** Source to read from. */
    private final BufferedReader reader;

    /** Index of the next row to read. */
    private int nextRow = 0;

    /** Flag indicating if the user closed this reader. */
    private boolean closed = false;

    /**
     * Creates an instance.
     * @param type Type of the elements we read.
     * @param reader Source to read the elements from.
     */
    AsciiElementReader(final ElementType type, final BufferedReader reader) {
        if (type == null) {
            throw new NullPointerException("definition must not be null.");
        }
        if (reader == null) {
            throw new NullPointerException("reader must not be null.");
        }
        this.type = type;
        this.reader = reader;
    }

    @Override
    public ElementType getElementType() {
        return type;
    }

    @Override
    public Element readElement() throws IOException {
        if (closed) {
            throw new IllegalStateException("Reader is closed.");
        }
        if (nextRow >= type.getCount()) {
            return null;
        }

        String line = reader.readLine();
        if (line == null) {
            throw new IOException("Unexpected end of file.");
        }
        String[] parts = line.split(" +");
        double[] numbers = new double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            numbers[i] = Double.parseDouble(parts[i]);
        }

        // distribute the values to the properties
        int pos = 0;
        List<Property> properties = type.getProperties();
        double[][] values = new double[properties.size()][];
        for (int propI = 0; propI < properties.size(); propI++) {
            Property property = properties.get(propI);
            if (property instanceof ListProperty) {
                int count = (int) Math.round(numbers[pos++]);
                values[propI] = Arrays.copyOfRange(numbers, pos, pos + count);
                pos += count;
            } else {
                values[propI] = new double[]{numbers[pos++]};
            }
        }

        nextRow++;
        return new Element(values, type);
    }

    @Override
    public int getCount() {
        return getElementType().getCount();
    }

    @Override
    public void close() throws IOException {
        // Consume the remaining elements so that we
        // are at the right position for the next group of elements.
        while (nextRow < type.getCount()) {
            String line = reader.readLine();
            if (line == null) {
                throw new IOException("Unexpected end of file.");
            }
        }
        closed = true;
    }
}
