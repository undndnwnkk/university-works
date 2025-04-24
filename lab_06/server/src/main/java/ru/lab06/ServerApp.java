package ru.lab06;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import ru.lab06.command.CommandRequest;
import ru.lab06.command.CommandResponse;
import ru.lab06.core.Storage;
import ru.lab06.network.CommandExecutor;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

public class ServerApp {
    private static final int PORT = 8080;
    private static final int BUFFER_SIZE = 65536;
    private static final Logger logger = (Logger) LogManager.getLogger(ServerApp.class);

    public static void main(String[] args) {
        logger.info("Server has starting...");
        if (args.length == 0) {
            logger.fatal("You didn't wrote a path to file :(");
            return;
        }
        File file = new File(args[0]);

        if (!file.exists()) {
            logger.fatal("File not found: " + args[0]);
            return;
        }

        if (file.isDirectory()) {
            logger.fatal("You wrote path to folder " + args[0]);
            return;
        }

        if (!file.canRead() || !file.canWrite()) {
            logger.fatal("Permission denied on file " +  args[0]);
            return;
        }

        Storage storage = new Storage(args[0], logger);

        try (DatagramChannel channel = DatagramChannel.open()) {
            channel.bind(new InetSocketAddress(PORT));
            channel.configureBlocking(false);

            Selector selector = Selector.open();
            channel.register(selector, SelectionKey.OP_READ);

            logger.info("Server started on port " + PORT);
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

            while(true) {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while(iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    if (key.isReadable()) {
                        buffer.clear();
                        SocketAddress clientAdress = channel.receive(buffer);
                        buffer.flip();

                        try (ObjectInputStream ois = new ObjectInputStream(
                                new ByteArrayInputStream(buffer.array(), 0, buffer.limit())
                        )) {
                            CommandRequest commandRequest = (CommandRequest) ois.readObject();
                            logger.info("Server received command: " + commandRequest.getCommandName());

                            CommandResponse commandResponse = CommandExecutor.execute(commandRequest, storage);

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ObjectOutputStream oos = new ObjectOutputStream(baos);
                            oos.writeObject(commandResponse);
                            oos.flush();

                            ByteBuffer responseBuffer = ByteBuffer.wrap(baos.toByteArray());
                            channel.send(responseBuffer, clientAdress);

                            logger.info("Server sent command: " + commandRequest.getCommandName());

                        } catch (ClassNotFoundException | IOException e) {
                            logger.fatal("Error in request handling");
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.fatal("Error in launching server :(");
        }
    }
}
