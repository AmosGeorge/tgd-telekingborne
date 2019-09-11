package games.telekingdom.hud;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CardTemplate {

	static private List <CardTemplate> instances;

	static {
		CardTemplate.instances = new ArrayList <CardTemplate> ();
		CardTemplate.load ("/data/cardTemplates.json");
		// CardTemplate.normalize ();
		// CardTemplate.save ("/data/cardTemplates2.json");
		if (CardTemplate.instances.size () == 0) {
			new CardTemplate ();
		}
	}

	static private void load (String filename) {
		if (filename != null && filename.startsWith ("/")) {
			try {
				BufferedReader reader = new BufferedReader (new FileReader (System.class.getResource (filename).getPath ()));
				String json = "";
				String line;
				while ((line = reader.readLine ()) != null) {
					json += line + "\n";
				}
				reader.close ();
				try {
					JSONArray array = new JSONArray (json);
					int size = CardTemplate.instances.size ();
					for (int i = 0, li = array.length (); i < li; i++) {
						CardTemplate cardTemplate = new CardTemplate ();
						try {
							JSONObject object = array.getJSONObject (i);
							try {
								Character character = Character.getItem (object.getInt ("character"));
								if (character != null) {
									cardTemplate.character = character;
								}
							} catch (JSONException error) {}
							try {
								cardTemplate.request = object.getString ("request");
							} catch (JSONException error) {}
							try {
								JSONArray response = object.getJSONArray ("response");
								for (int j = 0, lj = response.length (); j < lj && j < 2; j++) {
									try {
										cardTemplate.response [j] = response.getString (j);
									} catch (JSONException error) {}
								}
							} catch (JSONException error) {}
							try {
								JSONArray effect = object.getJSONArray ("effect");
								for (int j = 0, lj = effect.length (); j < lj && j < 2; j++) {
									try {
										JSONArray option = effect.getJSONArray (j);
										for (int k = 0, lk = option.length (); k < lk && k < 3; k++) {
											try {
												cardTemplate.effect [j] [k] = option.getInt (k);
											} catch (JSONException error) {}
										}
									} catch (JSONException error) {}
								}
							} catch (JSONException error) {}
						} catch (JSONException error) {}
					}
					for (int i = 0, li = array.length (); i < li; i++) {
						try {
							JSONArray next = array.getJSONObject (i).getJSONArray ("next");
							CardTemplate cardTemplate = CardTemplate.getItem (i + size);
							for (int j = 0, lj = next.length (); j < lj && j < 2; j++) {
								try {
									JSONArray option = next.getJSONArray (j);
									CardParams [] cardParams = new CardParams [option.length ()];
									for (int k = 0, lk = option.length (); k < lk; k++) {
										CardTemplate nextCardTemplate = CardTemplate.getItem (size);
										int zone = 0;
										int quantity = 1;
										try {
											JSONArray params = option.getJSONArray (k);
											try {
												int index = params.getInt (0);
												if (index >= 0 || index < li) {
													nextCardTemplate = CardTemplate.getItem (index + size);
												};
											} catch (JSONException error) {}
											try {
												zone = params.getInt (1);
											} catch (JSONException error) {}
											try {
												quantity = params.getInt (2);
											} catch (JSONException error) {}
										} catch (JSONException error) {}
										cardParams [k] = new CardParams (nextCardTemplate, zone, quantity);
									}
									cardTemplate.next [j] = cardParams;
								} catch (JSONException error) {}
							}
						} catch (JSONException error) {}
					}
				} catch (JSONException error) {}
			} catch (IOException error) {}
		}
	}

	static private void save (String filename) {
		if (filename != null && filename.startsWith ("/")) {
			try {
				JSONArray array = new JSONArray ();
				for (CardTemplate cardTemplate: CardTemplate.instances) {
					JSONObject object = new JSONObject ();
					try {
						object.put ("character", Character.getIndex (cardTemplate.character));
					} catch (JSONException error) {}
					try {
						object.put ("request", cardTemplate.request);
					} catch (JSONException error) {}
					JSONArray response = new JSONArray ();
					for (int i = 0; i < 2; i++) {
						response.put (cardTemplate.response [i]);
					}
					try {
						object.put ("response", response);
					} catch (JSONException error) {}
					JSONArray effect = new JSONArray ();
					for (int i = 0; i < 2; i++) {
						JSONArray option = new JSONArray ();
						for (int j = 0; j < 3; j++) {
							option.put (cardTemplate.effect [i] [j]);
						}
						effect.put (option);
					}
					try {
						object.put ("effect", effect);
					} catch (JSONException error) {}
					JSONArray next = new JSONArray ();
					for (int i = 0; i < 2; i++) {
						JSONArray option = new JSONArray ();
						for (int j = 0, l = cardTemplate.next [i].length; j < l; j++) {
							JSONArray params = new JSONArray ();
							params.put (CardTemplate.getIndex (cardTemplate.next [i] [j].getCardTemplate ()));
							params.put (cardTemplate.next [i] [j].getZone ());
							params.put (cardTemplate.next [i] [j].getQuantity ());
							option.put (params);
						}
						next.put (option);
					}
					try {
						object.put ("next", next);
					} catch (JSONException error) {}
					array.put (object);
				}
				String json = "\n";
				try {
					json = array.toString (2).replaceAll ("  ", "\t") + "\n";
				} catch (JSONException error) {}
				BufferedWriter writer = new BufferedWriter (new FileWriter (System.class.getResource (filename).getPath ()));
				writer.write (json);
				writer.close ();
			} catch (IOException error) {}
		}
	}

	static private void normalize () {
		List <CardTemplate> instances = new ArrayList <CardTemplate> ();
		boolean [] isVisited = new boolean [CardTemplate.instances.size ()];
		for (int i = 0, l = CardTemplate.instances.size (); i < l; i++) {
			if (!isVisited [i]) {
				List <CardTemplate> stack = new ArrayList <CardTemplate> ();
				int size;
				isVisited [i] = true;
				stack.add (CardTemplate.instances.get (i));
				while ((size = stack.size ()) != 0) {
					CardTemplate currentCardTemplate = stack.remove (size - 1);
					instances.add (currentCardTemplate);
					for (int j = 1; j >= 0; j--) {
						CardParams cardParams [] = currentCardTemplate.next [j];
						for (int k = cardParams.length - 1; k >= 0; k--) {
							CardTemplate nextCardTemplate = cardParams [k].getCardTemplate ();
							int nextIndex = CardTemplate.getIndex (nextCardTemplate);
							if (!isVisited [nextIndex]) {
								isVisited [nextIndex] = true;
								stack.add (nextCardTemplate);
							}
						}
					}
				}
			}
		}
		CardTemplate.instances = instances;
	}

	static public CardTemplate getItem (int index) {
		return index >= 0 && index < CardTemplate.instances.size () ? CardTemplate.instances.get (index) : null;
	}

	static public int getIndex (CardTemplate item) {
		return item != null ? CardTemplate.instances.indexOf (item) : -1;
	}

	static public int getLength () {
		return CardTemplate.instances.size ();
	}

	private Character character;
	private String request;
	private String [] response;
	private int [] [] effect;
	private CardParams [] [] next;

	private CardTemplate () {
		CardTemplate.instances.add (this);
		this.character = Character.getItem (0); // le personnage effectuant la requête
		this.request = "Hmm..."; // la requête en question
		this.response = new String [] {
			"Non", // la réponse négative à la requête
			"Oui" // la réponse positive à la requête
		};
		this.effect = new int [] [] {
			new int [] {
				0, // l'effet sur la première jauge en cas de réponse négative
				0, // l'effet sur la seconde jauge en cas de réponse négative
				0 // l'effet sur la troisième jauge en cas de réponse négative
			},
			new int [] {
				0, // l'effet sur la première jauge en cas de réponse positive
				0, // l'effet sur la seconde jauge en cas de réponse positive
				0 // l'effet sur la troisième jauge en cas de réponse positive
			}
		};
		this.next = new CardParams [] [] {
			new CardParams [] {}, // les paramètres des modèles de carte à ajouter à la pioche en cas de réponse négative
			new CardParams [] {} // les paramètres des modèles de carte à ajouter à la pioche en cas de réponse positive
		};
	}

	public Character getItem () {
		return this.character;
	}

	public String getRequest () {
		return this.request;
	}

	public String getResponse (int option) {
		return this.response [option];
	}

	public int [] getEffect (int option) {
		return Arrays.copyOf (this.effect [option], this.effect [option].length);
	}

	public CardParams [] getNext (int option) {	//option : gauche = 0, droite = 1
		return Arrays.copyOf (this.next [option], this.next [option].length);
	}

}
