/*
 * The MIT License
 *
 * Copyright 2015 Martin Minovski <martin at minovski.net>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package rmi.agents.monitor;

import java.io.Serializable;

/**
 * Dummy link class used by Registry to describe topology to monitors
 *
 * @author Martin Minovski <martin at minovski.net>
 */
public class AgentDummyLink implements Serializable {

    AgentLinkType linkType;
    int ID;
    int fromAgentID;
    int toAgentID;

    //Not used yet, just ideas
    boolean isActive = true;
    int strength = 1;

    /**
     * Default constructor
     *
     * @param linkType The link type
     * @param ID Link ID
     * @param fromAgentID
     * @param toAgentID
     */
    public AgentDummyLink(AgentLinkType linkType, int ID, int fromAgentID, int toAgentID) {
        this.linkType = linkType;
        this.ID = ID;
        this.fromAgentID = fromAgentID;
        this.toAgentID = toAgentID;
    }

    public enum AgentLinkType {

        //To implement in future

        AINeighbourLink,
        HumanToPerformerLink,
        MonitorOwnLink
    };
}
