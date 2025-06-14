package ru.lab06.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.lab06.db.FlatDaoImpl;
import ru.lab06.model.Flat;
import ru.lab06.storage.StorageLike;

import java.util.Date;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

public class Storage implements StorageLike {
    private Vector<Flat> flatStorage;
    private final Date initializationDate;
    private int currentId = 1;
    private static final Logger logger = (Logger) LogManager.getLogger(Storage.class);
    private String lastLogin;
    private final FlatDaoImpl flatDao = new FlatDaoImpl();
    private final ReentrantLock lock = new ReentrantLock();

    public Storage() {
        this.flatStorage = new Vector<>();
        flatStorage.addAll(flatDao.getAllFlats());
        this.initializationDate = new Date();
    }

    public void setLastLogin(String login) {
        lock.lock();
        try {
            this.lastLogin = login;
        } finally {
            lock.unlock();
        }
    }

    public String getLastLogin() {
        lock.lock();
        try {
            return lastLogin;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Date getInitializationDate() {
        return initializationDate;
    }

    @Override
    public Vector<Flat> getFlatStorage() {
        lock.lock();
        try {
            return flatStorage;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Integer getCurrentId() {
        lock.lock();
        try {
            return currentId;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void setCurrentId(Integer currentId) {
        lock.lock();
        try {
            this.currentId = currentId;
        } finally {
            lock.unlock();
        }
    }
}
