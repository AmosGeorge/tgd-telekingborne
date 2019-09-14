import app.AppGame;

public final class Main {

	public static final void main (String [] arguments) {
		String title = "TéléKingdom";
		int width = 1024;
		int height = 768;
		boolean fullscreen = true;
		new AppGame(title, width, height, fullscreen) {

			@Override
			public void init() {
				this.addState (new pages.Welcome (PAGES_WELCOME));
				this.addState (new pages.Choice (PAGES_CHOICE));
				this.addState (new pages.Pause (PAGES_PAUSE));
				this.addState (new pages.Defeat (PAGES_DEFEAT));
				this.addState (new games.telekingdom.World (PAGES_GAME));
			}
		};
	}
}
