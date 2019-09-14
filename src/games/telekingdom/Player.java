package games.telekingdom;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import pages.Defeat;

import app.AppPlayer;

import games.telekingdom.hud.Card;
import games.telekingdom.hud.CardParams;
import games.telekingdom.hud.CardTemplate;
import games.telekingdom.hud.Jauge;

public class Player{

	private List<Jauge> jauges;
	private ArrayList<Card> deck;
	private Card activeCard;	// Carte tirée et affiché

	private Boolean dead;

	private World world;
	private int controllerID;

	public Player(AppPlayer appPlayer, World world) {
		this.world = world;
		this.controllerID = appPlayer.getControllerID();

//		Initialisation des jauges :
		jauges = new ArrayList<Jauge>();	// L'ArrayList des jauges, sera passé à l'interface pour l'affichage

		/*Création et ajout des différentes jauges */
		jauges.add(new Jauge("Argent", "Trop d'argent", "Plus d'argent", "jackpot", "ruine", world,this));
		jauges.add(new Jauge("Reputation", "Trop de réputation", "Plus de réputation", "applause", "huee", world,this));
		jauges.add(new Jauge("Effectif", "Trop d'élèves", "Les élèves se sont enfuis, dommage !", "ouais", "ouh", world, this));

		//on place directement les jauges centrees et separees de 25px
		int n = jauges.size();
		for (int i=0; i<n; i++) {
			jauges.get(i).setX(world.getWidth()/2 - jauges.get(i).getWidth()*n/2 - 25/1280f*world.getWidth()*(n-1)/2 + i*(jauges.get(i).getWidth()+25/1280f*world.getWidth()));
		}
	}

	public void update (GameContainer container, StateBasedGame game, int delta) {
		for (Jauge j : jauges) {
			if (j.isEmptyOrFull()) { // Si le roi est mort
				dead = true;
				((Defeat) game.getState (3)).setSubtitle (j.getEndingMessage());
				world.setState (3);
				game.enterState (3, new FadeOutTransition (), new FadeInTransition ());
			}
		}
	}

	public void render (GameContainer container, StateBasedGame game, Graphics context) {
	}

	public List<Jauge> getJauges() {
		return jauges;
	}

	public Card getActiveCard() {
		return activeCard;
	}

	public ArrayList<Card> getDeck() {
		return deck;
	}

	public Boolean isDead() {
		return dead;
	}

	public void addNextCards() {
		//TODO : Ajouter les prochaines cartes (grâce au choix "state" fait à la carte active)
		CardParams[] nextCards = activeCard.getCardTemplate().getNext((activeCard.getState() > 0) ? 1 : 0); // En paramètre : on caste le booleen en entier pour passer du choix -1 ou 1 à 0 ou 1
//		CardParams[] nextCards = new CardParams[0];

		Card cardToAdd;
		int index = 0;

		for (int i = 0 ; i < nextCards.length ; i++) {	// Parcours des différentes cartes à ajouter
			for (int j = 0 ; j < nextCards[i].getQuantity() ; j ++) {	// Création du nombre de carte du même template
				cardToAdd = new Card(world, nextCards[i].getCardTemplate());

				if (nextCards[i].getZone() >= 0) {	// La carte est placée à partir du début du deck (dans les "zone" premiers % du deck), zone = 0 => carte placée au début du deck (donc sera tirée immédiatement)
					index = (int)(Math.random() *(1 + nextCards[i].getZone() * deck.size()/100));
				} else {	// La carte est placée à partir de la fin du deck (dans les - ("zone"+1) derniers % du deck), zone = -1 => carte placée à la fin du deck
					index = (int) (Math.random() * (nextCards[i].getZone() + 1) + 100 ) * deck.size()/100;
				}

				System.out.println("\n Index d'insertion : " + index + " avec zone = " + nextCards[i].getZone() + " et taille de deck : " + deck.size() + "\n");
				deck.add(index, cardToAdd);
			}
		}
		drawCard();
	}

	public void drawCard() {
		activeCard = deck.remove(0); //Pioche la carte du haut du deck
		activeCard.setPiocheeTrue();
	}

	public void init() { //initialisation du deck et des jauges
		dead = false;

		for (Jauge j : jauges) {
			j.init();
		}

		//Initialisation du deck :
		deck = new ArrayList<Card>();	// Création du deck des cartes
		activeCard = new Card (world, CardTemplate.getItem (0));
//		deck.add(activeCard);	// Ajout de la première carte

		if (!world.isJustLoaded()) {
			activeCard.setPiocheeTrue(); // On pioche la première carte
		}

	}

	public void applyEffects(int[] effet) {
		for (Jauge j : jauges) {
			if(j.getName() == "Reputation")  j.addValeur(effet[1]);
			if(j.getName() == "Argent")  j.addValeur(effet[0]);
			if(j.getName() == "Effectif")  j.addValeur(effet[2]);
		}
	}

	public void setJauges(List<Jauge> jauges) {
		this.jauges = jauges;
	}

	public void setDeck(ArrayList<Card> deck) {
		this.deck = deck;
	}

	public void setActiveCard(Card activeCard) {
		this.activeCard = activeCard;
	}

	public int getControllerID() {
		return this.controllerID;
	}

}
