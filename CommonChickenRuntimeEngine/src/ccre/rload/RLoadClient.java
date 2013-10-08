/*
 * Copyright 2013 Colby Skeggs
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
package ccre.rload;

import ccre.net.ClientSocket;
import ccre.net.Network;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * A client for the RLoad system primarily used by Obsidian projects.
 *
 * @author skeggsc
 */
public class RLoadClient {

    /**
     * The main launching function for an RLoad server.
     *
     * @param args
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Requires exactly two arguments - the file to upload and remote address to upload to.");
            System.exit(1);
            return;
        }
        upload(new File(args[0]), args[1]);
    }

    public static void upload(File target, String remote) throws IOException {
        long len = target.length();
        if (len < 0 || len >= 1024 * 1024) {
            throw new IOException("Data length out of range! Maximum 1 MB.");
        }
        ClientSocket server = Network.connect(remote, 11540);
        try {
            DataInputStream din = server.openDataInputStream();
            DataOutputStream dout = server.openDataOutputStream();
            dout.writeLong(RLoadServer.MAGIC_HEADER);
            if (din.readLong() != ~RLoadServer.MAGIC_HEADER) {
                throw new IOException("Invalid magic number!");
            }
            byte[] data = new byte[(int)len];
            DataInputStream fin = new DataInputStream(new FileInputStream(target));
            try {
                fin.readFully(data);
                if (fin.read() != -1) {
                    throw new IOException("Did not reach EOF at appropriate time.");
                }
            } finally {
                fin.close();
            }
            dout.writeInt((int)len);
            dout.write(data);
            dout.writeInt(RLoadServer.checksum(data));
            if (din.readInt() != (int) ((RLoadServer.MAGIC_HEADER >> 32) ^ RLoadServer.MAGIC_HEADER)) {
                throw new IOException("Failed to transmit!");
            }
        } finally {
            server.close();
        }
    }
}