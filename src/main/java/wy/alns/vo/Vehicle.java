package wy.alns.vo;

import lombok.Data;

@Data
public class Vehicle {
    private String id;

    public Vehicle(String id) {
        this.id = id;
    }

    private int capacity;
}
