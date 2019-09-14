package games.telekingdom;

import java.io.File;

import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import app.AppGame;
import app.AppInput;
import app.AppLoader;
import app.AppWorld;

import games.telekingdom.hud.Interface;

public class World extends AppWorld {

	public static Font fontJauges;
	public static Font font;

	private static Music music;

	static {
		World.fontJauges = AppLoader.loadFont ("/fonts/vt323.ttf", java.awt.Font.BOLD, 16);
		World.font = AppLoader.loadFont ("/fonts/vt323.ttf", java.awt.Font.BOLD, 12);
		try {
			World.music = new Music("musics" + File.separator + "main-theme.ogg");
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	private int width;
	private int height;

	private Interface interf;
	private Player player;

	private boolean justLoaded = false;

	public World (int ID) {
		super(ID);
	}

	@Override
	public void init (GameContainer container, StateBasedGame game) {
		/* Méthode exécutée une unique fois au chargement du programme */
		super.init(container, game);

		this.width = container.getWidth ();
		this.height = container.getHeight ();
	}

	@Override
	public void play(GameContainer container, StateBasedGame game) {
		/* Méthode exécutée une unique fois au début du jeu */
		AppGame appGame = (AppGame) game;
		this.player = new Player(appGame.appPlayers.get(0), this);
		this.interf = new Interface(this,player);

		if (!justLoaded) {
			player.init();
		} else {
			player.getActiveCard().setPiocheeTrue();
		}
		justLoaded = false;
		music.loop(1, .3f);
	}

	@Override
	public void resume(GameContainer container, StateBasedGame game) {
		/* Méthode exécutée lors de la reprise du jeu */
		music.resume();
	}

	@Override
	public void pause(GameContainer container, StateBasedGame game) {
		/* Méthode exécutée lors de la mise en pause du jeu */
		music.pause();
	}

	@Override
	public void stop(GameContainer container, StateBasedGame game) {
		/* Méthode exécutée une unique fois à la fin du jeu */
		music.stop ();
	}

	@Override
	public void update (GameContainer container, StateBasedGame game, int delta) {
		/* Méthode exécutée environ 60 fois par seconde */
		AppInput input = (AppInput) container.getInput ();
		

		interf.update(container, game, delta);
		//interf.addToArgent(-1); //debug
		//player.addToReputation(1); //debug
	}

	@Override
	public void render (GameContainer container, StateBasedGame game, Graphics context) {
		/* Méthode exécutée environ 60 fois par seconde */
		interf.render(container, game, context);
	}

	@Override
	public void poll(GameContainer container, StateBasedGame game, Input user) {
		/* Méthode exécutée environ 60 fois par seconde */
		super.poll(container, game, user);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Player getPlayer() {
		return player;
	}

	public void saveGame() {
		new Save(this);
	}

	public void loadGame() {
		justLoaded=true;
		new Load(this);
	}

	public boolean isJustLoaded() {
		return justLoaded;
	}

}
