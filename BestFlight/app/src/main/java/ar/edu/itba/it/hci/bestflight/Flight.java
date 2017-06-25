package ar.edu.itba.it.hci.bestflight;

/**
 * Created by Santiago on 6/24/2017.
 */

public class Flight {
     Integer flightNumber;
     String status;
     String airline;
     Integer id;
    String departureTime;
    String arrivalTime;
    String departureTerminal;
    String arrivalTerminal;
    String departureGate;
    String arrivalGate;
    String baggageGate;
    String airlineId;


    public Flight( Integer flightNumber,String airline,String status, Integer id, String departureTime, String arrivalTime,
                   String departureTerminal, String arrivalTerminal, String departureGate, String arrivalGate, String baggageGate, String airlineId){

        this.flightNumber = flightNumber;
        this.status = status;
        this. airline = airline;
        this.id = id;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.departureTerminal = departureTerminal;
        this.arrivalTerminal = arrivalTerminal;
        this.departureGate = departureGate;
        this.arrivalGate = arrivalGate;
        this.baggageGate = baggageGate;
        this.airlineId = airlineId;


    }

}
