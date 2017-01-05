package com.walmartlabs.internal.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.walmartlabs.internal.data.Reservation;

/**
 * Reader class for reading and parsing the reservations from the input file. It
 * populates 2 lists: firstMxNReservaations who are to be given the first
 * preference during the seat allocation and remainingReservations.
 * 
 * @author prasad
 *
 */
public class ReservationsReader {

	/* Top M cross N reservations according to the order of booking */
	private List<Reservation> firstMxNReservations;
	
	/* Remaining reservations after firstMxNReservations */
	private List<Reservation> remainingReservations;

	public ReservationsReader() {
		firstMxNReservations = new ArrayList<Reservation>();
		remainingReservations = new ArrayList<Reservation>();
	}

	public List<Reservation> getFirstMxNReservations() {
		return firstMxNReservations;
	}

	public List<Reservation> getRemainingReservations() {
		return remainingReservations;
	}

	public void readReservations(File file, int noOfRows, int noOfCols) {
		BufferedReader bufReader = null;
		try {
			String line = "";
			String[] tokens;
			int noOfSeats = noOfRows * noOfCols;
			int counter = 0;
			int curOrder;
			bufReader = new BufferedReader(new FileReader(file));

			while ((line = bufReader.readLine()) != null) {
				tokens = line.split(" ");
				curOrder = Integer.parseInt(tokens[1]);
				// Rogue inputs, neglecting them
				if (curOrder > noOfSeats || curOrder < 1) {
					continue;
				}
				counter += curOrder;

				if (counter <= noOfSeats) {
					processCurrentBooking(tokens, noOfCols, noOfSeats, firstMxNReservations);

				} else {
					processCurrentBooking(tokens, noOfCols, noOfSeats, remainingReservations);
				}

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bufReader != null) {
				try {
					bufReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void processCurrentBooking(String[] curBooking, int noOfCols, int noOfSeats,
			List<Reservation> reservationsList) {
		Reservation reservation = null;
		int curOrder = Integer.parseInt(curBooking[1]);
		if (curOrder <= noOfCols) {
			reservation = new Reservation();
			reservation.setId(curBooking[0]);
			reservation.setNoOfSeats(curOrder);
			reservationsList.add(reservation);
		} else {
			handleLargeBookings(curBooking, noOfCols, noOfSeats, reservationsList);
		}
	}

	/*
	 * Large bookings are those with noOfSeats more than the noOfColumns. Such
	 * bookings are split into chunks of size noOfColumns. Remaining seats after
	 * splitting are merged with the last chunk (of size = noOfcolumns) and that
	 * cumulative chunk is split into 2 halves. This is to make sure that nobody
	 * is seated alone.
	 */
	private void handleLargeBookings(String[] curBooking, int noOfCols, int noOfSeats,
			List<Reservation> reservationsList) {
		Reservation reservation = null;
		int curOrder = Integer.parseInt(curBooking[1]);
		int curOrderSplitCount = curOrder / noOfCols;

		for (int i = 0; i < curOrderSplitCount - 1; i++) {
			reservation = new Reservation();
			reservation.setId(curBooking[0]);
			reservation.setNoOfSeats(noOfSeats);
			reservationsList.add(reservation);
		}
		// Split the last partition into two to make sure no individual is
		// seated alone
		int remainingSeats = curOrder - ((curOrderSplitCount - 1) * noOfSeats);
		int splitSize = remainingSeats / 2;
		reservation = new Reservation();
		reservation.setId(curBooking[0]);
		reservation.setNoOfSeats(splitSize);
		reservationsList.add(reservation);

		reservation = new Reservation();
		reservation.setId(curBooking[0]);
		reservation.setNoOfSeats(remainingSeats - splitSize);
		reservationsList.add(reservation);

	}

}
