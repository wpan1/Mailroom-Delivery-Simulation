/* SWEN30006 Software Modelling and Design 
 * Project 1 - Mailroom Blues
 * Author: William Pan 694945
 */

package com.unimelb.swen30006.mailroom.strategies;

import java.util.UUID;

import com.unimelb.swen30006.mailroom.MailItem;
import com.unimelb.swen30006.mailroom.MailStorage;
import com.unimelb.swen30006.mailroom.Simulation;
import com.unimelb.swen30006.mailroom.SortingStrategy;
import com.unimelb.swen30006.mailroom.StorageBox;
import com.unimelb.swen30006.mailroom.StorageBox.Summary;
import com.unimelb.swen30006.mailroom.exceptions.DuplicateIdentifierException;
import com.unimelb.swen30006.mailroom.exceptions.MailOverflowException;

/**
 * A simple sorting strategy that attempts to place box in it's 'perfect' box
 * 'perfect' box is the number of floors divided equally in number of boxes
 */
public class SortingStrategyOne implements SortingStrategy{
	// Count variable, for identifier ID
	private int count = 0;
	// Simulation variables
	private int MAX_BOXES,MAX_FLOOR,MIN_FLOOR;
	
	// Constructor for needed Simulation variables
	public SortingStrategyOne(int MAX_BOXES, int MAX_FLOOR, int MIN_FLOOR){
		this.MAX_BOXES = MAX_BOXES;
		this.MAX_FLOOR = MAX_FLOOR;
		this.MIN_FLOOR = MIN_FLOOR;
	}
	
	@Override
	public String assignStorage(MailItem item, MailStorage storage) throws MailOverflowException {
        StorageBox.Summary[] available = storage.retrieveSummaries();
        // Calculate 'perfect' box number
        int perfectBox = (int)((item.floor*MAX_BOXES)/(MAX_FLOOR+Math.abs(MIN_FLOOR)));
        // If mailItem fits, place in box
        if (available.length >= perfectBox+1 && available[perfectBox].remainingUnits >= item.size){
        	return available[perfectBox].identifier;
        }
        // Otherwise, create a new box. This always works, as selection sends a box for delivery
        // when all boxes are being used
        return genIdentifier(storage,count);
	}

	/**
	 * Generate the identifier according to number of boxes created
	 */
	public String genIdentifier(MailStorage storage, int id) throws MailOverflowException {
		count += 1;
		try {
			storage.createBox(String.valueOf(id));
			return String.valueOf(id);
		} catch (DuplicateIdentifierException e) {
			System.out.println(e);
			System.exit(0);
		}
		return null;
	}
	
	/**
	 * Reset instance variables
	 */
	public void reset() {
		count = 0;
	}
}
