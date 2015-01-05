package java.lang;

import java.io.Serializable;

/**
 * A partial reimplementation of Enums for the cRIO. This is incomplete and
 * probably broken. It will be fixed up later. See the official Enum
 * documentation for more info.
 * 
 * @author skeggsc
 * @param <E> the type of the Enum.
 */
@SuppressWarnings({ "javadoc", "serial" })
public class Enum<E extends Enum<E>> implements Serializable, Comparable<E> {
    private final int ordinal;
    private final String name;

    protected Enum(String name, int ordinal) {
        this.name = name;
        this.ordinal = ordinal;
    }

    public final String name() {
        return name;
    }

    public final int ordinal() {
        return ordinal;
    }

    public String toString() {
        return name;
    }

    public final boolean equals(Object other) {
        return other == this;
    }

    public final int hashCode() {
        return ordinal;
    }

    // protected final Object clone() throws CloneNotSupportedException

    public final int compareTo(E o) {
        return ordinal - ((Enum<E>) o).ordinal;
    }

    // TODO: Implement getDeclaringClass
    // public final Class<E> getDeclaringClass()

    // TODO: Implement valueOf
    // Note: should be Class<T>
    public static <T extends Enum<T>> T valueOf(Class enumType, String name) {
        throw new Error("Enum.valueOf not yet implemented on the cRIO.");
    }

    protected final void finalize() {
        // Do absolutely nothing.
    }
}