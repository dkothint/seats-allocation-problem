package com.walmartlabs.internal.core;

import java.util.List;

import com.walmartlabs.internal.data.Reservation;
import com.walmartlabs.internal.data.SeatMap;

/**
 * Interface for using Seat Allocation algorithm implementations
 * @author prasad
 *
 */
public interface SeatAllocator {

	public void allocateSeats(SeatMap seatMap, List<Reservation> firstMxNReservations,
			List<Reservation> remainingReservations);
	
}
