package com.walmartlabs.internal.data;

/**
 * Abstraction of a Seat in the seat matrix 
 * @author prasad
 *
 */
public class Seat {

	/* Status of the seat */
	private SeatStatus status;
	
	/* Seat No which has rowid+number of the seat */
	private String seatNo;
	
	/* Reservation No to which this seat is allocated */
	private String reservationNo;

	public Seat(SeatStatus status, String seatNo) {
		this.status = status;
		this.seatNo = seatNo;
	}

	public String getSeatNo() {
		return seatNo;
	}

	public void setSeatNo(String seatNo) {
		this.seatNo = seatNo;
	}

	public SeatStatus getStatus() {
		return status;
	}

	public void setStatus(SeatStatus status) {
		this.status = status;
	}
	
	/**
	 * Return the Seat information in the form SeatNo[ReservationNo]
	 * XXXX indicates that the seat is not reserved.
	 */
	public String toString(){
		String statusStr = this.status == SeatStatus.AVAILABLE ? "XXXX" : this.reservationNo;
		return this.getSeatNo()+"["+statusStr+"]";
	}

	public String getReservationNo() {
		return reservationNo;
	}

	public void setReservationNo(String reservationNo) {
		this.reservationNo = reservationNo;
	}
}
