/* SWEN30006 Software Modelling and Design 
 * Project 1 - Mailroom Blues
 * Author: William Pan 694945
 */
package com.unimelb.swen30006.mailroom.strategies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import com.unimelb.swen30006.mailroom.DeliveryStrategy;
import com.unimelb.swen30006.mailroom.MailItem;
import com.unimelb.swen30006.mailroom.StorageBox;
import com.unimelb.swen30006.mailroom.exceptions.BoxEmptyException;
import com.unimelb.swen30006.mailroom.exceptions.SourceExhaustedException;

/**
 * Sorting strategy that returns the closest min/max floor to the current floor
 * Attempts to minimize elevator traversals by going from top to bottom or bottom to top
 */
public class DeliveryStrategyOne implements DeliveryStrategy {
	
	@Override
	public int chooseNextFloor(int currentFloor, StorageBox box) throws SourceExhaustedException {
		// Exit if box starts empty
		if (box.isEmpty()) {
			return 0;
		}
		// Create sorted arrayList of all unique floors
		ArrayList<Integer> aL = new ArrayList<Integer>();
		chooseNextFloorRecurse(box,aL);
		
		// Return closest min/max floor to currentFloor
		int max = Collections.max(aL);
		int min = Collections.min(aL);
		if (currentFloor - min <= max - currentFloor){
			return min;
		}
		return max;
	}
	
	/**
	 * A recursive function to add all unique floors to ArrayList
	 * @param box StorageBox to deliver
	 * @param aL ArrayList to add unique floors to
	 */
	public void chooseNextFloorRecurse(StorageBox box, ArrayList<Integer> aL){
		try{
			// Return when at end of recursion (stack empty)
			if (box.isEmpty()) {
				return;
			}
			// Peek at floor, add to ArrayList if no already there
			MailItem item = box.popItem();
			if (!aL.contains(item.floor))
				aL.add(item.floor);
			chooseNextFloorRecurse(box,aL);
			box.addItem(item);
		}
		// Handle Exceptions
		catch(Exception e){
			System.out.println(e);
			System.exit(0);
		}
		return;
	}
}
