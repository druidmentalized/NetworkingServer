package server;

import utils.Utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class ClientThread extends Thread {
    private static int clientsCount;
    private final Socket clientSocket;
    private String clientNickname;
    private PrintWriter output;
    private BufferedReader input;
    private final Server server;
    private Deque<String> lastMessages = new LinkedList<>();

    private static int MAX_HISTORY_SIZE = 5;

    ClientThread(Socket clientSocket, Server server) {
        this.clientSocket = clientSocket;
        this.server = server;
        clientsCount++;
        clientNickname = "User" + clientsCount;
        System.out.println("Connected user from " + this.clientSocket.getInetAddress().toString() + ":" + this.clientSocket.getPort());
        this.start();
    }

    @Override
    public void run() {
        try {
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output =  new PrintWriter(clientSocket.getOutputStream(), true);

            //notifying other clients about new one
            updateAllConsoles();

            //getting client's name
            output.println("Welcome to the " + server.getServerName() + " server!");
            output.println("Your current nickname: " + clientNickname + ". In order to change it lookup in the commands section");
            output.println("--------------------------------------------------------------------------------------------");
            showServerRules();
            output.println("--------------------------------------------------------------------------------------------");
            showServerCommands();
            output.println("--------------------------------------------------------------------------------------------");
            showAllConnectedClients();

            //reading messages from the client
            String inputLine;
            while((inputLine = input.readLine()) != null) {
                System.out.println("Message from " + clientNickname + ": " + inputLine);
                processMessage(inputLine);
            }
        }
        catch (IOException e) {
            System.out.println(clientNickname + " from " + this.clientSocket.getInetAddress().toString() + ":" + this.clientSocket.getPort() + " disconnected.");
        }
        finally {
            //removing client from the list
            server.removeClient(this);

            //notifying clients about disconnection of a one
            updateAllConsoles();

            //closing all connections
            try {
                input.close();
                output.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void processMessage(String clientMessage) {
        ArrayList<ClientThread> recipients = null;

        if (!clientMessage.isEmpty()) {
            ArrayList<String> messageParts = new ArrayList<>(List.of(clientMessage.split(" ")));
            //in case message was sent with some command
            if (clientMessage.charAt(0) == '/') {
                String command = messageParts.removeFirst();
                switch (command) {
                    case Commands.help -> {
                        showServerCommands();
                        return;
                    }
                    case Commands.rules -> {
                        showServerRules();
                        return;
                    }
                    case Commands.nickname -> {
                        if (messageParts.isEmpty()) {
                            output.println("You can't set empty nickname!");
                        }
                        else {
                            String newClientNickname = "";
                            while (!messageParts.isEmpty()) {
                                if (newClientNickname.isEmpty()) newClientNickname = messageParts.removeFirst();
                                else newClientNickname += "_" + messageParts.removeFirst();
                            }
                            String tempClientNickname = newClientNickname;
                            if (tempClientNickname.length() > 30) {
                                output.println("Your nickname is too long!");
                            }
                            else if (server.getBannedPhrases().contains(tempClientNickname.toLowerCase())) {
                                output.println("You can't use prohibited words as a nickname!");
                            }
                            else if (server.getConnectedClients().stream().anyMatch(clientThread -> clientThread.clientNickname.equals(tempClientNickname))) {
                                output.println("Nickname like this already exists!");
                            }
                            else {
                                clientNickname = tempClientNickname;
                                output.println("Your nickname was successfully changed to: " + clientNickname);

                                //notifying users about change
                                updateAllConsoles();
                            }
                        }
                        return;
                    }
                    case Commands.bannedWords -> {
                        clientMessage = "Currently prohibited words are: ";
                        clientMessage += server.getBannedPhrases();
                        output.println(clientMessage);
                        return;
                    }
                    case Commands.dm -> {
                        //getting clients names
                        ArrayList<String> clientNames = getNicknames(messageParts);
                        if (clientNames == null) return; //no client names were found -> no message would be sent
                        if (clientNames.contains(clientNickname)) {
                            output.println("You can't message yourself!");
                            return;
                        }

                        //taking only chosen users in the list
                        recipients = new ArrayList<>(server.getConnectedClients().stream().filter(client -> clientNames.contains(client.clientNickname)).toList());

                        if (checkForIncorrectUsernames(clientNames, recipients)) return; //inputted users are not/partially not correct -> no message would be sent
                    }
                    case Commands.em -> {
                        //getting clients names
                        ArrayList<String> clientNames = getNicknames(messageParts);
                        if (clientNames == null) return; //no client names were found -> no message would be sent

                        //filtering the users
                        recipients = new ArrayList<>(List.copyOf(server.getConnectedClients()));
                        recipients.removeIf(client -> clientNames.contains(client.clientNickname) || client == this);
                        if (checkForIncorrectUsernames(clientNames, server.getConnectedClients())) return; //inputted users are not correct -> no message would be sent
                    }
                    default -> {
                        output.println("Not correct command: " + command + ". Check for commands with /help");
                        return;
                    }
                }
            }
            else recipients = server.getConnectedClients();

            //reconstructing the message & checking for banned words
            clientMessage = "";
            while (!messageParts.isEmpty()) {
                if (server.getBannedPhrases().contains(messageParts.getFirst().toLowerCase())) {
                    output.println("Your message contains banned word!");
                    return;
                }
                clientMessage += messageParts.removeFirst() + " ";
            }

            //sending the message
            for (ClientThread client : recipients) {
                String singleClientMessage;

                //adding information into the message from/to
                if (recipients.size() == server.getConnectedClients().size()) {
                    singleClientMessage = "[" + this.clientNickname + " -> Global] " + clientMessage;
                }
                else {
                    singleClientMessage = "[" + this.clientNickname + " -> " + client.clientNickname + "] " + clientMessage;
                    this.addMessageToHistory(singleClientMessage);
                }

                client.addMessageToHistory(singleClientMessage);
            }
            updateAllConsoles();
        }
        else output.println("Your message is empty!");
    }

    private void updateAllConsoles() {
        for (ClientThread client : server.getConnectedClients()) {
            client.updateConsole();
        }
    }

    private void updateConsole() {
        Utility.clearConsole(output);
        showAllConnectedClients();
        showMessagesHistory();
    }

    private void addMessageToHistory(String message) {
        if (lastMessages.size() == MAX_HISTORY_SIZE) {
            lastMessages.pollFirst();
        }
        lastMessages.addLast(message); //adding new message
    }

    private void showMessagesHistory() {
        if (!lastMessages.isEmpty()) {
            output.println("Last messages:");
            for (String message : lastMessages) {
                output.println(message);
            }
        }
    }

    private void showServerRules() {
        output.println("Our server focuses only on polite and respectful communication, so\nwe kindly ask you to follow the rules written below:");
        output.println("1. Be Respectful and Kind");
        output.println("2. Use Appropriate Language");
        output.println("3. Stay On-Topic");
        output.println("4. Protect Privacy");
        output.println("5. No Harassment or Bullying");
        output.println("6. Follow Moderators’ Guidance");
        output.println("7. Enjoy and Contribute Positively");
    }

    private void showServerCommands() {
        output.println("Commands available on the server: ");
        output.println(Commands.help + " -- show this menu");
        output.println(Commands.rules + " -- shows server rules");
        output.println(Commands.bannedWords + " -- shows banned words");
        output.println(Commands.nickname + " <usernickname> -- sets your new nickname");
        output.println(Commands.dm + " <usernickname> -- addressing message to a specific user");
        output.println(Commands.dm + " <usernickname1>, <usernickname2>, <...> -- addressing message to group of specific users");
        output.println(Commands.em + " <usernickname> -- addressing message to everyone else but this user");
        output.println(Commands.em + " <usernickname1>, <usernickname2>, <...> -- addressing message to everyone else but group of specific users");
        output.println("/exit -- leaving the server");
        output.println("Writing plain text will result in messaging using global chat available to all users");
    }

    private void showAllConnectedClients() {
        output.println("Currently connected clients:");
        for (ClientThread client : server.getConnectedClients()) {
            if (client == this) output.println(client.clientNickname + " (you)");
            else output.println(client.clientNickname);
        }
    }

    private ArrayList<String> getNicknames(ArrayList<String> messageParts) {
        ArrayList<String> clientNames = new ArrayList<>();
        boolean commaAtTheEnd = true;

        if (messageParts.size() == 0) {
            output.println("No information were given!");
            return null;
        }

        //getting all the clients
        do {
            if (messageParts.getFirst().charAt(messageParts.getFirst().length() - 1) == ',') {
                clientNames.add(messageParts.getFirst().substring(0, messageParts.getFirst().length() - 1));
                messageParts.removeFirst();
            }
            else {
                clientNames.add(messageParts.removeFirst());
                commaAtTheEnd = false;
            }
        } while (commaAtTheEnd);
        return clientNames;
    }

    private boolean checkForIncorrectUsernames(ArrayList<String> clientNames, ArrayList<ClientThread> recipients) {
        //retrieving all the names that weren't found in the list of clients
        ArrayList<String> unmatchedClientNames = new ArrayList<>(clientNames.stream().filter(name -> recipients.stream().noneMatch(client -> client.clientNickname.equals(name))).toList());

        if (!unmatchedClientNames.isEmpty()) {
            output.println("Can't find " + unmatchedClientNames + "!");
            return true;
        }
        return false;
    }

    public static void decrementClients() {
        clientsCount--;
    }

}
