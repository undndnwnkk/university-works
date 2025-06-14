package ru.lab06.model;

import java.io.Serializable;

public class House implements Serializable {
    private String name;
    private Integer year;
    private Long numberOfFloors; // ✅ правильное название

    public House() {}

    public House(String name, Integer year, Long numberOfFloors) {
        this.name = name;
        this.year = year;
        this.numberOfFloors = numberOfFloors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Long getNumberOfFloors() {
        return numberOfFloors;
    }

    public void setNumberOfFloors(Long numberOfFloors) {
        this.numberOfFloors = numberOfFloors;
    }

    @Override
    public String toString() {
        return "House name: " + name +
                ", year: " + year +
                ", flats on floor: " + numberOfFloors;
    }
}
