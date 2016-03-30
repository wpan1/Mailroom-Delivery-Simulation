/* SWEN30006 Software Modelling and Design 
 * Project 1 - Mailroom Blues
 * Author: William Pan 694945
 */

package com.unimelb.swen30006.mailroom.strategies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.unimelb.swen30006.mailroom.MailItem;
import com.unimelb.swen30006.mailroom.MailStorage;
import com.unimelb.swen30006.mailroom.Simulation;
import com.unimelb.swen30006.mailroom.SortingStrategy;
import com.unimelb.swen30006.mailroom.StorageBox;
import com.unimelb.swen30006.mailroom.StorageBox.Summary;
import com.unimelb.swen30006.mailroom.exceptions.DuplicateIdentifierException;
import com.unimelb.swen30006.mailroom.exceptions.MailOverflowException;

public class SortingStrategyTwo implements SortingStrategy{
	
	ArrayList<DetailedSummary> detailedSummaryList = new ArrayList<DetailedSummary>();
	// Count for number of created boxes
	private int count = 0;
	// Simulation variables for sorting
	private int MAX_BOXES, MAX_MAIL_UNITS;
	// Buffer size used for maxFloor
	private static final int BUFFER_SIZE = 25;
	
	// Class containing additional information
	public class DetailedSummary{
		private String identifier;
		private int remainingUnits;
		private int maxFloor;
		// Constructor
		public DetailedSummary(String identifier, int remainingUnits, int maxFloor){
			this.identifier = identifier;
			this.remainingUnits = remainingUnits;
			this.maxFloor = maxFloor;
		}
	}
	
	/**
	 * Constructor including Simulation Values
	 */
	public SortingStrategyTwo(int MAX_BOXES, int MAX_MAIL_UNITS){
		this.MAX_BOXES = MAX_BOXES;
		this.MAX_MAIL_UNITS = MAX_MAIL_UNITS;
	}
	
	@Override
	public String assignStorage(MailItem item, MailStorage storage) throws MailOverflowException {
		StorageBox.Summary[] summary = storage.retrieveSummaries();	
		for (DetailedSummary summaryItem : detailedSummaryList){
			// Find first box that item.floor is less than maxFloor in summmary
			if (item.size <= summaryItem.remainingUnits && item.floor <= summaryItem.maxFloor+BUFFER_SIZE){
				return updateBox(summaryItem,item);
			}
		}
		// If no box available, create a new box. This always works, as selection sends a box for delivery
        // when all boxes are being used.
		return createBox(storage,summary,count,MAX_MAIL_UNITS-item.size,item.floor);
	}
	
	/**
	 *  Update detailed summary as well as returning identifier
	 */
	public String updateBox(DetailedSummary summaryItem, MailItem item){
		// Calculate remainingUnits
		int remainingUnits = summaryItem.remainingUnits - item.size;
		// If box is full, selection will deliver this box, thus needs to removed from
		// detailedSummary
		if (remainingUnits == 0){
			detailedSummaryList.remove(summaryItem);
			return String.valueOf(summaryItem.identifier);
		}
		// Otherwise, update detailedSummary and return identifier
		int maxFloor = Math.min(item.floor, summaryItem.maxFloor);
		int index = detailedSummaryList.indexOf(summaryItem);
		detailedSummaryList.set(index, new DetailedSummary(summaryItem.identifier,remainingUnits, maxFloor));
		return String.valueOf(summaryItem.identifier);
	}
	
	/**
	 * Creates a new box, updating detailedSummary
	 */
	public String createBox(MailStorage storage,StorageBox.Summary[] summary, int id, int remainingUnits, int maxFloor) throws MailOverflowException{
		// Increment number of new boxes
		count += 1;
		detailedSummaryList.add(new DetailedSummary(String.valueOf(id), remainingUnits, maxFloor));
		// One all boxes are filled, selection will remove the box with lowest remainingUnits, thus
		// detailedSummary also needs to be update
		if (detailedSummaryList.size() == MAX_BOXES) {
			int minSize = MAX_MAIL_UNITS + 1;
			Summary removeSummary = null;
			// Find min remainingUnits
			for (Summary summaryItem : summary) {
				if (summaryItem.remainingUnits < minSize) {
					removeSummary = summaryItem;
					minSize = summaryItem.remainingUnits;
				}
			}
			// Find summary in detailedSummaryList, and remove it
			DetailedSummary removeDetSummary;
			for (DetailedSummary detSummaryItem : detailedSummaryList){
				if (detSummaryItem.identifier.equals(removeSummary.identifier)) {
					detailedSummaryList.remove(detSummaryItem);
					break;
				}
			}
		}
		// Generate the new identifier
		return genIdentifier(storage,id);
	}
	
	/**
	 * Generate a new identifier with respect to id
	 */
	public String genIdentifier(MailStorage storage, int id) throws MailOverflowException {
		// If we get to here without returning there is no storage box
		// appropriate so let's try create one
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
	 * Reset instance variables for every Simulation loop
	 */
	public void reset() {
		detailedSummaryList = new ArrayList<DetailedSummary>();
		count = 0;
	}
}
