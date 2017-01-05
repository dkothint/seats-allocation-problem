package com.walmartlabs.internal.core;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.walmartlabs.internal.data.ConfirmedBooking;
import com.walmartlabs.internal.data.Reservation;
import com.walmartlabs.internal.data.ReservationStatus;
import com.walmartlabs.internal.data.Row;
import com.walmartlabs.internal.data.Seat;
import com.walmartlabs.internal.data.SeatMap;

/**
 * LinearSeatAllocator is more of a brute force approach to the seat allocation
 * problem with a couple of small improvements. It takes the input reservations
 * and starts allocating seats in the order they come. However, in this process
 * its obvious that certain holes shall be left in the seat matrix as we proceed
 * with allocation. This algorithm tries to minimize holes by first checking if
 * current reservation fits in any of the existing holes before going ahead with
 * placing it in a linear manner.
 * 
 * During seat allocation, the goal is to first make sure that the first MxN
 * reservations are given priority. Effectiveness of seat allocation can be
 * measured by this metric.
 * 
 * @author prasad
 *
 */
public class LinearSeatAllocator implements SeatAllocator {

	private static Logger logger = LoggerFactory.getLogger(LinearSeatAllocator.class);

	public void allocateSeats(SeatMap seatMap, List<Reservation> firstMxNReservations,
			List<Reservation> remainingReservations) {

		logger.debug("Beginning linear seat allocation. Size of firstMxNReservations : " + firstMxNReservations.size()
				+ " and size of remainingReservations : " + remainingReservations.size());
		int noOfAllocatedSeats = processReservations(seatMap, firstMxNReservations);

		logger.debug("noOfAllocatedSeats linearly from firstMxNReservations : " + noOfAllocatedSeats);
		
		// UnAllotted reservations from the first list
		for (Reservation curReservation : firstMxNReservations) {
			if (curReservation.getStatus() != ReservationStatus.SUCCESS) {
				curReservation.setStatus(ReservationStatus.FAILED);
			}
		}

		// fill the remaining seats, if any from ramainingReservations list
		if (noOfAllocatedSeats < seatMap.getTotalCapacity() && !remainingReservations.isEmpty()) {
			noOfAllocatedSeats += processReservations(seatMap, remainingReservations);
		}

		seatMap.updateRemainingSeats(noOfAllocatedSeats);
		logger.debug("Total noOfAllocatedSeats linearly " + noOfAllocatedSeats);
	}

	/**
	 * Fill up the seats starting from the last row and proceeding towards
	 * first. If the current reservation can be fit into any of the holes
	 * created in the previous rows, it is used to fill up the hole first.
	 * 
	 * @param seatMap
	 * @param reservations
	 * @return Number of seats allocated from the given reservations list
	 */
	private int processReservations(SeatMap seatMap, List<Reservation> reservations) {
		int noOfAllocatedSeats = 0;
		Row row = null;
		int noOfRows = seatMap.getRows().size();
		for (Reservation curReservation : reservations) {
			for (int i = noOfRows - 1; i >= 0; i--) {
				row = seatMap.getRows().get(i);
				if (row.getAvailableSeatsCount() >= curReservation.getNoOfSeats()) {
					confirmBooking(row, curReservation, seatMap);
					curReservation.setStatus(ReservationStatus.SUCCESS);
					noOfAllocatedSeats += curReservation.getNoOfSeats();
					break;
				}
			}
		}

		return noOfAllocatedSeats;
	}

	/**
	 * Reserves the seats and confirms the booking
	 * 
	 * @param row
	 * @param curReservation
	 * @param seatMap
	 */
	private void confirmBooking(Row row, Reservation curReservation, SeatMap seatMap) {
		List<Seat> reservedSeats = null;
		reservedSeats = row.reserveSeats(curReservation);

		ConfirmedBooking booking = new ConfirmedBooking();
		booking.setReservationId(curReservation.getId());
		booking.setAllotedSeats(reservedSeats);
		seatMap.addToConfirmedBookings(booking);
	}

}
