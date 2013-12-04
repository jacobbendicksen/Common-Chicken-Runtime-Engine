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
package ccre.cluck.any;

import ccre.cluck.CluckLink;
import ccre.cluck.CluckNode;

/**
 * A CluckNullLink is a link between two CluckNodes on the same computer, and an
 * example of how to write a link that connects two CluckNodes.
 *
 * Usage example:<br>
 * <code>
 * CluckNode alpha = new CluckNode();<br>
 * CluckNode beta = new CluckNode();<br>
 * CluckNullLink alphaLink = new CluckNullLink(alpha, "alpha-to-beta");<br>
 * CluckNullLink betaLink = new CluckNullLink(beta, "beta-to-alpha",
 * alphaLink);<br>
 * <br>
 * EventConsumer test = new EventLogger(LogLevel.INFO, "Pseudo-networked
 * test!");<br>
 * alpha.publish("test", test);<br>
 * EventConsumer test2 = beta.subscribeEC("beta-to-alpha/test");<br>
 * test2.eventFired();<br>
 * </code><br>
 * This will log "Pseudo-networked test!" at LogLevel INFO.
 *
 * @author skeggsc
 */
public class CluckNullLink implements CluckLink {

    /**
     * The other end of this CluckNullLink.
     */
    protected CluckNullLink paired;
    /**
     * The CluckNode attached to this end of the link.
     */
    protected final CluckNode node;
    /**
     * The link name of this link.
     */
    public String linkName;

    /**
     * Create a new link attached to the specified CluckNode.
     *
     * @param node The node to attach to.
     */
    public CluckNullLink(CluckNode node) {
        this.node = node;
        // Will expect other null link to be created.
    }

    /**
     * Create a new link attached to the specified CluckNode and paired with the
     * specified other link.
     *
     * @param node The node to attach to.
     * @param other The link to pair with.
     */
    public CluckNullLink(CluckNode node, CluckNullLink other) {
        paired = other;
        if (other.paired != null) {
            throw new IllegalStateException("Other link is already attached!");
        }
        this.node = node;
        other.paired = this;
    }

    /**
     * Create a new link attached to the specified CluckNode, and add the link
     * to the CluckNode under the specified name.
     *
     * @param node The node to attach to.
     * @param linkName The link name to use.
     */
    public CluckNullLink(CluckNode node, String linkName) {
        this.node = node;
        this.linkName = linkName;
        node.addLink(this, linkName);
        // Will expect other null link to be created.
    }

    /**
     * Create a new link attached to the specified CluckNode and paired with the
     * specified other link, and add the link to the CluckNode under the
     * specified name.
     *
     * @param node The node to attach to.
     * @param linkName The link name to use.
     * @param other The link to pair with.
     */
    public CluckNullLink(CluckNode node, String linkName, CluckNullLink other) {
        paired = other;
        if (other.paired != null) {
            throw new IllegalStateException("Other link is already attached!");
        }
        this.node = node;
        this.linkName = linkName;
        other.paired = this;
        node.addLink(this, linkName);
    }

    public boolean transmit(String rest, String source, byte[] data) {
        if (paired == null) {
            return true;
        }
        paired.pairTransmit(rest, source, data);
        return true;
    }

    private void pairTransmit(String rest, String source, byte[] data) {
        // Prepend link name
        if (linkName == null) {
            linkName = node.getLinkName(this);
        }
        if (source == null) {
            source = linkName;
        } else {
            source = linkName + "/" + source;
        }
        node.transmit(rest, source, data, this);
    }
}