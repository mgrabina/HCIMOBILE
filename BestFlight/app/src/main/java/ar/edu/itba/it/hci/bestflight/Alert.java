package ar.edu.itba.it.hci.bestflight;

/**
 * Created by juan on 6/23/2017.
 */

public class Alert {
    private Integer flight;
    private String status;
    private String airline;

    public Alert(Integer flight, String airline) {
        this.flight = flight;
        this.airline = airline;
        status = "default";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Alert{" +
                "flight=" + flight +
                ", status='" + status + '\'' +
                ", airline='" + airline + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Alert alert = (Alert) o;

        if (!flight.equals(alert.flight)) return false;
        return airline.equals(alert.airline);

    }

    @Override
    public int hashCode() {
        int result = flight.hashCode();
        result = 31 * result + airline.hashCode();
        return result;
    }
}
