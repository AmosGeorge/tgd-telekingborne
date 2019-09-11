package games.telekingdom.hud;

public class CardParams {

	private CardTemplate cardTemplate;
	private int zone;
	private int quantity;

	public CardParams (CardTemplate cardTemplate, int zone, int quantity) {
		this.cardTemplate = cardTemplate; // le modèle de carte
		this.zone = zone; // le pourcentage donnant la taille de la zone de la pioche où insérer les cartes
		this.quantity = quantity; // le nombre de cartes à insérer
	}

	public CardTemplate getCardTemplate () {
		return this.cardTemplate;
	}

	public int getZone () {
		return this.zone;
	}

	public int getQuantity () {
		return this.quantity;
	}

}
