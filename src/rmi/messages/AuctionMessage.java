/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.messages;

import enums.AuctionType;
import java.io.Serializable;
import music.elements.Chord;

/**
 *
 * @author Martin
 */
public class AuctionMessage implements Serializable {

    public AuctionType auctionType;

    //Chord type
    public Chord chord;
    public int chordOriginID;

    //Feature type
    public String featureName;
    public Double feature;

    public AuctionMessage(AuctionType auctionType) {
        this.auctionType = auctionType;
    }

}
