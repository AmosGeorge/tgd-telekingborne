package games.telekingdom.hud;

import java.io.File;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.state.StateBasedGame;

import app.AppLoader;

import games.telekingdom.Player;
import games.telekingdom.World;

public class Jauge {

	/* Déclaration des variables & constantes de toutes les jauges*/

	/* Position d'affichage sur l'écran*/
	private float x = 0;
	private float y;

	/* Nom */
	private String name;

	/* Référence vers le World principal pour en récupérer les variables.
	 * Il se donne lui même en argument lors de la création des jauges */
	private World world;

	/* Taille de l'image de la jauge */
	private int width;
	private int height;

	/* Valeur de la jauge */
	private int valeur;
	private double valeurAffichee;
	private double vitesseJauge;

	/* Image (format Slick) de la jauge (vide et pleine pour en gérer le remplissage) */
	private Image emptySprite;
	private Image fullSprite;

	/* Messages de défaite selon la valeur de la jauge */
	private String endMessageEmpty;
	private String endMessageFull;

	/* Sons additionnels sur l'écran de défaite */
	private Sound endSoundFull;
	private Sound endSoundEmpty;

	public Jauge(String name, String endMessageFull, String endMessageEmpty, String nameSoundFull, String nameSoundEmpty, World w, Player player) {

		this.name = name;
		this.endMessageEmpty = endMessageEmpty;
		this.endMessageFull = endMessageFull;

		this.world = w;
		this.vitesseJauge = 0.01;

		this.valeur = 50;
		this.valeurAffichee = valeur;

		this.emptySprite = AppLoader.loadPicture ("/images/" + name + "_empty.png");
		this.fullSprite = AppLoader.loadPicture ("/images/" + name + ".png");

		try {
			this.endSoundFull = new Sound("res/musics"+File.separator+nameSoundFull+".ogg");
			this.endSoundEmpty = new Sound("res/musics/"+File.separator+nameSoundEmpty+".ogg");
		} catch (SlickException e) {
			e.printStackTrace();
		}

		this.setSize();
	}

	private void setSize() {
		/* met la taille et la position en y de l'image en fonction de la taille de l'ecran */
		float scale = 75/1280f;
		width = (int) (scale*world.getWidth());
		height = width;

		y = 10/1280f*world.getWidth();
	}


	public void update (GameContainer container, StateBasedGame game, int delta) {

		if (valeurAffichee + vitesseJauge * delta < valeur) {
			valeurAffichee += vitesseJauge * delta;
		} else if (valeurAffichee - vitesseJauge * delta > valeur) {
			valeurAffichee -= vitesseJauge * delta;
		} else {
			valeurAffichee = valeur;
		}

	}

	public void render (GameContainer container, StateBasedGame game, Graphics context) {
		//on draw l'image vide
		context.drawImage(emptySprite, x, y, x+width, y+height, 0, 0, emptySprite.getWidth(), emptySprite.getHeight());
		//puis on draw la partie de l'image pleine qui correspond a la valeur de la jauge
		context.drawImage(fullSprite, x, y+Math.abs((float) valeurAffichee-100)/100*height, x+width, y+height, 0, Math.abs((float) valeurAffichee-100)/100*(emptySprite.getHeight()), emptySprite.getWidth(), emptySprite.getHeight());

		//puis on draw le nom de la jauge
		context.setFont(World.fontJauges);
		context.drawString(name, x+width/2-World.fontJauges.getWidth(name)/2, y+height+2/1280f*world.getWidth());
	}


	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setX(float newX) {
		this.x = newX;
	}

	public String getName() {
		return name;
	}

	public String getEndingMessage() {
		if (valeur == 0) {
			endSoundEmpty.play();
			return endMessageEmpty;
		} else {
			endSoundFull.play();
			return endMessageFull;
		}
	}

	public Boolean isEmptyOrFull() {
		return (valeur == 0 || valeur == 100);
	}

	public void addValeur(int v) {
		this.valeur += v;
		if(valeur>100) valeur = 100;
		else if(valeur<0) valeur = 0;
	}

	public int getValeur() {
		return valeur;
	}

	public void init() {
		this.valeur = 50;
	}
}
