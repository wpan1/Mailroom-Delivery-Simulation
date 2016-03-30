/* SWEN30006 Software Modelling and Design 
 * Project 1 - Mailroom Blues
 * Author: William Pan 694945
 */

package com.unimelb.swen30006.mailroom.strategies;

import com.unimelb.swen30006.mailroom.SelectionStrategy;
import com.unimelb.swen30006.mailroom.Simulation;
import com.unimelb.swen30006.mailroom.StorageBox;
import com.unimelb.swen30006.mailroom.StorageBox.Summary;
import com.unimelb.swen30006.mailroom.exceptions.NoBoxReadyException;

/**
 * Selection strategy that will wait until sorting has finished, a
 * box is full or when all boxes are being used
 *
 */
public class SelectionStrategyOne implements SelectionStrategy {
	// Counter to count until sorting has finished
	private int count = 0;
	// True if sorting has finished
	private boolean endSorting = false;
	// Simulation variables for sorting
	private int MAX_BOXES, MAX_MAIL_UNITS, NUM_MAIL, NUM_BOTS;
	
	public SelectionStrategyOne(int MAX_BOXES, int MAX_MAIL_UNITS, int NUM_MAIL, int NUM_BOTS){
		this.MAX_BOXES = MAX_BOXES;
		this.MAX_MAIL_UNITS = MAX_MAIL_UNITS;
		this.NUM_MAIL = NUM_MAIL;
		this.NUM_BOTS = NUM_BOTS;
	}
	
	@Override
	public String selectNextDelivery(Summary[] summaries) throws NoBoxReadyException {
		// Count for every iteration of bot
		count += 1;
		
		// Return if no boxes found
		if (summaries.length == 0) {
			throw new NoBoxReadyException();
		}
		
		// Check if boxes are completely full
		for (Summary summary : summaries) {
			if (summary.remainingUnits == 0) {
				// Decrement count as bot is delivering
				count -= 2*summary.numDests+1;
				return summary.identifier;
			}
		}
		
		// If all boxes are being used, return box with min remainingUnits
		if (summaries.length == MAX_BOXES){
			int minUnits = MAX_MAIL_UNITS+1;
			Summary tempSummary = null;
			for (int i = 0; i < summaries.length; i++){
				if (summaries[i].remainingUnits < minUnits) {
					tempSummary = summaries[i];
					minUnits = summaries[i].remainingUnits;
				}
			}
			// Decrement count as bot is delivering
			count -= 2*tempSummary.numDests+1;
			return tempSummary.identifier;
		}
		
		// If sorting has finished, change boolean endSorting value
		if ((count-1) == NUM_MAIL*NUM_BOTS){
			endSorting = true;
		}
		
		// If sorting has finished, select any box for delivery
		if (endSorting) {
			for (Summary summary : summaries) {
				if (summary.numItems > 0) {
					return summary.identifier;
				}
			}
		}
		
		// If it gets to here, no boxes are ready for selection
		throw new NoBoxReadyException();
	}
	
	public void reset() {
		count = 0;
		endSorting = false;
	}
}