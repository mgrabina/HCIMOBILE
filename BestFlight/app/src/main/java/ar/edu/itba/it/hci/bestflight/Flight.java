package ar.edu.itba.it.hci.bestflight;

/**
 * Created by Santiago on 6/24/2017.
 */

public class Flight {
     Integer flightNumber;
     String status;
     String airline;
     Integer id; //airlineId+flightNumber



    public Flight( Integer flightNumber,String airline,String status, Integer id ){

        this.flightNumber = flightNumber;
        this.status = status;
        this. airline = airline;
        this.id = id;


    }

}
