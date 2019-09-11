package games.telekingdom.hud;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import games.telekingdom.World;

public class Request {

	private static float WINDOW_WIDTH = 1280f; // la largeur de la fenêtre
	private static float WINDOW_HEIGHT = 720f; // la hauteur de la fenêtre
	private static float BOX_X = 498f; // l'abscisse de la zone de texte
	private static float BOX_Y = 160f; // l'ordonnée de la zone de texte
	private static float BOX_WIDTH = 284f; // la largeur de la zone de texte
	private static float BOX_HEIGHT = 168f; // la hauteur de la zone de texte
	private static float LINE_HEIGHT = .8f; // le ratio de la hauteur de ligne sur la hauteur de fonte

	private int length; // le nombre de lignes de texte
	private String [] lines; // les lignes de texte
	private int [] x; // l'abscisse de chaque ligne de texte
	private int [] y; // l'ordonnée de chaque ligne de texte

	public Request (String description, World world) {
		description = description.replaceAll ("^\\s+|\\s+$", "");
		if (!description.isEmpty ()) {
			float horizontalZoom = world.getWidth () / Request.WINDOW_WIDTH;
			float verticalZoom = world.getHeight () / Request.WINDOW_HEIGHT;
			float width = Request.BOX_WIDTH * horizontalZoom;
			float height = Request.BOX_HEIGHT * verticalZoom;
			String [] words = description.split ("\\s+");
			int length = 1;
			for (int i = 0, j = 1, l = words.length; j < l; j++) {
				String word = words [i] + " " + words [j];
				if (World.font.getWidth (word) < width) {
					words [i] = word;
					words [j] = null;
				} else {
					i = j;
					length++;
				}
			}
			float line = (float) World.font.getHeight (description) * Request.LINE_HEIGHT;
			float x = Request.BOX_X * horizontalZoom + width / 2f;
			float y = Request.BOX_Y * verticalZoom + (height - line * ((float) length - 1f + 1f / Request.LINE_HEIGHT)) / 2f;
			this.length = length;
			this.lines = new String [length];
			this.x = new int [length];
			this.y = new int [length];
			for (int i = 0, j = 0, l = words.length; j < l; j++) {
				if (words [j] != null) {
					this.lines [i] = words [j];
					this.x [i] = (int) (x - (float) World.font.getWidth (this.lines [i]) / 2f);
					this.y [i] = (int) (y + line * (float) i);
					i++;
				}
			}
		}
	}

	public void render (GameContainer container, StateBasedGame game, Graphics context) {
		context.setColor (Color.black);
		context.setFont (World.font);
		for (int i = 0, l = this.length; i < l; i++) {
			context.drawString (this.lines [i], this.x [i], this.y [i]);
		}
	}

}
