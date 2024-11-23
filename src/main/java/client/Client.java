package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Socket serverSocket;
        BufferedReader input;
        PrintWriter output;
        Scanner scanner = new Scanner(System.in);

        String hostname = "localhost";
        int port = 5058;

        try {
            //creating socket & assigning streams
            serverSocket = new Socket(hostname, port);
            input = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            output = new PrintWriter(serverSocket.getOutputStream(), true);

            //making thread in order to read information from the server
            new Thread(() -> {
               String serverMessage;
               try {
                   while ((serverMessage = input.readLine()) != null) {
                       System.out.println(serverMessage);
                   }
               } catch (IOException e) {
                   e.printStackTrace();
               }
            }).start();

            //writing information to the server
            while (scanner.hasNextLine()) {
                if (scanner.nextLine().equals("/exit")) {
                    System.exit(0);
                }
                output.println(scanner.nextLine());
            }
        }
        catch (IOException e) {
            System.err.println("Server's offline");
            System.exit(0);
        }
    }
}
