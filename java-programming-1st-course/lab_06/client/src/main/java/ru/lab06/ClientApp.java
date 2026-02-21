package ru.lab06;

import ru.lab06.command.CommandRequest;
import ru.lab06.command.CommandResponse;
import ru.lab06.util.FlatBuilder;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class ClientApp {
    private static final int PORT = 8080;
    private static final String HOST = "localhost";
    private static final int BUFFER_SIZE = 65536;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try (DatagramSocket socket = new DatagramSocket()) {
            System.out.println("App is started, write a command :)");

            while (true) {
                String input = scanner.nextLine().trim();
                if(input.isEmpty()) continue;

                String[] parts = input.split("\\s++");
                String commandName = parts[0];
                Object[] commandArgs;

                if (commandName.equals("exit")) {
                    System.out.println("Exiting...");
                    break;
                }

                if (commandName.equals("add")) {
                    commandArgs = FlatBuilder.build();
                } else if(commandName.equals("update")) {
                    if (parts.length < 2) {
                        System.out.println("You don't wrote an id. Try again.");
                        continue;
                    } try {
                        int id = Integer.parseInt(parts[1]);
                        commandArgs = FlatBuilder.updateBuild(id);
                    } catch (NumberFormatException e) {
                        System.out.println("Error. You don't wrote a number. Try again.");
                        continue;
                    }
                } else {
                    commandArgs = new Object[parts.length - 1];
                    System.arraycopy(parts, 1, commandArgs, 0, commandArgs.length);
                }

                CommandRequest request = new CommandRequest(commandName, commandArgs);

                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(request);
                    oos.flush();

                    byte[] bytes = baos.toByteArray();
                    DatagramPacket packet = new DatagramPacket(
                            bytes,
                            bytes.length,
                            InetAddress.getByName(HOST),
                            PORT
                    );

                    socket.send(packet);
                } catch (IOException e) {
                    System.out.println("Error. Try again.");
                }

                try {
                    byte[] buffer = new byte[BUFFER_SIZE];
                    DatagramPacket packet = new DatagramPacket(buffer, BUFFER_SIZE);
                    socket.receive(packet);

                    ObjectInputStream ois = new ObjectInputStream(
                            new ByteArrayInputStream(packet.getData(), 0, packet.getLength())
                    );
                    CommandResponse response = (CommandResponse) ois.readObject();
                    System.out.println(response.getMessage());
                } catch (Exception e) {
                    System.out.println("Error. Service not available. Try again later.");
                }
            }
        } catch (IOException e) {
            System.out.println("Error of client");
        }
    }
}
