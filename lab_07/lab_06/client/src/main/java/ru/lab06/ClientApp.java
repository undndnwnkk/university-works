package ru.lab06;

import ru.lab06.command.CommandRequest;
import ru.lab06.command.CommandResponse;
import ru.lab06.model.Flat;
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

        System.out.print("Login: ");
        String login = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        try (DatagramSocket socket = new DatagramSocket()) {
            System.out.println("Client started. Type a command.");

            while (true) {
                System.out.print("> ");                // === получение ответа ===

                String input = scanner.nextLine().trim();
                if (input.isEmpty()) continue;

                String[] split = input.split("\\s+", 2);
                String commandName = split[0];
                String argsLine = (split.length > 1) ? split[1] : "";

                if (commandName.equals("exit")) {
                    System.out.println("Exiting...");
                    break;
                }

                Object[] commandArgs;

                switch (commandName) {
                    case "add" -> {
                        Flat flat = FlatBuilder.build();
                        if (flat == null) {
                            System.out.println("Add canceled.");
                            continue;
                        }
                        commandArgs = new Object[]{flat};
                    }

                    case "update" -> {
                        if (argsLine.isEmpty()) {
                            System.out.println("Usage: update <id>");
                            continue;
                        }
                        try {
                            int id = Integer.parseInt(argsLine);
                            Flat flat = FlatBuilder.updateBuild(id);
                            if (flat == null) {
                                System.out.println("Update canceled.");
                                continue;
                            }
                            commandArgs = new Object[]{id, flat};
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid ID format.");
                            continue;
                        }
                    }

                    default -> {
                        commandArgs = argsLine.isEmpty()
                                ? new Object[0]
                                : argsLine.split("\\s+");
                    }
                }

                CommandRequest request = new CommandRequest(commandName, commandArgs, login, password);

                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(request);
                    oos.flush();

                    byte[] bytes = baos.toByteArray();
                    DatagramPacket packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(HOST), PORT);
                    socket.send(packet);
                } catch (IOException e) {
                    System.out.println("Failed to send command: " + e.getMessage());
                    continue;
                }

                try {
                    byte[] buffer = new byte[BUFFER_SIZE];
                    DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
                    socket.receive(responsePacket);

                    ObjectInputStream ois = new ObjectInputStream(
                            new ByteArrayInputStream(buffer, 0, responsePacket.getLength())
                    );
                    CommandResponse response = (CommandResponse) ois.readObject();
                    System.out.println(response.getMessage());

                } catch (Exception e) {
                    System.out.println("Failed to receive response: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Client failed to start: " + e.getMessage());
        }
    }
}
