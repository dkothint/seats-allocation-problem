package com.walmartlabs.internal.data;

/**
 * Represents the reservations read from the input. Holds the status of
 * reservation as well during the allocation process.
 * 
 * @author prasad
 *
 */
public class Reservation implements Comparable<Reservation> {
	
	/* Reservation Id */
	private String id;
	
	/* No of seats under the reservation */
	private int noOfSeats;
	
	/* Reservation status */
	private ReservationStatus status;

	public Reservation() {
		this.status = ReservationStatus.PENDING;
	}

	public ReservationStatus getStatus() {
		return status;
	}

	public void setStatus(ReservationStatus status) {
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getNoOfSeats() {
		return noOfSeats;
	}

	public void setNoOfSeats(int noOfSeats) {
		this.noOfSeats = noOfSeats;
	}

	public String toString() {
		return this.id + " " + this.noOfSeats;
	}

	public int compareTo(Reservation r) {
		if (this.noOfSeats >= r.getNoOfSeats()) {
			return 1;
		} else {
			return -1;
		}
	}

}
