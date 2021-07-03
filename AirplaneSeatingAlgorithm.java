import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class AirplaneSeatingAlgorithm {
	static Scanner scan = new Scanner(System.in);
	static char[] seatPreferenceInOrder = {'A','W','M'};
	static HashMap<Integer,List<Context>> finalXWiseSeatsMap;
	public static void main(String[] args) {
		
		System.out.println("Enter the number of passengers: ");
		int noOfPassengers = scan.nextInt();
		int arraySet = 1, currentSet=0;  
		HashMap<Integer,Integer> layoutVsAllocation = new HashMap<Integer,Integer>();
		ArrayList<ArrayList<Integer> > seatingLayout = new ArrayList<ArrayList<Integer> >();
		 
		while (arraySet > currentSet) {  
	    	int m, n;
	    	System.out.print("Enter the number of rows for seating layout " + arraySet+ ": ");   
	    	m = scan.nextInt();   
	    	System.out.print("Enter the number of columns for seating layout " + arraySet+ ": ");   
	    	n = scan.nextInt();   
	    	seatingLayout.add(currentSet, new ArrayList<>(Arrays.asList(m, n)));
	    	System.out.print("Please enter (true/false) to proceed with another set of dimensions");
	    	boolean checkForNextLayout = scan.nextBoolean();
            if (checkForNextLayout) {
            	arraySet++;
	        	currentSet++;
            } else {
            	currentSet = arraySet;
            }	
	    };  
	    
	    List<Context> checkForValidInputs = validationAndSeatAssignment(noOfPassengers, seatingLayout, layoutVsAllocation);
	    allocatingSeats(checkForValidInputs, noOfPassengers, seatPreferenceInOrder);
	    finalResult(checkForValidInputs,layoutVsAllocation);
	    
	}

	private static List<Context> validationAndSeatAssignment(int noOfPassengers, ArrayList<ArrayList<Integer>> seatingLayout, HashMap<Integer, Integer> layoutVsAllocation) {
		if (noOfPassengers > 0 && seatingLayout != null && !seatingLayout.isEmpty()) {
			List<Context> seats = new ArrayList<Context>();
			int totalSeatsAvailable = 0;
			int totalColumns = 0;
			int layoutSize = seatingLayout.size();
			
			//Handling seat type
			for(int i=0; i<layoutSize; i++) {
				
				ArrayList<Integer> currentSeatingLayout = seatingLayout.get(i);
				int rows = currentSeatingLayout.get(1);
				int columns = currentSeatingLayout.get(0);
				
				totalSeatsAvailable += (rows*columns);
				totalColumns+=columns;
				
				for(int j=0; j<rows; j++) {
					for(int k=0; k<columns; k++) {
					char seatType = '-';
					if((i==0 && k==0) || ((i==layoutSize-1) && (k==columns-1))){
					seatType = 'W';
					}
					else if((k==columns-1) || k==0){
					seatType = 'A';
					}
					else if(k>0 || (k<columns-1)){
					seatType = 'M';
					}
					Context seat = new Context(j,k,i,seatType);
					seats.add(seat);
					}
					}
				layoutVsAllocation.put(i, totalColumns);
			}
			
			//Validation
			if (totalSeatsAvailable > noOfPassengers) {
				return seats;
			}
			else {
				System.out.print("Sorry the input is invalid, the passanger count should not exceed the total seat count");
				return null;
			}
		}
		System.out.print("Please give the correct input");
		return null;
	}
	
	private static void allocatingSeats(List<Context> seats, int noOfPassengers,char[] seatPreferenceInOrder) {
		HashMap<Integer,List<Context>> xWiseSeatsMap = groupXWiseSeatsMap(seats);
		List<Context> orderedSeats = new ArrayList<Context>(); 
		int passengerSeatNo=0;

		
		for(int i=0;i<seatPreferenceInOrder.length;i++) {
			addOrderedSeats(orderedSeats, xWiseSeatsMap, seatPreferenceInOrder[i]);
		}

		for(Context seat: orderedSeats) {
			 if(passengerSeatNo == noOfPassengers) {
			 break;
			 }
			 seat.setCount(++passengerSeatNo);
		}
		finalXWiseSeatsMap = xWiseSeatsMap;
	}
	
	public static void addOrderedSeats(List<Context> orderedSeats, HashMap<Integer,List<Context>> xWiseSeatsMap, char seatPreference){

		for(List<Context> xWiseSeats: xWiseSeatsMap.values())	{	
		for(Context seat: xWiseSeats) {
			if(seat.getType() == seatPreference) {
				orderedSeats.add(seat);
			}
		}
		}	
	}
	
	public static HashMap<Integer,List<Context>> groupXWiseSeatsMap(List<Context> seats) {
		HashMap<Integer,List<Context>> xWiseSeatsMap = new HashMap<Integer,List<Context>>();
		for(Context seat: seats)
		{
		List<Context> xWiseSeats = xWiseSeatsMap.get(seat.getRow());
		if(xWiseSeats == null || xWiseSeats.isEmpty()) {
		xWiseSeats = new ArrayList<Context>();
		xWiseSeatsMap.put(seat.getRow(), xWiseSeats);
		}
		xWiseSeats.add(seat);
		}
		return xWiseSeatsMap;
	}
	
	private static void finalResult(List<Context> seats, HashMap<Integer, Integer> layoutVsAllocation) {
		 for(Integer x: finalXWiseSeatsMap.keySet()) {	
		 List<Context> xWiseSeats = finalXWiseSeatsMap.get(x);
		 int xSpacer = 0;
		 int spacer = 0;

		 for(Context seat: xWiseSeats) { 
			 if(seat.getCol() == 0 && (!(seat.getLayout() == 0))) {
				 int actualSpacer = layoutVsAllocation.get(seat.getLayout()-1);
				 boolean isXSpaced = false;
				 while(xSpacer < actualSpacer) {
				 System.out.format("%3s","");
				 xSpacer++;
				 isXSpaced=true;
				 }
				 spacer = spacing(seat, spacer, isXSpaced);
			 }
	
			 if(seat.getCount() > 0) {
				 System.out.format("%2d", seat.getCount());
				 System.out.print(" ");
			 }
			 else {	
				 System.out.print(" x ");
			 }
			 xSpacer++;
		 }

		System.out.println();
		}
	}
	
	public static int spacing(Context seat, int spacer, boolean isXSpaced) {
		if(isXSpaced) {
			while(spacer<seat.getLayout()-1) {
				 System.out.print(" ");
				 spacer++;
			}
		}
		spacer++;	
		System.out.print("| ");	
		return spacer;
	}
}
