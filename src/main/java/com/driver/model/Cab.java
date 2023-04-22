package com.driver.model;
import javax.persistence.*;
@Entity
@Table(name="cab")
public class Cab {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int perKmrate;
    private boolean available;
    public Cab(){}

    public Cab(int id, int perKmrate, boolean available) {
        this.id = id;
        this.perKmrate = perKmrate;
        this.available = available;
    }

    @OneToOne(mappedBy = "cab",cascade = CascadeType.ALL)
    private Driver driver;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPerKmrate() {
        return perKmrate;
    }

    public void setPerKmrate(int perKmrate) {
        this.perKmrate = perKmrate;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}