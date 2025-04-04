package ru.lab05.model;

import java.io.Serializable;
import java.util.Date;

public class Flat implements Comparable<Flat>, Serializable {
    private int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.util.Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Integer area; //Значение поля должно быть больше 0
    private long numberOfRooms; //Значение поля должно быть больше 0
    private Furnish furnish; //Поле не может быть null
    private View view; //Поле не может быть null
    private Transport transport; //Поле может быть null
    private House house; //Поле может быть null

    @Override
    public String toString() {
        return "Flat " + id + " info: " +
                "\n    Name of the flat: " + name +
                "\n    X coordinate: " + coordinates.getX() +
                "\n    Y coordinate: " + coordinates.getY() +
                "\n    Creation date: " + creationDate +
                "\n    Area: " + area +
                "\n    Number of rooms: " + numberOfRooms +
                "\n    Furnish type: " + furnish +
                "\n    View type: " + view +
                "\n    Transport type: " + transport +
                "\n    House info: " + house;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Coordinates getCoordinates() {
        return coordinates;
    }
    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }
    public Date getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    public Integer getArea() {
        return area;
    }
    public void setArea(Integer area) {
        this.area = area;
    }
    public long getNumberOfRooms() {
        return numberOfRooms;
    }
    public void setNumberOfRooms(long numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }
    public Furnish getFurnish() {
        return furnish;
    }
    public void setFurnish(Furnish furnish) {
        this.furnish = furnish;
    }
    public View getView() {
        return view;
    }
    public void setView(View view) {
        this.view = view;
    }
    public Transport getTransport() {
        return transport;
    }
    public void setTransport(Transport transport) {
        this.transport = transport;
    }
    public House getHouse() {
        return house;
    }
    public void setHouse(House house) {
        this.house = house;
    }

    @Override
    public int compareTo(Flat other) {
        int cmp = this.name.compareTo(other.name);
        if (cmp != 0) {
            return cmp;
        }
        return Integer.compare(this.id, other.id);
    }
}
