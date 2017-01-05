package com.walmartlabs.internal.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstraction of a row in the seat matrix
 * @author prasad
 *
 */
public class Row {

	/* row id */
	private char rowId;
	
	/* No of unAllocated seats in the row */
	private int availableSeatsCount;
	
	/* List of the seats in the row */ 
	private List<Seat> seats;
	
	public char getRowId() {
		return rowId;
	}

	public void setRowId(char rowId) {
		this.rowId = rowId;
	}

	public int getAvailableSeatsCount() {
		return availableSeatsCount;
	}

	public void setAvailableSeatsCount(int availableSeatsCount) {
		this.availableSeatsCount = availableSeatsCount;
	}

	public List<Seat> getSeats() {
		return seats;
	}

	public void initializeSeats(int noOfSeatsInRow){
		seats = new ArrayList<Seat>(noOfSeatsInRow);
		Seat seat = null;
		for(int i = 0; i < noOfSeatsInRow; i++){
			seat = new Seat(SeatStatus.AVAILABLE, ""+rowId+(i+1));
			seats.add(seat);
		}
		this.availableSeatsCount = noOfSeatsInRow;
		
	}
	
	/**
	 * Reserve seats of the <reservation> in the current row.
	 * @param reservation
	 * @return
	 */
	public List<Seat> reserveSeats(Reservation reservation){
		int startIndex = seats.size() - this.availableSeatsCount;
		int endIndex = startIndex + reservation.getNoOfSeats();
		List<Seat> reservedSeats = new ArrayList<Seat>();
		Seat seat = null;
		
		for(int i = startIndex; i < endIndex ; i++){
			seat = seats.get(i);
			seat.setReservationNo(reservation.getId());
			seat.setStatus(SeatStatus.BOOKED);
			reservedSeats.add(seat);
		}
		this.availableSeatsCount -= reservation.getNoOfSeats();
		return reservedSeats;
	}
	
}
