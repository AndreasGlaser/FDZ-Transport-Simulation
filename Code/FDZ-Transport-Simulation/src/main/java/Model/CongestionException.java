package Model;

public class CongestionException extends Exception {

    private Station station;

    public CongestionException(String msg, Station station){
        super(msg);
        this.station = station;
    }

    public Station getBlockingStation(){
        return station;
    }
}
