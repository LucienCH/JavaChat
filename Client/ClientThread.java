package Client;

import Graphique.InterfaceClient;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Classe permettant de gérer les interactions entre le serveur et le client.
 * Cette classe hérite de la classe Thread pour pouvoir être exécutée dans un thread séparé du client.
 * @see Thread
 * @author Chauvin Lucien
 * @version 1.0
 */
public class ClientThread extends Thread{

    /**
     * Une Hashmap qui associe l'identifiant et le nom du client.
     * @see HashMap
     * @see Integer
     * @see String
     */
    protected HashMap<Integer, String> clientList;

    /**
     * Une Hashmap qui associe l'identifiant et la couleur du client.
     * @see HashMap
     * @see Integer
     * @see Color
     */
    protected HashMap<Integer, Color> clientColors;

    /**
     * Un tableau des polices d'écriture présente sur la machine du client.
     * Ici seul 3 polices sont prise en compte : Arial, Comic Sans MS et Times New Roman.
     * @see String
     */
    private final String[] fontNameList;

    /**
     * Le socket de connexion entre le client et le serveur.
     * @see Socket
     */
    private final Socket socket;

    /**
     * L'interface du client.
     * @see InterfaceClient
     */
    private final InterfaceClient ic;

    /**
     * L'identifiant du client.
     * @see Integer
     */
    private int id;

    /**
     * La police d'écriture par défaut du client.
     * @see String
     */
    private String fontFamily;

    /**
     * Lecteur de texte dans le flux d'entrée provenant du serveur.
     * Ce text est lue par blocs d'une taille fixe et définie par défaut avec BufferReader (8192 caractères).
     * @see BufferedReader
     */
    private BufferedReader reader;

    /**
     * Écrivain qui écrit un text formaté dans le flux de sortie du client vers le serveur.
     * @see PrintWriter
     */
    private PrintWriter writer;

    /**
     * Méthode qui initialise la classe ClientThread
     * @param socket le socket du client
     * @param ic l'interface du client
     * @param font la police par défaut du client (utilisé lors du chargement des sauvegardes)
     */
    public ClientThread(Socket socket, InterfaceClient ic, String font) {
        this.socket = socket;
        this.ic = ic;
        this.clientList = new HashMap<>();
        this.clientColors = new HashMap<>();
        this.fontNameList = new String[6];
        this.fontFamily = font;
        this.reader = null;
        this.writer = null;

    }

    /**
     * Méthode permettant d'initialiser les ActionListeners des différents boutons de l'interface du client.
     * @see java.awt.event.ActionListener
     */
    private void initActionListeners(){

        this.ic.sendButton.addActionListener(e -> {
            keyWordMsg();
            ic.messageArea.setText("");
        });

        this.fontNameList[0] = "Arial";
        this.fontNameList[1] = "ArialMT";
        this.fontNameList[2] = "Comic Sans MS";
        this.fontNameList[3] = "ComicSansMS";
        this.fontNameList[4] = "Times New Roman";
        this.fontNameList[5] = "TimesNewRomanPSMT";

        Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();

        for(Font f : fonts){
            for (String fName: fontNameList){
                if(f.getFontName().equals(fName)){
                    JMenuItem jmi = new JMenuItem(f.getFontName());
                    jmi.addActionListener(e -> {
                        fontFamily = f.getFontName();
                        printCli();
                    });
                    this.ic.fontList.add(jmi);
                }
            }
        }
        for (JMenuItem jmi: this.ic.fontList) {
            this.ic.fontMenu.add(jmi);
        }

        this.ic.saveMenuItem.addActionListener(e -> saveParam());

        this.ic.saveChatMenuItem.setEnabled(true);

        this.ic.saveChatMenuItem.addActionListener(e -> saveChat());

        this.ic.docMenuItem.addActionListener(e -> openDoc());



    }

    /**
     * Méthode appelée par le client et qui s'exécute dans un nouveau Thread.
     * @see Thread
     */
    @Override
    public void run() {

        try {
            this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

            this.writer = new PrintWriter(this.socket.getOutputStream(), true);

            System.out.println("Connecté sur le serveur ! ");

            initActionListeners();

            while(! socket.isClosed()){

                String rawMessage = reader.readLine();

                if( rawMessage == null ){
                    break;
                }

                switch (cutMessage(rawMessage)) {
                    case 1 -> addMessageToChat(rawMessage);
                    case 2 -> addPrivateMessage(rawMessage);
                    case 3 -> addCli(rawMessage);
                    case 4 -> rmCli(rawMessage);
                    case 5 -> noCli(rawMessage);
                    default -> {
                    }
                }
                setColors();
                /*
                printHashmap();
                printColors();
                */
                printCli();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode qui vérifie la présence de mots-clés dans le message envoyé par le client.
     */
    private void keyWordMsg(){

        String msg = ic.messageArea.getText();
        System.out.println("raw input messageArea : " + msg);
        String finalMessage = msg.replaceAll("\n", "   ");

        if ( Pattern.matches("@.*", finalMessage) ){
            System.out.println("Message privé vers ...");
            sendPrivateMsg(finalMessage);

        }else{
            sendMsg(finalMessage);
        }
    }

    /**
     * Méthode pour formater et envoyer un message dans le chat.
     * @param msg La chaine de caractères saisie par l'utilisateur.
     */
    private void sendMsg(String msg){
        String finalMessage = "!msg:"+this.id+":"+msg;
        System.out.println("final message to send : " + finalMessage);
        this.writer.println(finalMessage);
    }


    /**
     * Méthode pour formater et envoyer un message privé à un autre utilisateur.
     * @param msg La chaine de caractères saisie par l'utilisateur.
     */
    private void sendPrivateMsg(String msg){
        String[] split = msg.split(" ", 2);
        String dest = split[0];
        String fin = split[1];

        String newDest = dest.replace("@", "");
        String finalMessage = "!pv:"+newDest+":"+fin;
        this.writer.println(finalMessage);
    }

    /**
     * Méthode qui vérifie la présence de mots-clés dans les messages reçus.
     * @param rawMessage la chaine de caractères formatée avec les informations de l'émetteur.
     * @return un entier
     */
    private int cutMessage(String rawMessage){
        if ( Pattern.matches("!exit.*", rawMessage) ){
            return -1;
        }else if(Pattern.matches("!msg.*", rawMessage)){
            return 1;
        }else if(Pattern.matches("!pv.*", rawMessage)){
            return 2;
        }else if(Pattern.matches("!newcli.*", rawMessage)){
            return 3;
        }else if(Pattern.matches("!rmcli.*", rawMessage)){
            return 4;
        }else if(Pattern.matches("!errNoCli.*", rawMessage)){
            return 5;
        }
        else{
            return -1;
        }
    }

    /**
     * Méthode qui formate la chaine de caractères à ajouter au chat et concatène le nom de l'émetteur.
     * @param rawMessage le message brut reçu par le client.
     */
    private void addMessageToChat(String rawMessage){

        String[] split = rawMessage.split(":", 3);
        int senderID = Integer.parseInt(split[1]);
        String msg = split[2].replaceAll("   ", "\n");

        if(clientList.containsKey(senderID)){
            String name = clientList.get(senderID);
            Color color = clientColors.get(senderID);
            System.out.println(msg);
            System.out.println("nom : " + name);
            appendToChatPane(msg, name, color, false);
        }
    }

    /**
     * Méthode qui formate la chaine de caractères à ajouter au chat avec un affichage différent, car c'est un message privé.
     * @param rawMessage le message brut reçu par le client.
     */
    private void addPrivateMessage(String rawMessage) {

        String[] split = rawMessage.split(":", 3);
        int senderID = Integer.parseInt(split[1]);
        String msg = split[2].replaceAll("   ", "\n");

        if (clientList.containsKey(senderID)) {
            String name = clientList.get(senderID);
            Color color = clientColors.get(senderID);
            System.out.println(msg);
            System.out.println("nom : " + name);
            appendToChatPane(msg, name, color, true);
        }
    }

    /**
     * Méthode qui ajoute le nom de l'utilisateur dans la section des utilisateurs connectés.
     * @param rawMessage le message brut reçu par le client.
     */
    private void addCli(String rawMessage){

        String[] split = rawMessage.split(":", 3);
        int senderID = Integer.parseInt(split[1]);
        String msg = split[2];

        if(! clientList.containsKey(senderID)){
            clientList.put(senderID, msg);
        }

        if(this.id == 0){
            for (int i : clientList.keySet() ) {
                if(clientList.get(i).equals(ic.getNom())){
                    this.id = i;
                }
            }
        }
    }

    /**
     * Méthode qui supprime le nom de l'utilisateur qui s'est déconnecté.
     * Affiche une phrase de déconnexion au chat pour avertir l'utilisateur.
     * Supprime l'utilisateur de la liste des clients connectés.
     * @param rawMessage le message brut reçu par le client.
     */
    private void rmCli(String rawMessage){

        String[] split = rawMessage.split(":", 3);
        int senderID = Integer.parseInt(split[1]);

        if( clientList.containsKey(senderID)){
            String msg = "Serveur : " + clientList.get(senderID) + " s'est déconnecté(e) ! ";
            appendServerInfoToChatPane(msg);
            clientList.remove(senderID);
        }

    }

    /**
     * Méthode qui affiche un message d'erreur dans le chat lorsqu'un message privé est envoyé à un utilisateur inconnu.
     * @param rawMessage le message brut reçu par le client.
     */
    private void noCli(String rawMessage){

        String[] split = rawMessage.split(":", 2);
        String name = split[1];
        String finalMessage = "Le client : " + name + " n'existe pas sur le serveur.";
        appendServerInfoToChatPane(finalMessage);
    }

    /**
     * Méthode qui appelle pour chaque utilisateur de la liste, la méthode d'affichage sur l'interface des clients connectés.
     */
    private void printCli(){
        ic.connectedTextPane.setText("");
        for(int i : clientList.keySet()){
            appendToConnectedPane(clientList.get(i), clientColors.get(i));
        }
    }

    /**
     * Méthode qui attribue une couleur aux nouveaux clients.
     */
    private void setColors(){
        Random rand = new Random();
        for(int i : clientList.keySet()){
            if( ! clientColors.containsKey(i)){
                int r = rand.nextInt(255);
                int g = rand.nextInt(255);
                int b = rand.nextInt(255);
                clientColors.put(i,new Color(r,g,b));
            }
        }
    }

    /**
     * Méthode qui affiche sur l'interface du client les utilisateurs connectés.
     * @param msg le nom de l'utilisateur.
     * @param c la couleur de l'utilisateur.
     */
    private void appendToConnectedPane(String msg, Color c){

        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aSet = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aSet = sc.addAttribute(aSet, StyleConstants.FontFamily, this.fontFamily);

        Document doc = ic.connectedTextPane.getDocument();
        try {
            doc.insertString(doc.getLength(), msg, aSet);
            doc.insertString(doc.getLength(), "\n", aSet);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

    }

    /**
     * Méthode qui affiche le message dans la section discussion de l'interface graphique.
     * @param msg Le message saisi par un des utilisateurs.
     * @param name Le nom de l'émetteur du message.
     * @param c La couleur de l'émetteur du message.
     * @param italic Un booléen pour mettre ou non le message en italique.
     */
    private void appendToChatPane(String msg, String name, Color c, boolean italic){
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet colored = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
        colored = sc.addAttribute(colored, StyleConstants.FontSize, 13);
        colored = sc.addAttribute(colored, StyleConstants.FontFamily, this.fontFamily);

        AttributeSet black = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.black);
        black = sc.addAttribute(black, StyleConstants.FontSize, 13);
        black = sc.addAttribute(black, StyleConstants.FontFamily, this.fontFamily);


        if(italic){
            colored = sc.addAttribute(colored, StyleConstants.Italic, true);
            black = sc.addAttribute(black, StyleConstants.Italic, true);
        }

        Document doc = ic.chatTextPane.getDocument();

        try {
            doc.insertString(doc.getLength(), name + " : ", colored);
            doc.insertString(doc.getLength(), msg, black);
            doc.insertString(doc.getLength(), "\n", black);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode qui affiche les messages d'erreur ou de déconnexion provenant du serveur dans la section discussion de l'interface graphique.
     * @param msg le message du serveur.
     */
    private void appendServerInfoToChatPane(String msg){
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet colored = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, new Color(102,103,171));
        colored = sc.addAttribute(colored, StyleConstants.FontFamily, this.fontFamily);
        colored = sc.addAttribute(colored, StyleConstants.Italic,true);

        Document doc = ic.chatTextPane.getDocument();

        try {
            doc.insertString(doc.getLength(), msg, colored);
            doc.insertString(doc.getLength(), "\n", colored);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode qui sauvegarde, dans un fichier, les paramètres de l'utilisateur pour faciliter la connexion.
     */
    private void saveParam(){

        Path path = Paths.get(System.getProperty("user.dir") + "/.Save");

        try {

            if(! Files.exists(path)){
                Files.createDirectory(path);
            }else{
                System.out.println("Le dossier existe deja on remplace la sauvegarde");
            }
            File saveParamFile = new File(path + "/"+this.ic.getNom()+".txt");

            boolean fileCreated = saveParamFile.createNewFile();

            if(! fileCreated && ! saveParamFile.exists() ){
                System.out.println("Erreur lors de la création du fichier de sauvegarde");
            }else{
                FileWriter fw = new FileWriter(saveParamFile.getPath());
                String stringToSave = "name:"+this.ic.getNom()+":ip:"+this.ic.getIP()+":port:"+this.ic.getPort()+":font:"+this.fontFamily;
                fw.write(stringToSave);
                fw.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode qui permet de sauvegarder sous forme de texte brut le chat du serveur dans un fichier .txt.
     */
    private void saveChat() {
        JFileChooser fc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Fichier text", "txt", "text");

        fc.setDialogTitle("Sauvegarde du chat :");
        fc.setApproveButtonText("Sauvegarder");
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fc.setFileFilter(filter);

        int i = fc.showSaveDialog(null);

        if (i == JFileChooser.APPROVE_OPTION) {
            System.out.println("Fichier ou dossier séléctionné " +fc.getSelectedFile().getPath());

            File saveChatFile = new File(fc.getSelectedFile().getPath() + ".txt");

            try {
                FileWriter fw = new FileWriter(saveChatFile.getPath());
                String stringToSave = this.ic.chatTextPane.getText();
                fw.write(stringToSave);
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Méthode qui affiche dans la console la liste de client.
     */
    private void printHashmap(){
        for(int i : clientList.keySet()){
            System.out.println("hashmap : Key : "+ i + " nom : " +clientList.get(i));
        }
    }

    /**
     * Méthode qui affiche dans la console la liste des couleurs des clients.
     */
    private void printColors(){
        for(int i : clientColors.keySet()){
            System.out.println("hashmap : Key : "+ i + " color : " +clientColors.get(i));
        }
    }

    /**
     * Méthode qui permet d'ouvrire la documentation du code.
     */
    private void openDoc(){

        File doc = new File(System.getProperty("user.dir") + "/Doc/index.html");
        System.out.println("doc path : " +doc.getPath());

        if(doc.exists()){

            try {

                Desktop.getDesktop().open(doc);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("La documentation n'existe pas ...");
        }
    }

    /**
     * Méthode permettant de fermer la connexion entre le client et le serveur.
     * @throws IOException si jamais la connexion ne peut pas être fermée.
     */
    public void disconnect() throws IOException{
        this.socket.close();
        System.exit(0);
    }
}
