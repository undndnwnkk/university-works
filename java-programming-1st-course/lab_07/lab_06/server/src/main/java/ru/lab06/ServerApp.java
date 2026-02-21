package ru.lab06;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.lab06.command.CommandRequest;
import ru.lab06.command.CommandResponse;
import ru.lab06.core.Storage;
import ru.lab06.db.DatabaseConnector;
import ru.lab06.network.CommandExecutor;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.*;

public class ServerApp {
    private static final int PORT = 8080;
    private static final int BUFFER_SIZE = 65536;
    private static final Logger logger = LogManager.getLogger(ServerApp.class);

    private static final ExecutorService requestHandlerPool = Executors.newFixedThreadPool(4);
    private static final ForkJoinPool responseSenderPool = new ForkJoinPool();

    public static void main(String[] args) {
        logger.info("Server is starting...");

        try (Connection conn = DatabaseConnector.connect()) {
            logger.info("Connected to database!");
        } catch (SQLException e) {
            logger.fatal("Failed to connect to database: " + e.getMessage());
            return;
        }

        Storage storage = new Storage();

        try (DatagramChannel channel = DatagramChannel.open()) {
            channel.bind(new InetSocketAddress(PORT));
            channel.configureBlocking(false);
            logger.info("Server is ready on port " + PORT);

            while (true) {
                ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                SocketAddress clientAddress = channel.receive(buffer);

                if (clientAddress == null) continue;

                new Thread(() -> {
                    try {
                        buffer.flip();
                        ObjectInputStream ois = new ObjectInputStream(
                                new ByteArrayInputStream(buffer.array(), 0, buffer.limit())
                        );
                        CommandRequest commandRequest = (CommandRequest) ois.readObject();
                        logger.info("Received command: " + commandRequest.getCommandName());

                        storage.setLastLogin(commandRequest.getLogin());

                        requestHandlerPool.submit(() -> {
                            try {
                                CommandResponse response = CommandExecutor.execute(commandRequest, storage);

                                responseSenderPool.execute(() -> {
                                    try {
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        ObjectOutputStream oos = new ObjectOutputStream(baos);
                                        oos.writeObject(response);
                                        oos.flush();

                                        ByteBuffer responseBuffer = ByteBuffer.wrap(baos.toByteArray());
                                        channel.send(responseBuffer, clientAddress);
                                        logger.info("Sent response for command: " + commandRequest.getCommandName());
                                    } catch (IOException e) {
                                        logger.error("Sending response failed: " + e.getMessage());
                                    }
                                });

                            } catch (Exception e) {
                                logger.error("Request processing failed: " + e.getMessage());
                            }
                        });

                    } catch (IOException | ClassNotFoundException e) {
                        logger.error("Receiving request failed: " + e.getMessage());
                    }
                }).start();
            }
        } catch (IOException e) {
            logger.fatal("Server launch error: " + e.getMessage());
        }
    }
}
