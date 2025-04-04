package ru.lab05.client;

import ru.lab05.commands.CommandRequest;
import ru.lab05.commands.CommandResponse;
import ru.lab05.model.Flat;
import ru.lab05.util.FlatBuilder;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class ClientApp {

    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        System.out.println("Client app started...");

        try (
                DatagramSocket socket = new DatagramSocket();
                Scanner scanner = new Scanner(System.in)
        ) {
            while (true) {
                System.out.print("> ");
                String userInput = scanner.nextLine().trim();

                if (userInput.isEmpty()) continue;
                if (userInput.equals("exit")) {
                    System.out.println("Client app stopped.");
                    break;
                } else if (userInput.equals("save")) {
                    System.out.println("This function is only for server!");
                    continue;
                }
                processCommand(userInput, scanner, socket);
            }
        } catch (Exception e) {
            System.out.println("Client's error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void processCommand(String userInput, Scanner scanner, DatagramSocket socket) throws IOException, ClassNotFoundException {
        String[] inputValues = userInput.split(" ");
        String commandName = inputValues[0];
        Object argument = null;

        if (commandName.equals("add")) {
            argument = FlatBuilder.create(scanner);
        } else if (commandName.equals("execute_script")) {
            if (inputValues.length < 2) {
                System.out.println("Please provide a script file name.");
                return;
            }
            String fileName = inputValues[1];
            executeScript(fileName, socket);
            return;
        } else if (inputValues.length > 1) {
            argument = inputValues[1];
        }

        sendCommandToServer(socket, new CommandRequest(commandName, argument));
    }

    private static void executeScript(String fileName, DatagramSocket socket) {
        File file = new File(fileName);
        if (!file.exists() || !file.canRead()) {
            System.out.println("Script file does not exist or is not readable.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            Scanner scanner = new Scanner(System.in);

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(" ");
                String command = parts[0];
                Object argument = null;

                if (command.equals("exit")) {
                    System.out.println("Exit command in script. Skipping...");
                    continue;
                } else if (command.equals("add")) {
                    argument = FlatBuilder.create(new Scanner(System.in));
                } else if (parts.length > 1) {
                    argument = parts[1];
                }

                sendCommandToServer(socket, new CommandRequest(command, argument));
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error while executing script: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void sendCommandToServer(DatagramSocket socket, CommandRequest request) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteStream);
        out.writeObject(request);

        byte[] sendData = byteStream.toByteArray();

        InetAddress serverAddress = InetAddress.getByName(SERVER_IP);
        DatagramPacket packet = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
        socket.send(packet);

        System.out.println("Request sent: " + request.getCommandName());

        byte[] buffer = new byte[8192];
        DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
        socket.receive(responsePacket);

        ByteArrayInputStream byteInput = new ByteArrayInputStream(responsePacket.getData());
        ObjectInputStream objectInput = new ObjectInputStream(byteInput);
        CommandResponse response = (CommandResponse) objectInput.readObject();

        System.out.println("Server's answer: " + response.getMessage());
    }
}
