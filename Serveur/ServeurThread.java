package Serveur;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Classe permettant de gérer les interactions entre le client et le serveur
 * Cette classe hérite de la classe Thread pour pouvoir être exécutée dans un thread séparé du client.
 * @see Thread
 * @author Chauvin Lucien
 * @version 1.0
 */
public class ServeurThread extends Thread{

    /**
     * Le socket de connexion entre le serveur et le client.
     * @see Socket
     */
    private Socket socket;

    /**
     * Le nom du client affecté au thread
     * @see String
     */
    private String name;

    /**
     * La liste des différentes instances de ServeurThread. Chaque ServerThread représente une connexion avec un client.
     * @see ArrayList
     * @see ServeurThread
     */
    private ArrayList<ServeurThread> threadList;

    /**
     * La liste des identifiants des clients présents sur le serveur.
     * @see HashMap
     * @see Integer
     * @see String
     */
    private HashMap<Integer, String> clientList;

    /**
     * Écrivain qui écrit un text formaté dans le flux de sortie du serveur vers le client.
     * @see PrintWriter
     */
    private PrintWriter writer;

    /**
     * L'identifiant du client de cette connexion
     * @see Integer
     */
    private final int id;

    /**
     * Méthode qui initialise une instance de la classe ServeurThread.
     * @param socket Le socket de connexion entre le serveur et le client.
     * @param threadList La liste des instance de ServeurThread
     * @param id L'identifiant du client de cette connexion
     * @param name Le nom du client de cette connexion
     * @param userList la liste des utilisateurs connectés sur le serveur
     */
    public ServeurThread(Socket socket, ArrayList<ServeurThread> threadList, int id, String name, HashMap<Integer, String> userList){
        this.socket = socket;
        this.threadList = threadList;
        this.id = id;
        this.name = name;
        this.clientList = userList;
    }

    /**
     * Méthode appelée par le serveur et qui s'exécute dans un nouveau Thread.
     * @see Thread
     */
    @Override
    public void run() {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            writer = new PrintWriter(this.socket.getOutputStream(), true);
            clientList.put(this.id, this.name);


            while(! socket.isClosed() ) {
                sendInfoToClient();
                String rawMessage = input.readLine();

                if(rawMessage == null){
                    break;
                }
                switch (cutMessage(rawMessage)) {
                    case 1:
                        sendToClients(rawMessage);
                        break;
                    case 2:
                        sendPrivate(rawMessage);
                        break;
                    case 3:
                        sendInfoToClient();
                        break;
                    default:
                        break;
                }
            }
            closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode qui vérifie la présence de mots-clés dans les messages reçus.
     * @param rawMessage la chaine de caractères formatée avec les informations de l'émetteur.
     * @return un entier
     */
    private int cutMessage(String rawMessage){
        if(Pattern.matches("!msg.*", rawMessage)){
            System.out.println("Message reçu srv : " + rawMessage);
            return 1;
        }else if(Pattern.matches("!pv.*", rawMessage)){
            return 2;
        }else if(Pattern.matches("!addcli.*", rawMessage)){
            System.out.println("Message reçu srv : " + rawMessage);
            return 3;
        }
        else{
            return -1;
        }
    }

    /**
     * Méthode qui envoie une chaine de caractères à tous les clients présents sur le serveur.
     * @param outputString la chaîne de caractère à émettre.
     */
    private void sendToClients(String outputString){
        for(ServeurThread st : threadList){
            st.writer.println(outputString);
        }
    }

    /**
     * Méthode permettant d'envoyer à tous les clients le message d'une nouvelle connexion sur le serveur.
     */
    private void sendInfoToClient(){
        for(int i: clientList.keySet()){
            String msg = "!newcli:"+i+":"+clientList.get(i);
            sendToClients(msg);
        }

    }

    /**
     * Méthode qui formate et envoie le message privé au client concerné.
     * @param rawMessage le message brut reçu par le serveur.
     */
    private void sendPrivate(String rawMessage){

        String split[] = rawMessage.split(":", 3);
        String dest = split[1];
        String fin = split[2];
        boolean clientExist = false;

        for(ServeurThread st : threadList){
            if(st.getClientName().equals(dest)){
                String finalMessage = "!pv:"+id+":"+fin;
                st.writer.println(finalMessage);
                this.writer.println(finalMessage);
                clientExist = true;
            }

        }
        if(! clientExist){
            String err = "!errNoCli:"+dest;
            this.writer.println(err);
        }
    }

    /**
     * Méthode qui ferme la connexion entre le client et le serveur.
     * Cette méthode envoie un message de déconnexion à tous les clients et ferme la connexion.
     * @throws IOException si jamais il est impossible de fermer la connexion.
     */
    private void closeConnection() throws IOException {
        System.out.println("Fin du client : " + this.name);
        String msqQuit = "!rmcli:"+this.id;
        sendToClients(msqQuit);
        this.threadList.remove(this);
        this.clientList.remove(this.id);
        this.socket.close();
    }

    /**
     * Méthode qui permet de récupérer le nom du client.
     * @return le nom du client.
     */
    public String getClientName(){
        return this.name;
    }
}
