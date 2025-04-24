package ru.lab06.storage;

import ru.lab06.model.Flat;

import java.util.Date;
import java.util.Vector;

public interface StorageLike {
    Vector<Flat> getFlatStorage();
    Integer getCurrentId();
    void setCurrentId(Integer id);
    Date getInitializationDate();
    String getFilename();
}
