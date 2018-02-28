# Seats-allocation-problem

Overview:
Problem Statement: Given an input file with a set of reservations, and a seat matrix representing a theater, implement a solution for seat allocation with a goal to maximize both customer satisfaction and theater utilization.

Notes and Assumptions:
- Solution is implemented for a generic MxN matrix and not limited to 10x20 specifically. However, testing results presented were obtained by running the tests on a 10x20 matrix.
- There is a provision given to provide dimensions of seat matrix and the application launch mode via external properties file outside the binaries. This helps to be able run  the program for different sized matrices and launchmodes without having to rebuild and deploy the code. 

- Customer Satisfaction: Two factors are considered to ensure customer satisfaction. 1. Customers under one reservation are to be seated together. 2. Customers who have made bookings earlier are to be given preference.

- Theater utilization: Goal here is to ensure maximum number of seats are allocated during seat allocation process. 

- The implementation does not consider seating customers under different reservations separately(with a few empty seats in between) in the case where theater is sparsely populated. It is assumed, customers help themselves by occupying unreserved seats if they wish to have the privacy, once the movie starts. 

- Application supports 3 launch modes: linear, optimized and both. 

- LinearSeatAllocator is more of a brute force approach to the seat allocation problem with a couple of small improvements. It takes the input reservations and starts allocating seats in the order they come. However, in this process its obvious that certain holes shall be left in the seat matrix as we proceed with allocation. This algorithm tries to minimize holes by first checking if current reservation fits in any of the existing holes before going ahead with placing it in a linear manner.

- OptimizedSeatAllocator attempts to fill up one row completely before moving on to the next with the goal to minimize scattering of holes in the seat matrix. Filling up the row is modeled as a subset sum problem, which is solved using a backtracking solution. Remaining seats are filled up using the LinearSeatAllocator.

- Selecting ‘both’ as launch mode would run both algorithms producing 2 output files.


Running the application:
- Application uses maven build framework.
- Commands to run the build :
	Building the jar with dependencies: mvn clean compile install assembly:single
	Running the jar: java -jar <jar location> "<input file path>"
Example : java -jar /home/prasad/workspace/walmartlabs/ticket-allocation-algo/target/ticket-allocation-algo-0.0.1-SNAPSHOT-jar-with-dependencies.jar "/home/prasad/Desktop/wr/input0.txt"

- Alternately, we can execute both the above steps with a single command: mvn clean compile install assembly:single exec:java -Dexec.args="<input file path>"
Example: mvn clean compile install assembly:single exec:java -Dexec.args="/home/prasad/Desktop/wr/input0.txt"

- Application has been designed to have support for running multiple input files together. Just place all the input files in a folder and pass the folder path as the input parameter. One output file shall be created for every input file.

- Output files shall be generated in a folder called ‘output’ which is created in the same directory from  where the program is executed. 

Testing: 
- Gerating inputs for testing the application manually is too much of overhead. Hence, a simple tool – InputGenerator is developed and submitted along with this solution. 

InputGenerator: A simple tool for generating inputs for the seat allocation algorithm. This tool is not tied to the solution provided in any way. It is developed with a motive to ease testing, to facilitate generating random and weighted inputs using built-in libraries support, It is a stand-alone tool that can be used to test other candidates’ solutions as well. Using this tool is quite intuitive.
Steps for running the tool:
1. cd src
2. javac com/walmartlabs/main/InputGenerator.java 
3. java com.walmartlabs.main.InputGenerator
4. Provide inputs to the questions asked

Weighted distribution: Although number of seats in a reservation can be any number, from practical perspective, it is unlikely that it is random. It is more likely that couples visit the theater than a group of 17 individuals. That is to say, in a real world scenario, we are more likely to get reservations with 2 seats than those with 17 seats, for example. 
InputGenerator gives a provision to generate inputs resembling real world scenarios. We can specify numbers which are more likely to occur. These numbers are given 4 times more weightage than others. That is, random number generator is 4 times more likely to pick these numbers than others. 
In the tests run, the numbers 2,3,4,5,6,7 and 8 were selected to have extra weightage(out of the range 1-20). As demonstrated in the results, this strategy indeed helps to measure the effectiveness of algorithms as it clearly brings out the better performer in real world scenarios. 

Output Analysis: 
- OutputAnalyzer : A simple tool to analyze the effectiveness of seat allocation algorithm. It spits out 2 metrics: Effectiveness and Resource Utilization. The goal during seat allocation is to ensure the top MxN seats are given preference and are alloted the seats. The more we miss from this, the less effective our algorithm is. Also, another key goal is to achieve max theater utilization in terms of seat allotment. Effectiveness measure says what percentage of reservations in the top MxN did we miss and Resource utilization metric says what percentage of seats did we not utilize.

https://docs.google.com/a/asu.edu/spreadsheets/d/1f9-ZlHJvrzlWadW9Cto7kKjmOt7ekFDRpkD6CPngL7U/edit?usp=sharing 

Results: 
Random Inputs : 
Summary of analysis:
- Optimized solution constantly outperforms linear solution in terms of resource utilization
- Sometimes 'Linear' does outperform optimized solution in terms of honouring top MxN reservations
- 10 / 25 cases remained neutral


Weighted Inputs: 
Summary of result analysis: 
- Optimized algorithm clearly outperforms linear one in terms of resource utilization
- As inputs become more realistic, optimized algorithm starts to outperform linear one in terms of effectiveness as well
- 8 / 25 cases remained neutral
