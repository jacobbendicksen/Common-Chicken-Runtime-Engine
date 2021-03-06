/*
 * Copyright 2014 Colby Skeggs.
 *
 * This file is part of the CCRE, the Common Chicken Runtime Engine.
 *
 * The CCRE is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * The CCRE is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the CCRE.  If not, see <http://www.gnu.org/licenses/>.
 */
package java.io;

/**
 * This is a not-really-implemented implementation of
 * java.io.ObjectOutputStream. Don't use this. It is used when Retrotranslator
 * downgrades the code to 1.3, because 1.3 doesn't have ObjectOutputStream. It
 * doesn't have any functionality because serialization doesn't work like this
 * in 1.3.
 *
 * @see java.io.ObjectOutputStream
 * @author skeggsc
 */
public class ObjectOutputStream {
}
