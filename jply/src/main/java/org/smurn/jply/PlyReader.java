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

import java.io.IOException;
import java.util.List;

/**
 * Interface for streams that provide PLY data.
 * <h2>Why sequential and random-access methods are put into the same
 * interface</h2>
 * <p>It might seem natural to separate the random-access methods into separate
 * interfaces. A {@code SeekablePlyReader} interface that extends from the
 * sequential interface {@code PlyReader}. While this works nicely in some
 * use-cases it causes problems for wrapper classes:</p>
 * <ul>
 * <li>A {@code PlyReader} wrapper might be used to wrap a
 * {@code SeekablePlyReader}. The wrapper might assume that 
 * {@link #readElement()} returns the elements in the order they are stored in
 * the file. But since the seek methods can be called on the wrapped stream
 * directly this assumption can fail. With the combined interface this cannot
 * happen since every wrapper is aware that there exist seekable streams.</li>
 * <li>Wrappers might work on both random-access and sequential streams.
 * Often such wrappers can provide random-access themselves if the underlying
 * stream offers random-access as well. Such a wrapper would need to implement
 * either the sequential or the random-access interface depending on the
 * underlying stream. This basically requires two separate implementations
 * of the wrapper. With the combined interface such wrappers behave
 * as expected.</li>
 * </ul>
 */
public interface PlyReader {

    /**
     * Gets all element groups in this PLY file.
     * @return Immutable list of element groups in the same order as they will
     * be returned by {@link #getNextElementGroup()}.
     */
    List<ElementGroup> getElementGroups();

    /**
     * Positions the stream at the first element of the next group.
     * <p>Each group has a different name.</p>
     * @return The group to which the element belongs that will be read
     * next or {@code null} if the end of the stream has been reached.
     * @throws IOException if reading fails.
     */
    ElementGroup readElementGroup() throws IOException;

    /**
     * Reads the next element of the current group.
     * <p>This methods reads only the elements of the current group.
     * If there are no more elements {@code null} is returned. At this point
     * {@link #readElementGroup()} needs to be called to continue reading.
     * After that this method will start reading the elements of the next
     * group.</p>
     * @return The next element of the current group or {@code null} if
     * there are no more elements in the current group.
     * @throws IOException  if reading fails.
     */
    Element readElement() throws IOException;

    /**
     * Checks if this stream supports random-access.
     * @return {@code true} if the steam supports random-access. {@code false}
     * if only sequential access is supported.
     */
    boolean isSeakable();

    /**
     * Positions the stream at the first element of the given group.
     * @param groupName Group at which to position the stream.
     * @throws NoSuchElementException if there is no such group in
     * this stream.
     * @throws NullPointerException if {@code group} is {@code null}.
     * @throws UnsupportedOperationException if this stream does not
     * support random-access. See {@link #isSeakable()}.
     */
    void seekElementGroup(String groupName) throws IOException;

    /**
     * Positions the stream such that the element with the given index
     * will be read next.
     * @param Index of the next element to read.
     * @throws IndexOutOfBoundsException if {@code index} is negative or
     * greater-equal than the number of elements in the current group.
     * @throws IOException if reading fails.
     * @throws UnsupportedOperationException if this stream does not
     * support random-access. See {@link #isSeakable()}.
     */
    void seekElement(int index) throws IOException;

    /**
     * Returns the element at the given index.
     * <p>The next element that will be returned by {@link #readElement()} is
     * not affected.</p>
     * @param index Index of the requested element.
     * @return Element with the given index.
     * @throws IndexOutOfBoundsException if {@code index} is negative or
     * greater-equal than the number of elements in the current group.
     * @throws IOException if reading fails.
     * @throws UnsupportedOperationException if this stream does not
     * support random-access. See {@link #isSeakable()}.
     */
    Element getElement(int index) throws IOException;

    /**
     * Returns the element at the given index.
     * <p>The next element that will be returned by {@link #readElement()} is
     * not affected.</p>
     * @param groupName Group of the requested element.
     * @param index Index of the requested element.
     * @return Element with the given index.
     * @throws IndexOutOfBoundsException if {@code index} is negative or
     * greater-equal than the number of elements in the current group.
     * @throws NoSuchElementException if there is no such group in
     * this stream.
     * @throws NullPointerException if {@code group} is {@code null}.
     * @throws IOException if reading fails.
     * @throws UnsupportedOperationException if this stream does not
     * support random-access. See {@link #isSeakable()}.
     */
    Element getElement(String groupName, int index) throws IOException;

    /**
     * Closes the stream.
     * @throws IOException if closing fails. 
     */
    void close() throws IOException;
}
