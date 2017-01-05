package com.walmartlabs.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.List;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.walmartlabs.internal.constants.AppConstants;
import com.walmartlabs.internal.core.LinearSeatAllocator;
import com.walmartlabs.internal.core.OptimizedSeatAllocator;
import com.walmartlabs.internal.core.OutputAnalyzer;
import com.walmartlabs.internal.core.ReservationsReader;
import com.walmartlabs.internal.core.SeatAllocator;
import com.walmartlabs.internal.data.ConfirmedBooking;
import com.walmartlabs.internal.data.Reservation;
import com.walmartlabs.internal.data.SeatMap;

public class AppLauncher {

	private static Logger logger = LoggerFactory.getLogger(AppLauncher.class);

	public static void main(String[] args) {

		int noOfRows;
		int noOfCols;
		String launchMode;

		String curDir = new File("").getAbsolutePath();
		Configurations configs = new Configurations();
		Configuration config = null;

		/**
		 * Look for the properties file in the properties folder that is
		 * expected to be present in the current directory. Use the local
		 * properties file, if this one is not present
		 */
		try {
			// Using File.separator keeps the path platform agnostic
			config = configs.properties(
					new File(curDir + File.separator + "properties" + File.separator + "config.properties")); 
			
		} catch (ConfigurationException cex) {
			logger.error("Exception while loading external config!" + cex.getMessage());
			try {
				config = configs.properties("config.properties");
			} catch (ConfigurationException e) {
				logger.error("Exception while loading Local config!" + e.getMessage());
			}
		}

		if (null != config) {
			noOfRows = config.getInt("noOfRows");
			noOfCols = config.getInt("noOfColumns");
			launchMode = config.getString("launchMode");
		} else {
			noOfRows = AppConstants.DEFAULT_ROWS;
			noOfCols = AppConstants.DEFAULT_COLUMNS;
			launchMode = AppConstants.DEFAULT_LAUNCH_MODE;
		}

		AppLauncher launcher = new AppLauncher();

		File pathParam = new File(args[0]);

		if(!pathParam.exists()){
			logger.error("Invalid Path passed as input!");
			return;
		}
		/**
		 * There is a provision given to work with multiple input files. If the
		 * path given is a file, proceed with it. If it is a directory, run the
		 * application for each of the files in it. If there are sub-directories
		 * inside the given folder, they are not traversed further.
		 */
		if (pathParam.isFile()) {
			launcher.launchSeatAllocation(pathParam, noOfRows, noOfCols, launchMode);
		} else {
			File[] files = pathParam.listFiles();
			for (File file : files) {
				if (file.isFile()) {
					launcher.launchSeatAllocation(file, noOfRows, noOfCols, launchMode);
				}
			}
		}
	}

	/**
	 * 
	 * @param inputFile
	 *            : File with reservations in the form : "ReservationId
	 *            NoOfSeats"
	 * @param noOfRows
	 *            : No of rows in the theater
	 * @param noOfCols
	 *            : No of seats in each rows
	 * @param launchMode
	 *            : linear - for brute force approach, optimized - for an
	 *            optimized version based on subset sum solution and both - for running both forms
	 */
	private void launchSeatAllocation(File inputFile, int noOfRows, int noOfCols, String launchMode) {
		SeatMap seatMap = new SeatMap(noOfRows, noOfCols);
		SeatAllocator seatAllocator = null;

		if ("linear".equals(launchMode)) {
			seatAllocator = new LinearSeatAllocator();
		} else if ("optimized".equals(launchMode)) {
			seatAllocator = new OptimizedSeatAllocator();
		} else if ("both".equals(launchMode)) {
			this.launchSeatAllocation(inputFile, noOfRows, noOfCols, "linear");
			this.launchSeatAllocation(inputFile, noOfRows, noOfCols, "optimized");
			return;
		} else {
			logger.error("Illeagal Launch Mode specified, use - linear/optimized/both");
			return;
		}
		ReservationsReader reader = new ReservationsReader();
		reader.readReservations(inputFile, noOfRows, noOfCols);

		logger.debug("\n\n============ Launching Seat Allocation Application ============");
		logger.debug("NoOfRows : "+noOfRows+", NoOfCols : "+noOfCols+", LaunchMode : "+launchMode);
		
		logger.debug("Top MxN reservations which are expected to be honoured : ");
		List<Reservation> reservations = reader.getFirstMxNReservations();
		for (Reservation r : reservations) {
			logger.debug("" + r);
		}

		logger.debug("Remaining reservations : ");
		reservations = reader.getRemainingReservations();
		for (Reservation r : reservations) {
			logger.debug("" + r);
		}

		List<ConfirmedBooking> confirmedBookings = null;
		String curDir = new File("").getAbsolutePath();
		String outputDir = curDir + File.separator + "output";
		File outputDirHandle = new File(outputDir);
		if(!outputDirHandle.exists()){
			outputDirHandle.mkdirs();	
		}

		// Output shall be stored in a directory called 'output' within the current directory. 
		File outputFile = new File(outputDir + File.separator + launchMode + "Soln-" + inputFile.getName());
		try {
			outputFile.createNewFile();
			Writer writer = this.getWriter(outputFile);
			seatAllocator.allocateSeats(seatMap, reader.getFirstMxNReservations(), reader.getRemainingReservations());

			confirmedBookings = seatMap.getConfirmedBookings();
			// Sorting reservations based on Reservation Id, just to aid us in verifying the output  
			Collections.sort(confirmedBookings);
			for (ConfirmedBooking booking : confirmedBookings) {
				writer.write(booking.toString());
				writer.write('\n');
			}
			writer.flush();
			if (logger.isDebugEnabled()) {
				seatMap.displaySeatAllocation();
			}

			logger.info(outputFile.getAbsolutePath());
			System.out.println(outputFile.getAbsolutePath());

			// Analyze the results
			if (logger.isDebugEnabled()) {
				OutputAnalyzer outputAnalyzer = new OutputAnalyzer();
				outputAnalyzer.analyzeSeatAllocation(reader, seatMap);
			}

		} catch (FileNotFoundException e) {
			logger.error("Error while creating writer instance !");

		} catch (IOException e) {
			logger.error("Error Creating output file !!" + e.getMessage());
		}
	}

	/*
	 * Get the Writer instance for the given file
	 */
	private Writer getWriter(File outputFile) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(outputFile);
		return writer;
	}

}
