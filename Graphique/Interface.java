package Graphique;

import javax.swing.*;
import java.awt.*;

/**
 * Interface est la classe abstraite qui permet de créer une interface grahique
 * @author Chauvin Lucien
 * @version 1.0
 */
public abstract class Interface {

    /**
     * La dimension de la taille de l'écran
     * @see Dimension
     */
    protected Dimension screenSize;

    /**
     * La fenêtre de l'interface.
     * @see JFrame
     */
    public JFrame jFrame;

    /**
     * la Taille de la fenêtre.
     * @see Integer
     */
    protected int width, height;

    /**
     * Méthode qui initialise les variables d'instance de la classe.
     * @param titre le titre de la fenêtre.
     * @param width La largeur de la fenêtre.
     * @param height La hauteur de la fenêtre.
     */
    public Interface(String titre, int width, int height){
        this.jFrame = new JFrame(titre);
        this.width = width;
        this.height = height;
        this.screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    }

    /**
     * Méthode abstraite qui initialise la fenêtre.
     */
    public abstract void initFrame();

    /**
     * Méthode abstraite qui initialise la barre de menu.
     */
    public abstract void initMenuBar();

}
