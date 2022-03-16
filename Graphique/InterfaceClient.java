package Graphique;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;

/**
 * InterfaceClient est une classe concrète de la classe mère Interface
 * Cette classe permet de créer une interface graphique pour la partie client.
 * @author Chauvin Lucien
 * @version 1.0
 * @see Interface
 */
public class InterfaceClient extends Interface{

    /**
     * Méthode qui écoute les changements dans les champs de texte pour valider les paramètres de connexion.
     * @see DocumentListener
     */
    private class FieldListener implements DocumentListener {


        @Override
        public void insertUpdate(DocumentEvent e) {
            testParam();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            testParam();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            testParam();
        }

        /**
         * Méthode qui permet de tester des conditions pour valider les champs de connexion
         */
        private void testParam(){
            if(( nomField.getText().length() > 0 ) && (ipField.getText().length() > 3) && (portField.getText().length() > 3) ){
                connexionButton.setEnabled(true);
            }else{
                connexionButton.setEnabled(false);
            }
        }
    }

    /**
     * Un conteneur générique pour séparer les différentes parties de l'interface.
     * @see JPanel
     */
    private JPanel mainPanel, northPanel, northLeftPanel, northRightPanel, centerLeftPanel, centerRightPanel, centerBotPanel;

    /**
     * Un bouton permettant de démarrer la connexion.
     * @see JButton
     */
    public JButton connexionButton;

    /**
     * Un bouton permettant d'envoyer le message.
     * @see JButton
     */
    public JButton sendButton;

    /**
     * Un bouton permettant de mettre fin à la connexion.
     * @see JButton
     */
    public JButton deconnectBtn;

    /**
     * Un champ de saisie du nom de l'utilisateur
     * @see JTextField
     */
    public JTextField nomField;

    /**
     * Un champ de saisie de l'adresse IP du serveur.
     * @see JTextField
     */
    public JTextField ipField;

    /**
     * Un champ de saisie du port d'écoute du serveur
     * @see JTextField
     */
    public JTextField portField;

    /**
     * Une étiquette affiché sur l'interface
     * @see JLabel
     */
    private JLabel nomLabel, ipLabel, portLabel, connexionLabel, connectedLabel, chatLabel, messageLabel;

    /**
     * Un panneau avec des bares de défilement qui s'affiche dès que nécessaire.
     * @see JScrollPane
     */
    private JScrollPane chatScroll, messageScroll, connectedScroll;

    /**
     * Un champ de saisie du message à envoyer.
     * @see JTextArea
     */
    public JTextArea messageArea;

    /**
     * Un panneau d'affichage de texte
     * @see JTextPane
     */
    public JTextPane chatTextPane, connectedTextPane;

    /**
     * Un Objet définissant la disposition des bordures.
     * @see BorderLayout
     */
    private BorderLayout mainBL;

    /**
     * Un Objet définissant la disposition sous forme de grille du panneau du haut de la fenêtre.
     * @see GridLayout
     */
    private GridLayout northGL;

    /**
     * Une barre de menu pour effectuer différentes actions.
     * @see JMenuBar
     */
    public JMenuBar menuBar;

    /**
     * Un menu déroulant de la barre de menu.
     * @see JMenu
     */
    public JMenu fileMenu, editMenu, linksMenu, fontMenu, loadMenu;

    /**
     * Un composant de la barre de menu.
     * @see JMenuItem
     */
    public JMenuItem saveMenuItem, saveChatMenuItem, exitMenuItem, docMenuItem;


    /**
     * Une liste de JMenuItem pour représenter les polices d'écriture.
     * @see ArrayList
     * @see JMenuItem
     */
    public ArrayList<JMenuItem> fontList;

    /**
     * Méthode d'instance de la classe InterfaceClient. Elle initialise les variables d'instance de cette dernière.
     * @param titre le titre de la fenêtre.
     * @param width La largeur de la fenêtre.
     * @param height La hauteur de la fenêtre.
     */
    public InterfaceClient(String titre, int width, int height) {
        super(titre, width, height);
    }

    /**
     * Méthode permettant de changer les contraintes de disposition du GridBagLayout
     * @param gbc Le gridBagConstraints à modifier
     * @param gridX La disposition sur les colones.
     * @param gridY La disposition sur les lignes.
     * @param gWidth Le nombre de cellules en largeur pour afficher le composant
     * @param gHeight Le nombre de cellules en hauteur pour afficher le composant
     * @param wX Le nombre de cases en largeur qu'il peut prendre.
     * @param wY Le nombre de cases en hauteur qu'il peut prendre.
     */
    private void changerConstraints(GridBagConstraints gbc, int gridX, int gridY,int gWidth, int gHeight, int wX, int wY) {
        gbc.gridx = gridX;
        gbc.gridy = gridY;
        gbc.gridwidth = gWidth;
        gbc.gridheight = gHeight;
        gbc.weightx = wX;
        gbc.weighty = wY;
    }

    /***
     * Méthode qui initialise la barre de menu.
     * @see JMenuBar
     */
    @Override
    public void initMenuBar() {

        menuBar = new JMenuBar();
        fileMenu = new JMenu("Fichier");
        editMenu = new JMenu("Edition");
        linksMenu = new JMenu("Liens");
        fontMenu = new JMenu("Police");

        saveMenuItem = new JMenuItem("Sauvegarder");
        saveMenuItem.setActionCommand("Sauvegarder");
        saveMenuItem.setEnabled(false);

        saveChatMenuItem = new JMenuItem("Sauvegarder Chat");
        saveChatMenuItem.setActionCommand("Sauvegarder Chat");
        saveChatMenuItem.setEnabled(false);

        loadMenu = new JMenu("Charger");
        loadMenu.setActionCommand("Charger");
        loadMenu.setEnabled(false);

        exitMenuItem = new JMenuItem("Quitter");
        exitMenuItem.setActionCommand("Quitter");


        docMenuItem = new JMenuItem("Documentation");
        docMenuItem.setActionCommand("Documentation");

        this.fontList = new ArrayList<>();
        fileMenu.add(saveChatMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(loadMenu);
        fileMenu.add(exitMenuItem);

        editMenu.add(fontMenu);

        linksMenu.add(docMenuItem);
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(linksMenu);

        jFrame.setJMenuBar(menuBar);

    }


    /**
     * Méthode qui initialise la fenêtre graphique de l'interface
     * @see JFrame
     */
    @Override
    public void initFrame() {
        Dimension min = new Dimension();
        min.setSize(this.width, this.height);

        initMenuBar();

        connexionLabel = new JLabel("");
        connexionButton = new JButton("Connexion");
        connexionButton.setEnabled(false);

        deconnectBtn = new JButton("Déconnexion");
        deconnectBtn.setVisible(false);

        nomLabel = new JLabel("nom", SwingConstants.CENTER);
        nomField = new JTextField();
        nomField.setText("");

        ipLabel = new JLabel("IP", SwingConstants.CENTER);
        ipField = new JTextField();
        ipField.setText("");

        portLabel = new JLabel("Port", SwingConstants.CENTER);
        portField = new JTextField();
        portField.setText("");

        connectedLabel = new JLabel("Connectés", SwingConstants.CENTER);

        connectedTextPane = new JTextPane();
        connectedTextPane.setEditable(false);

        connectedScroll = new JScrollPane(connectedTextPane);
        connectedScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        connectedScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        chatLabel = new JLabel("Discussion");
        chatTextPane = new JTextPane();
        chatTextPane.setEditable(false);

        chatScroll = new JScrollPane(chatTextPane);
        chatScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        messageLabel = new JLabel("Message");
        messageArea = new JTextArea();
        messageScroll = new JScrollPane(messageArea);
        messageScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        messageScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        sendButton = new JButton("Envoyer");

        nomField.getDocument().addDocumentListener(new FieldListener());
        portField.getDocument().addDocumentListener(new FieldListener());
        ipField.getDocument().addDocumentListener(new FieldListener());

        mainBL = new BorderLayout();
        northGL = new GridLayout(0,2);
        northGL.setVgap(2);
        northGL.setHgap(2);


        mainPanel = new JPanel(mainBL);
        northPanel = new JPanel(northGL);

        northLeftPanel = new JPanel(new GridBagLayout());
        northRightPanel = new JPanel(new GridBagLayout());

        centerRightPanel = new JPanel(new GridBagLayout());
        centerLeftPanel = new JPanel(new GridBagLayout());

        centerBotPanel = new JPanel();

        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setSize(this.width, this.height);
        jFrame.setLocation((screenSize.width / 2) - (jFrame.getWidth() / 2), screenSize.height / 2 - (jFrame.getHeight() / 2));
        jFrame.setMinimumSize(min);





        /*
         * Positionnement des éléments de la grille
         */
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        changerConstraints(c, 0, 0, 1, 1, 1, 0);
        northLeftPanel.add(nomLabel, c);

        changerConstraints(c, 1, 0, 1, 1, 3, 1);
        northLeftPanel.add(nomField, c);

        changerConstraints(c, 0, 1, 1, 1, 1, 0);
        northLeftPanel.add(ipLabel, c);

        changerConstraints(c, 1, 1, 1, 1, 3, 1);
        northLeftPanel.add(ipField, c);

        changerConstraints(c, 0, 0, 1, 1, 1, 1);
        northRightPanel.add(connexionLabel, c);

        changerConstraints(c, 1, 0, 1, 1, 2, 1);
        northRightPanel.add(deconnectBtn, c);
        northRightPanel.add(connexionButton, c);

        changerConstraints(c, 0, 1, 1, 1, 1, 1);
        northRightPanel.add(portLabel, c);

        changerConstraints(c, 1, 1, 1, 1, 2, 1);
        northRightPanel.add(portField, c);

        northPanel.add(northLeftPanel);
        northPanel.add(northRightPanel);

        c.fill = GridBagConstraints.BOTH;

        changerConstraints(c, 0, 0, 1, 1, 1, 0);
        centerRightPanel.add(chatLabel, c);

        changerConstraints(c, 0, 1, 1, 1, 1, 8);
        centerRightPanel.add(chatScroll, c);

        changerConstraints(c, 0, 2, 1, 1, 1, 0);
        centerRightPanel.add(messageLabel, c);

        changerConstraints(c, 0, 4, 1, 1, 1, 2);
        centerRightPanel.add(messageScroll, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        centerBotPanel.add(sendButton, SwingConstants.CENTER);

        c.fill = GridBagConstraints.BOTH;

        changerConstraints(c, 0, 5, 1, 1, 1, 1);
        centerRightPanel.add(centerBotPanel, c);


        changerConstraints(c, 0, 0, 10, 1, 1, 1);
        centerLeftPanel.add(connectedLabel, c);

        changerConstraints(c, 0, 1, 1, 1, 50, 10);
        centerLeftPanel.add(connectedScroll,c);

        centerLeftPanel.setVisible(false);
        centerRightPanel.setVisible(false);

        mainPanel.add(northPanel, BorderLayout.NORTH);
        mainPanel.add(centerRightPanel, BorderLayout.CENTER);
        mainPanel.add(centerLeftPanel, BorderLayout.WEST);

        mainBL.setHgap(15);
        mainBL.setVgap(15);

        mainPanel.setBorder(new EmptyBorder(5,5,5,5));

        jFrame.setContentPane(mainPanel);
        centerLeftPanel.setPreferredSize(new Dimension( (int) (jFrame.getWidth() * 0.22), mainPanel.getHeight() - northPanel.getHeight() ));

        /*
        System.out.println("MP : height = "+ mainPanel.getHeight());
        System.out.println("MP Width =  "+ mainPanel.getWidth());
        System.out.println("NP Width =  "+ northPanel.getWidth());
        System.out.println("NP Height =  "+ northPanel.getHeight());
        */

        jFrame.setVisible(true);

    }


    /**
     * Méthode qui retourne le nom saisi par l'utilisateur.
     * @return une chaîne de caractère : le nom de l'utilisateur
     */
    public String getNom(){
        return nomField.getText();
    }

    /**
     * Méthode qui retourne l'adresse IP du serveur saisie par l'utilisateur.
     * @return Une chaîne de caractère : l'adresse IP du serveur.
     */
    public String getIP(){
        return ipField.getText();
    }

    /**
     * Méthode qui retourne le port d'écoute du serveur saisi par l'utilisateur.
     * @return Un entier : le port d'écoute du serveur.
     */
    public int getPort(){
        return Integer.parseInt(portField.getText());
    }

    /**
     * Méthode permettant d'afficher les différents panneaux de saisie de message, d'affichage des messages et d'affichage des clients connectés sur le serveur.
     */
    public void setPanelVisible(){
        centerLeftPanel.setVisible(true);
        centerRightPanel.setVisible(true);
        connexionButton.setVisible(false);
        deconnectBtn.setVisible(true);
        ipField.setEditable(false);
        nomField.setEditable(false);
        portField.setEditable(false);
    }

}
