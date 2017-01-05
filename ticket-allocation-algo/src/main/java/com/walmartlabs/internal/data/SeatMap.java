package com.walmartlabs.internal.data;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstraction of the seat matrix that represents the theater.
 * 
 * @author prasad
 *
 */
public class SeatMap {

	private static Logger logger = LoggerFactory.getLogger(SeatMap.class);

	/* List of Rows in the seat matrix */
	private List<Row> rows;
	
	/* List of Confirmed Bookings that hold the seats in the seat matrix */
	private List<ConfirmedBooking> confirmedBookings;
	
	/* Total capacity of the seat matrix */
	private int totalCapacity;
	
	/* No of seats in the seat matrix available for allocation */
	private int remainingSeats;
	
	/* No of rows in the seat matrix */
	private int noOfRows;
	
	/* No of seats in each row */
	private int noOfCols;

	public SeatMap(int noOfRows, int noOfColumns) {
		totalCapacity = noOfRows * noOfColumns;
		remainingSeats = totalCapacity;
		this.noOfRows = noOfRows;
		this.noOfCols = noOfColumns;
		confirmedBookings = new ArrayList<ConfirmedBooking>();
		rows = new ArrayList<Row>(noOfRows);
		initializeSeats(noOfRows, noOfColumns);
	}
	
	private void initializeSeats(int noOfRows, int noOfColumns) {
		Row row = null;
		int rowId = (int) 'A';
		for (int i = 0; i < noOfRows; i++, rowId++) {
			row = new Row();
			row.setRowId((char) rowId);
			row.initializeSeats(noOfColumns);
			rows.add(row);
		}

	}

	public int getNoOfRows() {
		return noOfRows;
	}

	public int getNoOfCols() {
		return noOfCols;
	}

	public int getTotalCapacity() {
		return this.totalCapacity;
	}

	public int getRemainingSeats() {
		return this.remainingSeats;
	}

	public void updateRemainingSeats(int noOfAllocatedSeats) {
		this.remainingSeats -= noOfAllocatedSeats;
	}
	
	public void addToConfirmedBookings(ConfirmedBooking booking) {
		this.confirmedBookings.add(booking);
	}

	public List<ConfirmedBooking> getConfirmedBookings() {
		return this.confirmedBookings;
	}

	public List<Row> getRows() {
		return rows;
	}

	/**
	 * Display the seat matrix
	 * @return
	 */
	public boolean displaySeatAllocation() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\n");
		for (Row row : rows) {
			buffer.append("ROW : " + row.getRowId());
			for (Seat seat : row.getSeats()) {
				buffer.append(" " + seat.toString());
			}
			buffer.append(
					"\n----------------------------------------------------------------------------------------------------------------------\n");
		}
		logger.debug(buffer.toString());
		return true;
	}

}
