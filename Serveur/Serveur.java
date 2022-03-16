package Serveur;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Serveur est la classe représentant le serveur sur lequel les clients se connectent.
 * Cette classe Serveur contient la méthode main de la partie serveur.
 * @author Chauvin Lucien
 * @version 1.0
 */
public class Serveur {

    /**
     * Méthode permettant de rendre la classe executable.
     * @param args un tableau de chaînes de caractères.
     */
    public static void main(String[] args) {

        ArrayList<ServeurThread> serveurThreadList = new ArrayList<ServeurThread>();
        HashMap<Integer, String> serveurUserList = new HashMap<>();

        try (ServerSocket serverSocket = new ServerSocket(54000)){
            System.out.println("Serveur prêt en attente de connexion .... ");

            while(true){
                Socket socket = serverSocket.accept();

                System.out.println("Connexion acceptée : " +socket.toString());
                System.out.println(socket);

                String name = getSocketInfo(socket);

                Random rd = new Random();
                int id = rd.nextInt();

                ServeurThread st = new ServeurThread(socket, serveurThreadList, id, name, serveurUserList);
                serveurThreadList.add(st);
                serveurUserList.put(id, name);
                System.out.println("Nombre clients serveur : " + serveurThreadList.size());
                st.start();
            }
        } catch (IOException e) {
            System.out.println("Erreur dans Serveur main " + e.getStackTrace() );
        }
    }

    /**
     * Méthode qui attends le message contenant le nom du client connecté.
     * @param socket le socket de connexion du serveur vers le client.
     * @return le nom du client.
     * @throws IOException si getInputStreamReader() retourne une erreur.
     */
    private static String getSocketInfo(Socket socket) throws IOException{
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String name = in.readLine();
        System.out.println("Name : " + name);
        return name;
    }

}
