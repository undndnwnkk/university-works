package ru.lab05.server;

import ru.lab05.commands.CommandRequest;
import ru.lab05.commands.CommandResponse;
import ru.lab05.commands.SaveCommand;
import ru.lab05.core.Storage;
import ru.lab05.network.CommandExecutor;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerApp {
    private static final Logger logger = LogManager.getLogger(ServerApp.class);

    public static void main(String[] args) {
        final int PORT = 8080;

        if (args.length < 1) {
            logger.error("Please enter a path to json file");
            return;
        }

        String filename = args[0];

        Storage storage = new Storage(filename);
        logger.info("Collection was loaded from file " + filename);

        CommandExecutor executor = new CommandExecutor(storage, filename);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            new SaveCommand(storage, filename);
            logger.info("Server shutdown: collection was saved.");
        }));

        try (DatagramChannel channel = DatagramChannel.open()) {
            channel.bind(new InetSocketAddress(PORT));
            logger.info("Server started on port " + PORT);

            ByteBuffer buffer = ByteBuffer.allocate(8192);

            while (true) {
                buffer.clear();

                SocketAddress clientAdress = channel.receive(buffer);
                buffer.flip();

                byte[] data = new byte[buffer.remaining()];

                buffer.get(data);

                ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
                ObjectInputStream in = new ObjectInputStream(byteStream);
                CommandRequest request = (CommandRequest) in.readObject();

                logger.info("Server received: " + request);

                CommandResponse response = executor.execute(request);

                ByteArrayOutputStream responseBytes = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(responseBytes);
                out.writeObject(response);
                byte[] responseData = responseBytes.toByteArray();

                ByteBuffer responseBuffer = ByteBuffer.wrap(responseData);
                channel.send(responseBuffer, clientAdress);
                logger.info("Server sent response to: " + clientAdress);
            }

        } catch (Exception e) {
            logger.error("Server Error: " + e.getMessage());
        }
    }
}
