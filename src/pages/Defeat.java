package pages;

import java.io.File;
import java.util.Arrays;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import app.AppMenu;
import app.elements.MenuItem;

public class Defeat extends AppMenu {

	private static Music music;

	static {
		try {
			Defeat.music = new Music ("musics" + File.separator + "defeat.ogg");
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	public Defeat (int ID) {
		super (ID);
	}

	@Override
	public void init (GameContainer container, StateBasedGame game) {
		super.initSize (container, game, 1024, 768);
		super.init (container, game);
		this.setTitle ("Fin du regne");
		this.setSubtitle ("Le roi est mort, vive le roi !");
		this.setMenu (Arrays.asList (new MenuItem [] {
			new MenuItem ("Quitter") {
				public void itemSelected () {
					game.enterState (1, new FadeOutTransition (), new FadeInTransition ());
				}
			}
		}));
		// this.setHint ("HAVE A SNACK");
	}

	public void play (GameContainer container, StateBasedGame game) {
		music.loop (1, .3f);
	}

	public void stop (GameContainer container, StateBasedGame game) {
		music.stop ();
	}

}
