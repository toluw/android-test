package ng.riby.androidtest.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "items")
public class Item {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;
    private double startLat, stopLat, startLong, stopLong;

    public Item(double startLat, double stopLat, double startLong, double stopLong) {
        this.startLat = startLat;
        this.stopLat = stopLat;
        this.startLong = startLong;
        this.stopLong = stopLong;
    }

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getStartLat() {
        return startLat;
    }

    public void setStartLat(double startLat) {
        this.startLat = startLat;
    }

    public double getStopLat() {
        return stopLat;
    }

    public void setStopLat(double stopLat) {
        this.stopLat = stopLat;
    }

    public double getStartLong() {
        return startLong;
    }

    public void setStartLong(double startLong) {
        this.startLong = startLong;
    }

    public double getStopLong() {
        return stopLong;
    }

    public void setStopLong(double stopLong) {
        this.stopLong = stopLong;
    }
}
