/**
 * 
 */
package com.walmartlabs.internal.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.walmartlabs.internal.data.ConfirmedBooking;
import com.walmartlabs.internal.data.Reservation;
import com.walmartlabs.internal.data.Row;
import com.walmartlabs.internal.data.Seat;
import com.walmartlabs.internal.data.SeatMap;

/**
 * OptimizedSeatAllocator attempts to fill up one row completely before moving
 * on to the next with the goal to minimize scattering of holes in the seat
 * matrix. Filling up the row is modeled as a subset sum problem, which is
 * solved using a backtracking solution. Remaining sets are filled up using the
 * LinearSeatAllocator.
 * 
 * During seat allocation, the goal is to first make sure that the first MxN
 * reservations are given priority. Effectiveness of seat allocation can be
 * measured by this metric.
 * 
 * @author prasad
 *
 */
public class OptimizedSeatAllocator implements SeatAllocator {

	private static Logger logger = LoggerFactory.getLogger(OptimizedSeatAllocator.class);

	/* Subset of reservations that exactly fill up one full row */
	private List<Reservation> subset;

	/* flag to track if subsets were found */
	private boolean isSubSetFound;

	public void allocateSeats(SeatMap seatMap, List<Reservation> firstMxNReservations,
			List<Reservation> remainingReservations) {

		logger.debug("Beginning Optimized seat allocation. Size of firstMxNReservations : "
				+ firstMxNReservations.size() + " and size of remainingReservations : " + remainingReservations.size());

		int noOfAllocatedSeats = 0;
		int rowIndex = seatMap.getNoOfRows() - 1;

		/*
		 * Using a recursive approach here to solve the subset sum problem, with
		 * allocated reservations being removed from the original list in every
		 * recursive call. Hence, proceeding with a copy of reservations here,
		 * leaving the original list as is to aid in analysis later. In a
		 * production environment where you do not need analysis, we don't have
		 * to work with the copy.
		 */
		List<Reservation> copyOfFirstMxNReservation = new ArrayList<Reservation>(firstMxNReservations);
		getReservationsThatFitInARow(copyOfFirstMxNReservation, 0, seatMap.getNoOfCols(), new Stack<Reservation>());
		while (subset != null && rowIndex >= 0) {
			for (Reservation r : this.subset) {
				confirmBooking(seatMap.getRows().get(rowIndex), r, seatMap);
				noOfAllocatedSeats += r.getNoOfSeats();
				copyOfFirstMxNReservation.remove(r);
			}
			rowIndex--;
			this.subset = null;
			this.isSubSetFound = false;
			getReservationsThatFitInARow(copyOfFirstMxNReservation, 0, seatMap.getNoOfCols(), new Stack<Reservation>());
		}

		logger.debug("noOfAllocatedSeats using the optimized approach : " + noOfAllocatedSeats);
		seatMap.updateRemainingSeats(noOfAllocatedSeats);
		allocateRemainingRows(seatMap, copyOfFirstMxNReservation, remainingReservations);
	}

	/**
	 * Allocate remaining seats using the Linear Seat Allocator
	 * 
	 * @param seatMap
	 * @param firstMxNReservations
	 * @param remainingReservations
	 */
	private void allocateRemainingRows(SeatMap seatMap, List<Reservation> firstMxNReservations,
			List<Reservation> remainingReservations) {

		LinearSeatAllocator linearSeatAllocator = new LinearSeatAllocator();
		linearSeatAllocator.allocateSeats(seatMap, firstMxNReservations, remainingReservations);
	}

	/**
	 * Reserves the seats and confirms the booking
	 * 
	 * @param row
	 * @param curReservation
	 * @param seatMap
	 */
	// This method is the same as the one present in LinearSeatAllocator.
	// Justification for duplicate code: We can avoid code duplication using 2
	// approaches, make both the classes extend an Abstract class and move this
	// method to that or have a helper class have this method and call it from
	// both the classes using helper class's instance. First approach doesn't
	// seem appropriate because, while one of the benefits of Inheritance is
	// code re-use, that should not be the motivation for designing the classes
	// using inheritance. Classes have to have natural meaningful properties for
	// using inheritance. For using helper class, creating a new class just to
	// avoid duplication of one simple method seemed a little overkill. For me,
	// ideal cases for moving to a new helper class would be - 1. Many re-usable
	// methods are present. 2. Even if there is only one method, that is
	// complicated enough and often under goes maintenance changes.
	private void confirmBooking(Row row, Reservation curReservation, SeatMap seatMap) {
		List<Seat> reservedSeats = null;
		reservedSeats = row.reserveSeats(curReservation);

		ConfirmedBooking booking = new ConfirmedBooking();
		booking.setReservationId(curReservation.getId());
		booking.setAllotedSeats(reservedSeats);
		seatMap.addToConfirmedBookings(booking);
	}

	/**
	 * Recursive solution for the subset sum problem. 
	 * Source : https://stackoverflow.com/a/31038556/7378431 
	 * @param reservations
	 * @param index
	 * @param sum
	 * @param solnStack
	 */
	private void getReservationsThatFitInARow(List<Reservation> reservations, int index, int sum,
			Stack<Reservation> solnStack) {
		if (isSubSetFound) {
			return;
		}
		if (sum == 0) {
			this.subset = new ArrayList<Reservation>(solnStack);
			isSubSetFound = true;
		}

		if (sum < 0) {
			return;
		}

		if (index == reservations.size()) {
			return;
		}

		// Guess that solution includes the current number
		solnStack.add(reservations.get(index));
		getReservationsThatFitInARow(reservations, index + 1, sum - reservations.get(index).getNoOfSeats(), solnStack);
		
		// Guess that solution does not include the current number
		solnStack.pop();
		getReservationsThatFitInARow(reservations, index + 1, sum, solnStack);
	}

}
