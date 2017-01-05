package com.walmartlabs.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 
 * @author prasad
 *
 */
public class InputGenerator {

	private int noOfFiles;
	private int noOfInputs;
	private int maxSeatsPerReservation;
	private String folderPath;
	private String genMode;

	public String getGenMode() {
		return genMode;
	}

	public void setGenMode(String genMode) {
		this.genMode = genMode;
	}

	public int getMaxSeatsPerReservation() {
		return maxSeatsPerReservation;
	}

	public void setMaxSeatsPerReservation(int maxSeatsPerReservation) {
		this.maxSeatsPerReservation = maxSeatsPerReservation;
	}

	public String getFolderPath() {
		return folderPath;
	}

	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	public int getNoOfFiles() {
		return noOfFiles;
	}

	public void setNoOfFiles(int noOfFiles) {
		this.noOfFiles = noOfFiles;
	}

	public int getNoOfInputs() {
		return noOfInputs;
	}

	public void setNoOfInputs(int noOfInputs) {
		this.noOfInputs = noOfInputs;
	}

	public static void main(String[] args) {
		InputGenerator ig = new InputGenerator();

		Scanner sc = new Scanner(System.in);
		System.out.println("Welcome! This is a simple tool for generating inputs for the seat allocation problem."
				+ "This is a very trivial one and has no validations in place, yet!");

		System.out.println("Enter the input generation mode : 0 for random and 1(or any other INT) for weighted");
		int inpGenMode = sc.nextInt();
		String genMode = (inpGenMode == 0) ? "random" : "weighted";
		ig.setGenMode(genMode);
		
		System.out.println("Enter the no of input files to generate. Please be kind and keep it small ;)");
		ig.setNoOfFiles(sc.nextInt());

		System.out.println("Enter the no of inputs in each file. You could try to be realistic :)");
		ig.setNoOfInputs(sc.nextInt());

		System.out.println("Enter the max no of seats per reservation. Please be sure that it is within proper range");
		ig.setMaxSeatsPerReservation(sc.nextInt());

		int[] weights = null;
		int totalWeight = 0;
		if (0 != inpGenMode) {
			weights = new int[ig.getMaxSeatsPerReservation()];
			
			for(int i = 0; i < weights.length; i++){
				weights[i] = 1;
			}
			totalWeight = weights.length;
			System.out.println(
					"In weighted distribution, probability of a few numbers being selected is 4 times more than the rest");
			System.out.println("Since you selected weighted distribution of inputs, please give additional inputs");
			System.out.println("How many unique numbers do you want to have more weightage");
			int counter = sc.nextInt();

			System.out.println("Indicate those unique numbers");
			for(int i = 0; i < counter; i++){
				weights[sc.nextInt() - 1] = 4;
			}
			
			totalWeight += (3*counter);
			for(int i = 0; i < weights.length; i++){
				System.out.println(""+i+" : "+weights[i]);
			}
			System.out.println("totalWeight"+totalWeight);
		}

		System.out.println(
				"Enter the directory where files are to be created. Please make sure that the directory exits!");
		ig.setFolderPath(sc.next());

		File parentDir = new File(ig.getFolderPath());
		if (!parentDir.isDirectory()) {
			System.out.println("InputGenerator.getInputFiles() Path specified is not a folder");
			sc.close();
			return;
		}

		ig.generateInputs(parentDir,weights, totalWeight);
		
		System.out.println("Generation Successful!");
		
		sc.close();
		
	}

	private void generateInputs(File parentDir, int[] weights, int totalWeight) {

		File[] inputFiles = this.getInputFiles(parentDir);
		Writer writer = null;
		int randomNo = 0;
		for (File file : inputFiles) {
			try {
				writer = getWriter(file);
				for (int i = 0; i < noOfInputs; i++) {
					randomNo = this.getRandomNo(weights, totalWeight);
					writer.write("R_" + (i+1) + " " + randomNo + "\n");
				}
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private int getRandomNo(int[] weights, int totalWeight){
		int randomNo = -1;
		if(this.getGenMode().equals("random")){
			randomNo = ThreadLocalRandom.current().nextInt(1, maxSeatsPerReservation + 1);
		}
		else{
			randomNo = getWeightedRandomNo(weights, totalWeight);
		}
		return randomNo;
	}
	
	private int getWeightedRandomNo(int[] weights, int totalWeight){
		int randomNo = -1;
		int random = ThreadLocalRandom.current().nextInt(1, totalWeight + 1);
		
		for (int i = 0; i < weights.length; i++)
		{
		    random -= weights[i];
		    if (random <= 0)
		    {
		        randomNo = i;
		        break;
		    }
		}
		
		return randomNo + 1;
	}

	private File[] getInputFiles(File parentDir) {
		File file = null;
		File[] files = new File[noOfFiles];
		clearDirectoryContents(parentDir);
		for (int i = 0; i < noOfFiles; i++) {
			try {
				file = new File(parentDir, "input" + i + ".txt");
				file.createNewFile();
				files[i] = file;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return files;
	}

	private void clearDirectoryContents(File directory) {
		for (File file : directory.listFiles()) {
			if (file.isDirectory())
				clearDirectoryContents(file);
			file.delete();
		}
	}

	private Writer getWriter(File file) {
		try {
			PrintWriter writer = new PrintWriter(file);
			return writer;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
