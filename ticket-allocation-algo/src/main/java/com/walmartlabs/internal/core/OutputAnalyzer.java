package com.walmartlabs.internal.core;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.walmartlabs.internal.data.ConfirmedBooking;
import com.walmartlabs.internal.data.Reservation;
import com.walmartlabs.internal.data.SeatMap;

/**
 * A simple tool to analyze the Effectiveness of seat allocation algorithm. It
 * spits out 2 metrics: Effectiveness and Resource Utilization. The goal during
 * seat allocation is to ensure the top MxN seats are given preference and are
 * alloted the seats. The more we miss from this, the less effective our
 * algorithm is. Also, another key goal to achieve max theater utilization in
 * terms of seat allotment. Effectiveness measure says how many reservations in
 * the top MxN did we miss and Resource utilization metric says what percentage
 * of seats did we not utilize.
 * 
 * @author prasad
 *
 */
public class OutputAnalyzer {

	private static Logger logger = LoggerFactory.getLogger(OutputAnalyzer.class);

	public void analyzeSeatAllocation(ReservationsReader reader, SeatMap seatMap) {
		List<Reservation> firstMxNReservations = reader.getFirstMxNReservations();
		List<ConfirmedBooking> confirmedBookings = seatMap.getConfirmedBookings();

		List<Reservation> unAllocatedReservations = new ArrayList<Reservation>();
		boolean found = false;
		for (Reservation r : firstMxNReservations) {
			found = false;
			for (ConfirmedBooking booking : confirmedBookings) {
				if (r.getId().equals(booking.getReservationId())) {
					found = true;
					break;
				}
			}
			if (!found) {
				unAllocatedReservations.add(r);
			}
		}
		logger.debug(
				"================================================== EFFECTIVENESS ====================================================================");

		if (unAllocatedReservations.isEmpty()) {
			logger.debug("We did pretty well there! All top MxN reservations were honoured. Great Job!");
		} else {
			logger.debug("Following Reservations were not honoured!");
			int noOfSeats = 0;
			for (Reservation r : unAllocatedReservations) {
				logger.debug("" + r);
				noOfSeats += r.getNoOfSeats();
			}

			double failurePercentage = (((double) unAllocatedReservations.size()) / firstMxNReservations.size()) * 100;
			logger.debug("Percentage of reservations not honoured: " + failurePercentage);
			logger.debug("No Of Seats that were not honoured: " + noOfSeats);
		}

		int noOfSeatsAllocated = 0;
		for (ConfirmedBooking booking : confirmedBookings) {
			noOfSeatsAllocated += booking.getAllotedSeats().size();
		}

		int totalNoOfSeats = seatMap.getTotalCapacity();
		int theatreVacancy = totalNoOfSeats - noOfSeatsAllocated;

		logger.debug(
				"================================================== RESOURCE UTILIZATION ============================================================");

		if (theatreVacancy == 0) {
			logger.debug("All seats are allocated.");
		} else {
			double theatreVacancyPercentage = (((double) theatreVacancy) / totalNoOfSeats) * 100;
			logger.debug("Percentage of seats not allocated : " + theatreVacancyPercentage);
		}

	}
}
