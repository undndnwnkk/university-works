package ru.lab06.model;

import java.io.Serializable;

public class House implements Serializable {
    private String name; // Может быть null
    private int year; // > 0, максимум 822
    private long numberOfFlatsOnFloor; // > 0

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }
    public long getNumberOfFlatsOnFloor() {
        return numberOfFlatsOnFloor;
    }
    public void setNumberOfFlatsOnFloor(long numberOfFlatsOnFloor) {
        this.numberOfFlatsOnFloor = numberOfFlatsOnFloor;
    }

    @Override
    public String toString() {
        return "\n       Name of the house: " + name +
                "\n       Year: " + year +
                "\n       Number of flats on the floor: " + numberOfFlatsOnFloor;
    }
}
