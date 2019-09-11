package pages;

import java.util.Arrays;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import app.AppMenu;
import app.elements.MenuItem;

import games.telekingdom.World;

public class Choice extends AppMenu {

	public Choice (int ID) {
		super (ID);
	}

	@Override
	public void init (GameContainer container, StateBasedGame game) {
		super.initSize (container, game, 600, 400);
		super.init (container, game);
		this.setTitle ("TeleKingdom");
		this.setSubtitle ("Bienvenue dans votre royaume");
		this.setMenu (Arrays.asList (new MenuItem [] {
			new MenuItem ("Nouvelle partie") {
				public void itemSelected () {
					((World) game.getState (4)).setState (0);
					game.enterState (4, new FadeOutTransition (), new FadeInTransition ());
				}
			},
			new MenuItem ("Charger partie") {
				public void itemSelected () {
					((World) game.getState (4)).loadGame();
					((World) game.getState (4)).setState (0);
					game.enterState (4, new FadeOutTransition (), new FadeInTransition ());
				}
			},
			new MenuItem ("Editeur de cartes") {
				public void itemSelected () {

				}
			},
			new MenuItem ("Quitter") {
				public void itemSelected () {
					container.exit ();
				}
			}
		}));
		this.setHint ("BY TELEGAME DESIGN");
	}

}
