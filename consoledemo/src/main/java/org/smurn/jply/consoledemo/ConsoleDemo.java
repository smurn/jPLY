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
package org.smurn.jply.consoledemo;

import java.io.IOException;
import org.smurn.jply.Element;
import org.smurn.jply.ElementReader;
import org.smurn.jply.ElementType;
import org.smurn.jply.PlyReader;
import org.smurn.jply.PlyReaderImpl;

/**
 * Console application that prints out the positions of all vertices.
 */
public class ConsoleDemo {

    public static void main(String[] args) throws IOException {

        PlyReader ply;

        if (args.length != 1) {
            System.err.println("--Usage: ConsoleDemo [PathToPLYFile]");
            System.err.println("--Will use a demo file of a cube instead.");

            // Reads a PLY file from an InputStream. Handy if, for example,
            // you receive PLY data via network sockets.
            
            ply = new PlyReaderImpl(
                    ClassLoader.getSystemResourceAsStream("cube.ply"));
        } else {

            // Opens the PLY file.
            // If the file name ends with .gz its automatically decompressed.

            String file = args[0];
            ply = new PlyReaderImpl(file);
        }

        // The elements are stored in order of their types. For each
        // type we get a reader that reads the elements of this type.
        ElementReader reader = ply.nextElementReader();
        while (reader != null) {

            ElementType type = reader.getElementType();

            // In PLY files vertices always have a type named "vertex".
            if (type.getName().equals("vertex")) {
                printVertices(reader);
            }

            // Close the reader for the current type before getting the next one.
            reader.close();

            reader = ply.nextElementReader();
        }

        ply.close();
    }

    private static void printVertices(ElementReader reader) throws IOException {

        // The number of elements is known in advance. This is great for
        // allocating buffers and a like.
        // You can even get the element counts for each type before getting 
        // the corresponding reader via the PlyReader.getElementCount(..)
        // method.
        System.out.println("There are " + reader.getCount() + " vertices:");

        // Read the elements. They all share the same type.
        Element element = reader.readElement();
        while (element != null) {

            // Use the the 'get' methods to access the properties.
            // jPly automatically converts the various data types supported
            // by PLY for you.

            System.out.print("x=");
            System.out.print(element.getDouble("x"));
            System.out.print(" y=");
            System.out.print(element.getDouble("y"));
            System.out.print(" z=");
            System.out.print(element.getDouble("z"));
            System.out.println();

            element = reader.readElement();
        }
    }
}
