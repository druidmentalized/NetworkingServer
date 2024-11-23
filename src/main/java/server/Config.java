package server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Config {
    private int port;
    private String name;
    private ArrayList<String> bannedPhrases;

    private Config() {} //no instantiation possible without information possible

    public static Config loadFromTxt(String filePath) {
        Config config = new Config();
        System.out.println("Loading config from: " + filePath);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(Config.class.getResourceAsStream(filePath)));
            config.port = Integer.parseInt(br.readLine());
            config.name = br.readLine();
            config.bannedPhrases =  new ArrayList<>(Arrays.asList(br.readLine().split(", ")));
            System.out.println("Config successfully loaded");
        }
        catch (IOException e) {
            loadReserved(config);
        }
        return config;
    }

    private static void loadReserved(Config config) {
        System.err.println("Config reading gone wrong!");
        System.err.println("Loading reserved config...");
        config.port = 5058;
        config.name = "UTPProject2";
        config.bannedPhrases = new ArrayList<>(List.of("Dumbass", "Nigger", "Prick", "Asshole",
                "Muslim", "Racism", "Ass", "Fuck", "Damn", "Shit", "Motherfucker", "Penis", "Slut", "Rape"));
    }

    public int getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getBannedPhrases() {
        return bannedPhrases;
    }
}
