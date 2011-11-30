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

import java.util.Map;

/**
 * Element of a PLY file.
 * <p>Each element has a number of values that describe its properties. The
 * type of an element describes which properties are present in an element.</p>
 */
public final class Element implements Cloneable {

    /** Values of this element. The first index selects the property. */
    private final double[][] values;
    /** Type of this element. */
    private final ElementType type;
    /** Maps from property name to the index in {@code values}. */
    private final Map<String, Integer> propertyMap;

    /**
     * Creates an instance.
     * @param values Array containing an array of values for each property.
     * @param type Element type of the element.
     * @param propertyMap Maps property names to indicies in {@code values}.
     */
    Element(final double[][] values, final ElementType type,
            final Map<String, Integer> propertyMap) {
        if (values == null || type == null || propertyMap == null) {
            throw new NullPointerException();
        }
        this.values = values;
        this.type = type;
        this.propertyMap = propertyMap;
    }

    /**
     * Gets the type of this element.
     * @return Element type. Is never {@code null}.
     */
    public ElementType getType() {
        return type;
    }

    /**
     * Gets a property value of this element.
     * <p>If the property is a list-property, the first element is returned.</p>
     * @param propertyName Name of the property.
     * @return Value of the current element casted to {@code int}.
     * @throws NullPointerException if {@code propertyName} is {@code null}.
     * @throws IllegalArgumentException if the element type does not have
     * a property with the given name.
     * @throws IndexOutOfBoundsException if the requsted property is a
     * list property with zero elements.
     */
    public int getInt(final String propertyName) {
        if (propertyName == null) {
            throw new NullPointerException("propertyName must not be null.");
        }
        Integer index = propertyMap.get(propertyName);
        if (index == null) {
            throw new IllegalArgumentException("non existent property: '"
                    + propertyName + "'.");
        }
        if (values[index].length == 0) {
            throw new IndexOutOfBoundsException(
                    "The property value is a list with zero entries.");
        }
        return (int) values[index][0];
    }

    /**
     * Gets a property value of this element.
     * <p>If the property is a list-property, the first element is returned.</p>
     * @param propertyName Name of the property.
     * @return Value of the current element casted to {@code double}.
     * @throws NullPointerException if {@code propertyName} is {@code null}.
     * @throws IllegalArgumentException if the element type does not have
     * a property with the given name.
     * @throws IndexOutOfBoundsException if the requsted property is a
     * list property with zero elements.
     */
    public double getDouble(final String propertyName) {
        if (propertyName == null) {
            throw new NullPointerException("propertyName must not be null.");
        }
        Integer index = propertyMap.get(propertyName);
        if (index == null) {
            throw new IllegalArgumentException("non existent property: '"
                    + propertyName + "'.");
        }
        if (values[index].length == 0) {
            throw new IndexOutOfBoundsException(
                    "The property value is a list with zero entries.");
        }
        return (double) values[index][0];
    }

    /**
     * Gets a property value list of this element.
     * <p>If the property is not list-property, a list with a single
     * element is returned.</p>
     * @param propertyName Name of the property.
     * @return Values of the current element, each casted to {@code int}.
     * @throws NullPointerException if {@code propertyName} is {@code null}.
     * @throws IllegalArgumentException if the element type does not have
     * a property with the given name.
     */
    public int[] getIntList(final String propertyName) {
        if (propertyName == null) {
            throw new NullPointerException("propertyName must not be null.");
        }
        Integer index = propertyMap.get(propertyName);
        if (index == null) {
            throw new IllegalArgumentException("non existent property: '"
                    + propertyName + "'.");
        }
        int[] v = new int[values[index].length];
        for (int i = 0; i < v.length; i++) {
            v[i] = (int) values[index][i];
        }
        return v;
    }

    /**
     * Gets a property value list of this element.
     * <p>If the property is not list-property, a list with a single
     * element is returned.</p>
     * @param propertyName Name of the property.
     * @return Values of the current element, each casted to {@code double}.
     * @throws NullPointerException if {@code propertyName} is {@code null}.
     * @throws IllegalArgumentException if the element type does not have
     * a property with the given name.
     */
    public double[] getDoubleList(final String propertyName) {
        if (propertyName == null) {
            throw new NullPointerException("propertyName must not be null.");
        }
        Integer index = propertyMap.get(propertyName);
        if (index == null) {
            throw new IllegalArgumentException("non existent property: '"
                    + propertyName + "'.");
        }
        return values[index].clone();
    }

    /**
     * Sets the value of a property-list.
     * If the property is not a list, the first element is used.
     * @param values Values to set for that property.
     * @throws NullPointerException if {@code propertyName} is {@code null}.
     * @throws IllegalArgumentException if the element type does not have
     * a property with the given name.
     * @throws IndexOutOfBoundsException if the property is not a list property
     * and the given array does not have exactly one element.
     */
    public void setDoubleList(final String propertyName, double[] values) {
        if (propertyName == null) {
            throw new NullPointerException("propertyName must not be null.");
        }
        Integer index = propertyMap.get(propertyName);
        if (index == null) {
            throw new IllegalArgumentException("non existent property: '"
                    + propertyName + "'.");
        }
        if (type.getProperties().get(index) instanceof ListProperty) {
            this.values[index] = values.clone();
        } else {
            if (values.length != 0) {
                throw new IndexOutOfBoundsException("property is not a list");
            }
            this.values[index][0] = values[0];
        }
    }

    /**
     * Sets the value of a property-list.
     * If the property is a list, the list will be set to a single entry.
     * @param values Values to set for that property.
     * @throws NullPointerException if {@code propertyName} is {@code null}.
     * @throws IllegalArgumentException if the element type does not have
     * a property with the given name.
     */
    public void setDouble(final String propertyName, double value) {
        if (propertyName == null) {
            throw new NullPointerException("propertyName must not be null.");
        }
        Integer index = propertyMap.get(propertyName);
        if (index == null) {
            throw new IllegalArgumentException("non existent property: '"
                    + propertyName + "'.");
        }
        this.values[index] = new double[]{value};
    }

    /**
     * Sets the value of a property-list.
     * If the property is not a list, the first element is used.
     * @param values Values to set for that property.
     * @throws NullPointerException if {@code propertyName} is {@code null}.
     * @throws IllegalArgumentException if the element type does not have
     * a property with the given name.
     * @throws IndexOutOfBoundsException if the property is not a list property
     * and the given array does not have exactly one element.
     */
    public void setIntList(final String propertyName, int[] values) {
        if (propertyName == null) {
            throw new NullPointerException("propertyName must not be null.");
        }
        Integer index = propertyMap.get(propertyName);
        if (index == null) {
            throw new IllegalArgumentException("non existent property: '"
                    + propertyName + "'.");
        }
        if (type.getProperties().get(index) instanceof ListProperty) {
            this.values[index] = new double[values.length];
            for (int i = 0; i < values.length; i++) {
                this.values[index][i] = values[i];
            }
        } else {
            if (values.length != 0) {
                throw new IndexOutOfBoundsException("property is not a list");
            }
            this.values[index][0] = values[0];
        }
    }

    /**
     * Sets the value of a property-list.
     * If the property is a list, the list will be set to a single entry.
     * @param values Values to set for that property.
     * @throws NullPointerException if {@code propertyName} is {@code null}.
     * @throws IllegalArgumentException if the element type does not have
     * a property with the given name.
     */
    public void setInt(final String propertyName, int value) {
        if (propertyName == null) {
            throw new NullPointerException("propertyName must not be null.");
        }
        Integer index = propertyMap.get(propertyName);
        if (index == null) {
            throw new IllegalArgumentException("non existent property: '"
                    + propertyName + "'.");
        }
        this.values[index] = new double[]{value};
    }

    @Override
    public Element clone(){
        double[][] clone = new double[values.length][];
        for (int i = 0; i < clone.length; i++) {
            clone[i] = values[i].clone();
        }
        return new Element(clone, type, propertyMap);
    }
}
