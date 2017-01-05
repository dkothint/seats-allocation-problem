package com.walmartlabs.internal.data;

import java.util.List;

/**
 * Represents reservation whose booking is confirmed
 * @author prasad
 *
 */
public class ConfirmedBooking implements Comparable<ConfirmedBooking>{

	/* Reservation Id whose booking is confirmed */
	private String reservationId;
	
	/* No of Seats confirmed under this reservation */
	private List<Seat> allotedSeats;
	
	public String getReservationId() {
		return reservationId;
	}
	public void setReservationId(String reservationId) {
		this.reservationId = reservationId;
	}
	public List<Seat> getAllotedSeats() {
		return allotedSeats;
	}
	public void setAllotedSeats(List<Seat> allotedSeats) {
		this.allotedSeats = allotedSeats;
	}
	
	/**
	 * Return the booking information if the form : Rid A1, A2
	 */
	public String toString(){
		StringBuffer result = new StringBuffer();
		result.append(this.reservationId);
		
		for(Seat seat: allotedSeats){
			result.append(" ").append(seat.getSeatNo()).append(",");
		}
		return result.substring(0,result.length() - 1);
	}
	
	/**
	 * Compare by the reservation Id. Uses string comparison.
	 */
	public int compareTo(ConfirmedBooking cb) {
		return this.reservationId.compareToIgnoreCase(cb.getReservationId());
	}
}
