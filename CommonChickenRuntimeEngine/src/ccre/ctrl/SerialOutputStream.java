/*
 * Copyright 2015 Colby Skeggs
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
package ccre.ctrl;

import java.io.IOException;
import java.io.OutputStream;

import ccre.channel.SerialOutput;

/**
 * Wraps a SerialOutput in an OutputStream so that it can be used with
 * infrastructure designed for OutputStreams.
 *
 * @author skeggsc
 */
public class SerialOutputStream extends OutputStream {
    private final SerialOutput output;

    /**
     * Create a new SerialOutputStream wrapping the output.
     *
     * @param output the output to wrap.
     */
    public SerialOutputStream(SerialOutput output) {
        this.output = output;
    }

    @Override
    public void write(int b) throws IOException {
        output.writeFully(new byte[] { (byte) b }, 0, 1);
    }

    public void write(byte b[], int off, int len) throws IOException {
        output.writeFully(b, off, off + len);
    }

    public void flush() throws IOException {
        output.flush();
    }

    public void close() throws IOException {
        output.close();
    }
}
