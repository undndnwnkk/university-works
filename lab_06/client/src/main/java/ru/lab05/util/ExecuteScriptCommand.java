package ru.lab05.util;

import ru.lab05.commands.CommandRequest;
import ru.lab05.commands.CommandResponse;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class ExecuteScriptCommand {
    private final DatagramSocket socket;
    private final InetAddress address;
    private final int port;
    private final Set<String> executedScripts = new HashSet<>();

    public ExecuteScriptCommand(DatagramSocket socket, InetAddress address, int port) {
        this.socket = socket;
        this.address = address;
        this.port = port;
    }

    public void execute(String filename) {
        if (executedScripts.contains(filename)) {
            System.out.println("Script " + filename + " already executed");
            return;
        }

        executedScripts.add(filename);

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(" ");
                String command = parts[0];
                Object argument = null;

                if (command.equals("exit")) {
                    System.out.println("Exit command executed");
                    return;
                } else if (command.equals("add")) {
                    argument = FlatBuilder.create(new Scanner(System.in));
                } else if (command.equals("execute_script")) {
                    if (parts.length < 2) {
                        System.out.println("Wrong number of arguments");
                        continue;
                    }
                    execute(parts[1]);
                    continue;
                } else if (parts.length > 1) {
                    argument = parts[1];
                }

                CommandRequest request = new CommandRequest(command, argument);

                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(byteStream);
                out.writeObject(request);
                byte[] sendData = byteStream.toByteArray();

                DatagramPacket packet = new DatagramPacket(sendData, sendData.length, address, port);
                socket.send(packet);

                byte[] buffer = new byte[8192];
                DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(responsePacket);

                ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buffer));
                CommandResponse response = (CommandResponse) in.readObject();

                System.out.println(response.getMessage());

            }
        } catch (Exception e) {
            System.out.println("Script " + filename + " not found");
        }
    }
}
