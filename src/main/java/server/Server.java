package server;

import java.io.*;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server {
    private static ServerSocket serverSocket;
    private final ArrayList<ClientThread> connectedClients = new ArrayList<>();
    private Config config;


    public static void main(String[] args) {
        Server server = new Server();
        server.launch();
    }

    private void launch() {
        String filePath = "/config.txt";

        System.out.println("Welcome to the server!");
        config = Config.loadFromTxt(filePath);
        System.out.println("Server " + config.getName() + " ready to accept connections with port " + config.getPort());
        System.out.println();

        try {
            //opening a server socket to let clients connect
            serverSocket = new ServerSocket(config.getPort());

            //loop of the server which allows to accept arbitrary number of clients
            while (true) {
                connectedClients.add(new ClientThread(serverSocket.accept(), this));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void removeClient(ClientThread client) {
        connectedClients.remove(client);
        ClientThread.decrementClients();
    }

    //getters & setters
    public ArrayList<String> getBannedPhrases() {
        return config.getBannedPhrases();
    }

    public ArrayList<ClientThread> getConnectedClients() {
        return connectedClients;
    }

    public String getServerName() {
        return config.getName();
    }
}
