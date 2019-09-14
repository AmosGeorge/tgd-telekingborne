package games.telekingdom.hud;


import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.state.StateBasedGame;

import app.AppInput;
import games.telekingdom.World;

public class Card {

	private World w;

	//template de la carte
	private CardTemplate cardTemplate;

	private boolean animGo;
	private boolean animGetOut;
	private boolean animGetIn;

	private double speed;
	private boolean speedPos;
	private double acc;
	private int goal;
	private float tmax;
	private int decalage;

	//dimensions
	private int x;
	private int y;
	private int length;

	//etat de la carte : 0 au milieu, 1 à droite et -1 à gauche
	private int state;
	private boolean piochee;

	//a recuperer dans la base des cartes
	private int[] effet;

	private Request request;

	private Color answerBackground = new Color(200,200,200,150);

	private Sound turn;

	public Card (World world, CardTemplate cardTemplate) {
		this.cardTemplate = cardTemplate;

		w = world;

		state = 0; //on commence carte au milieu

		piochee = false;

		animGo = false;
		animGetOut = false;
		animGetIn = false;

		length = (int) (426/1920f*world.getWidth());

		x = world.getWidth()/2 - length/2;
		y = -length;

		this.request = new Request(this.cardTemplate.getRequest(),world);

		try {
			turn = new Sound("res/musics/turn-page.ogg");
		} catch (SlickException e) {
			e.printStackTrace();
		}

		//test
		//setPiocheeTrue();
	}

	public void update (GameContainer container, StateBasedGame game, int delta) {
		AppInput input = (AppInput) container.getInput();
		int controllerID = w.getPlayer().getControllerID();
		if (piochee) {

			if(animGetIn) {
				getIn(delta);
			} else {
				if (animGo) {
					go(delta);
				} else {
					if (animGetOut) {
						getOut(delta);
					} else {
						if (input.isButtonPressed(AppInput.BUTTON_ZL, controllerID) && !input.isButtonPressed(AppInput.BUTTON_ZR, controllerID)) { //si on appuie sur gauche et pas droite
							if (state==-1) {
								confirmLeft();
							} else {
								shiftLeft();
							}
						}
						if (input.isButtonPressed(AppInput.BUTTON_ZR, controllerID) && !input.isButtonPressed(AppInput.BUTTON_ZL, controllerID)) { //si on appuie sur droite et pas gauche
							if (state==1) {
								confirmRight();
							} else {
								shiftRight();
							}
						}
					}
				}
			}
		}
	}

	public void render (GameContainer container, StateBasedGame game, Graphics context) {
		Image image = this.cardTemplate.getItem ().getImage ();
		context.drawImage(image, x, y, x+length, y+length,0,0,image.getWidth()-1, image.getHeight()-1);
		request.render(container, game, context);
		if (!animGetIn && !animGo && !animGetOut) {
			context.setColor(answerBackground);
			if (state==-1) {
				context.fillRect(x, y, World.font.getWidth(this.cardTemplate.getResponse(0)) + 8, World.font.getHeight(this.cardTemplate.getResponse(0)) + 8);
				context.setColor(Color.black);
				context.setFont(World.font);
				context.drawString(this.cardTemplate.getResponse (0), x+4, y+4);
			} else if (state==1) {
				context.fillRect(x+length-World.font.getWidth(this.cardTemplate.getResponse(1)) - 8, y, World.font.getWidth(this.cardTemplate.getResponse(1)) + 8, World.font.getHeight(this.cardTemplate.getResponse(1)) +8);
				context.setColor(Color.black);
				context.setFont(World.font);
				context.drawString(this.cardTemplate.getResponse (1), x+length-World.font.getWidth(this.cardTemplate.getResponse(1))-4, y+4);
			}
		}
	}


	private void shiftRight() { //on décale la carte à droite
		state += 1;
		decalage = w.getWidth()/8;
		initGo(x,x+decalage);
	}

	private void shiftLeft() { // on décale la carte à gauche
		state -= 1;
		decalage = w.getWidth()/8;
		initGo(x,x-decalage);
	}

	//initGo et go : pour les etats -1, 0 et 1

	public void initGo(int dep, int fin) {
		tmax = 400;
		speed = 2*(fin-dep)/tmax;
		acc = -speed/tmax;
		animGo = true;
		speedPos = (speed >= 0);
		goal = fin;
	}

	public void go(int d) {
		if (speedPos == (speed>0) && x*(speedPos ? 1 : -1) < goal*(speedPos ? 1 : -1)) {
			x+=d*speed;
			speed += d*acc;
		} else {
			x=goal;
			animGo = false;
		}
	}

	//initGetOut et getOut :pour confirmer un choix

	public void initGetOut(int dep, int fin) {
		tmax = 500;
		speed = 2*(fin-dep)/tmax;
		acc = -speed/tmax;
		animGetOut = true;
		speedPos = (speed >= 0);
		goal = fin;
	}

	public void getOut(int d) {
		if (speedPos == (speed>0) && y*(speedPos ? 1 : -1) < goal*(speedPos ? 1 : -1)) {
			y+=d*speed;
			speed += d*acc;
		} else {
			y=goal;
			animGetOut = false;
			w.getPlayer().applyEffects(effet);
			w.getPlayer().addNextCards();
		}

	}

	public void confirmLeft() {
		effet = cardTemplate.getEffect(0);
		initGetOut(y,(int) (w.getHeight()*1.2));
		turn.play();
	}

	public void confirmRight() {
		effet = cardTemplate.getEffect(1);
		initGetOut(y,(int) (w.getHeight()*1.2));
		turn.play();
	}

	public void initGetIn(int dep, int fin, int max) {
		tmax = 700;
		speed = 2*(max-dep)/tmax;
		acc = -speed/tmax;
		animGetIn = true;
		speedPos = (speed >= 0);
		goal = fin;
	}

	public void getIn(int d) {
		if (speed>0 || y>goal) {
			y+=d*speed;
			speed += d*acc;
		} else {
			y=goal;
			animGetIn = false;
		}

	}

	public void setPiocheeTrue() {
		piochee = true;
		initGetIn(y,(int) (343*w.getHeight())/720 , (720-40-length*720/w.getHeight())*w.getHeight()/720);
	}

	public int getState() {
		return state;
	}

	public CardTemplate getCardTemplate() {
		return cardTemplate;
	}
}
