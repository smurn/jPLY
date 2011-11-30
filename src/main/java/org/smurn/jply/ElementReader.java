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

/**
 * Reads a stream of elements.
 * <p>The {@code get} methods are returning the property values of
 * the current element. The current element is changed to the next by
 * {@link #next()}. Initially {@code next()} must be called to select
 * the first element.</p>
 */
public interface ElementReader {

    /**
     * The element type of all elements read by this stream.
     * @return Element type of all elements. Never {@code null}.
     */
    public ElementType getElementType();

    /**
     * Total number of elements provided by this stream.
     * <p>This is equivalent to {@code getElementType().getCount()}.</p>
     * @return Number of elements.
     */
    public int getCount();

    /**
     * Reads the next element.
     * @return The next element or {@code null} if there are no more
     * elements.
     * @throws IOException if reading fails.
     * @throws IllegalStateException if the stream is closed.
     */
    public Element readElement() throws IOException;

    /**
     * Closes this stream.
     * @throws IOException  if closing the stream fails.
     */
    public void close() throws IOException;
}
