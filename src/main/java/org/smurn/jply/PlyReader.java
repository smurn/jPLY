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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Reads meshes in the PLY file format.
 */
public class PlyReader {

    private List<ElementType> elements;
    private Format format;
    private InputStream binaryStream;
    private BufferedReader asciiReader;
    private int nextElement = 0;
    private ElementReader currentReader;

    /**
     * Opens a PLY file.
     * <p>If the file name ends with {@code .gz} the file will be decompressed
     * automatically.</p>
     * @param file File to open.
     * @throws NullPointerException if {@code file} is {@code null}.
     * @throws IOException if an error occurs during opening or reading.
     */
    public PlyReader(final File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("file must not be null.");
        }
        InputStream inputStream;
        if (file.getName().endsWith(".gz")) {
            inputStream = new GZIPInputStream(new FileInputStream(file));
        } else {
            inputStream = new FileInputStream(file);
        }
        readHeader(inputStream);
    }

    /**
     * Opens a PLY file.
     * @param file Path to the file to open.
     * @throws NullPointerException if {@code file} is {@code null}.
     * @throws IOException if an error occurs during opening or reading.
     */
    public PlyReader(final String file) throws IOException {
        this(new File(file));
    }

    /**
     * Opens a PLY file.
     * @param stream InputStream from which to read the file.
     * @throws NullPointerException if {@code stream} is {@code null}.
     * @throws IOException if an error occurs during opening or reading.
     */
    public PlyReader(final InputStream stream) throws IOException {
        if (stream == null) {
            throw new NullPointerException("stream must not be null.");
        }
        readHeader(stream);
    }

    /**
     * Reads the header and prepares everything to read the file's content.
     * @param stream Stream to read the file from.
     * @throws IOException if an error occurs during reading.
     */
    private void readHeader(final InputStream stream) throws IOException {
        UnbufferedASCIIReader hdrReader = new UnbufferedASCIIReader(stream);
        String magic = hdrReader.readLine();
        if (!"ply".equals(magic)) {
            throw new IOException("Invalid PLY file: does not start "
                    + "with 'ply'.");
        }

        format = null;
        ElementType currentElement = null;
        List<Property> currentElementProperties = null;
        elements = new ArrayList<ElementType>();

        for (String line = hdrReader.readLine(); true;
                line = hdrReader.readLine()) {
            if (line == null) {
                throw new IOException("Unexpected end of file while "
                        + "reading the header.");
            }
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.startsWith("format ")) {
                if (format != null) {
                    throw new IOException("Multiple format definitions.");
                }
                format = Format.parse(line);
            } else if (line.startsWith("element ")) {
                if (currentElement != null) {
                    // finish the last element
                    ElementType element = new ElementType(
                            currentElement.getName(),
                            currentElement.getCount(),
                            currentElementProperties);
                    elements.add(element);
                    currentElement = null;
                    currentElementProperties = null;
                }
                currentElement = ElementType.parse(line);
                currentElementProperties = new ArrayList<Property>();
            } else if (line.startsWith("property ")) {
                if (currentElement == null) {
                    throw new IOException("Property without element found.");
                }
                Property property = Property.parse(line);
                currentElementProperties.add(property);
            } else if (line.startsWith("end_header")) {
                break;
            } else if (!line.startsWith("comment ")) {
                throw new IOException("Unsupported header entry: " + line);
            }
        }

        if (currentElement != null) {
            // finish the last element
            ElementType element = new ElementType(currentElement.getName(),
                    currentElement.getCount(),
                    currentElementProperties);
            elements.add(element);
            currentElement = null;
            currentElementProperties = null;
        }

        elements = Collections.unmodifiableList(elements);

        if (format == Format.ASCII) {
            this.asciiReader = new BufferedReader(
                    new InputStreamReader(stream, Charset.forName("US-ASCII")));
            this.binaryStream = null;
        } else {
            this.asciiReader = null;
            this.binaryStream = stream;
        }

    }

    /**
     * Gets all element types in this PLY file.
     * <p>The order of the list
     * is the same in which the corresponding readers are returned
     * by {@link #nextElementReader()}.</p>
     * @return Immutable list with all element types.
     */
    public List<ElementType> getElementTypes() {
        return elements;
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
    public ElementReader nextElementReader() throws IOException {
        if (currentReader != null) {
            // TODO: throw exception if user did not properly close instead.
            currentReader.close();
        }
        currentReader = nextElementReaderInternal();
        return currentReader;
    }

    /**
     * Creates the next element reader.
     * @return next element Reader.
     */
    private ElementReader nextElementReaderInternal() {
        if (nextElement >= elements.size()) {
            return null;
        }
        try {
            switch (format) {
                case ASCII:
                    return new AsciiElementReader(
                            elements.get(nextElement),
                            asciiReader);
                default:
                    throw new UnsupportedOperationException("PLY format "
                            + format + " is currently not supported.");
            }
        }
        finally {
            nextElement++;
        }
    }
}
