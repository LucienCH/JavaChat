package Client;
import Graphique.InterfaceClient;
import javax.swing.*;
import java.io.*;
import java.net.Socket;

/**
 * Client est la classe représentant un client sur le serveur.
 * Cette classe Client contient la méthode main de la partie client.
 * @author Chauvin Lucien
 * @version 1.0
 */
public class Client{

    /**
     * Une instance de la classe InterfaceClient.
     * @see InterfaceClient .
     */
    protected InterfaceClient ic;

    /**
     * L'adresse ip du serveur
     * @see String
     */
    private String ip;

    /**
     * Le nom du client.
     * @see String
     */
    private String name;

    /**
     * Le port de connexion du serveur.
     * @see Integer
     */
    private int port;

    /**
     * Le socket de connexion entre le client et le serveur
     * @see Socket .
     */
    protected Socket socket;

    /**
     * La police d'écriture de l'interface.
     * @see String
     */
    protected String font = "Arial";

    /**
     * Méthode de classe qui initialise le client
     */
    public Client(){
       this.ic = new InterfaceClient("JavaChat", 500,600);
       this.ip = "";
       this.name = "";
       this.port = 0;
   }

    /**
     * Méthode permettant de rendre la classe executable.
     * @param args un tableau de chaînes de caractères
     */
    public static void main(String[] args) {
        Client c1 = new Client();
        c1.ic.initFrame();
        c1.clientLauncher();
    }

    /**
     * Méthode permettant de démarrer une connexion entre le client et le serveur.
     * Dès que la connexion est établie une instance de ClientThread est démarrée afin de gérer les
     * interactions entre le serveur et ce client.
     * @see ClientThread
     */
    private void clientLauncher(){

        this.ic.exitMenuItem.addActionListener(e -> {
            System.exit(0);
        });

        this.ic.connexionButton.addActionListener(e -> {

            name = ic.getNom();
            ip = ic.getIP();
            port = ic.getPort();

            try {

                socket = new Socket(ip, port);
                sendName(socket);

                ClientThread ct = new ClientThread(socket, ic, font);
                ct.start();

                ic.setPanelVisible();
                ic.saveMenuItem.setEnabled(true);
                ic.loadMenu.setEnabled(false);

                ic.deconnectBtn.addActionListener(e1 -> {
                    try {
                        ct.disconnect();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        this.ic.loadMenu.setEnabled(testSaveFile());

        this.ic.loadMenu.addActionListener(e -> testSaveFile());
    }




    /**
     * Méthode qui envoie le nom du client au serveur dès que la connexion est établie.
     * @param socket Le socket de la connexion entre le client et le serveur.
     * @throws IOException si la méthode getOuputStream() ne retourne rien
     */
    private void sendName(Socket socket) throws IOException{
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        writer.println(this.name);
    }


    /**
     * Méthode qui vérifie la présence d'un fichier de sauvegarde des paramètres.
     * @return un booléen si le fichier n'existe pas.
     */
    private boolean testSaveFile(){

        File loadFolder = new File(System.getProperty("user.dir") + "/.Save");
        File[] files = loadFolder.listFiles();

        if(files == null || files.length <= 0){
            return false;
        }else{
            for(File f:files){
                String saveName = f.getName().replaceAll(".txt", "");
                System.out.println("Save name : " + saveName);
                JMenuItem jmi = new JMenuItem(saveName);

                jmi.addActionListener(e -> loadParam(f));
                this.ic.loadMenu.add(jmi);
            }
            return true;
        }
    }

    /**
     * Méthode qui charge à partir d'un fichier les paramètres de connexion du client.
     * Ces paramètres sont affectés dans les champs de connexion de l'interface client.
     * @param f le fichier de sauvegarde.
     */
    private void loadParam(File f){
        String name, ip, port, font;

        try {
            BufferedReader br = new BufferedReader(new FileReader(f));

            String info = br.readLine();
            br.close();

            String[] split = info.split(":", 8);
            name = split[1];
            ip = split[3];
            port = split[5];
            font = split[7];

            this.ic.nomField.setText(name);
            this.ic.ipField.setText(ip);
            this.ic.portField.setText(port);
            this.font = font;

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
