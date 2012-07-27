/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.elearn;


/**
 * @author Kathi Krug
 * 
 */

//@Stateless
//@Name("elearnRandomTermChoice")
public class RandomTermChoice {

/*	@Logger
	protected Log log;
	@In
	protected GameRound gameRound;
	@In
	protected GameConfiguration newElearnGameVariables;

	private Term term;

	@PersistenceContext
	private EntityManager em;

	private List<Term> random;

	@Out
	private Term thisrandom;

	static String antwortenrichtig2;

	static Term chosenterm;

	static boolean timerstarted = false;

	int points = 0;

	private List<Term> random2;
	private List<Term> auswahlausrichtig;
	private List<Term> auswahlausfalsch;
	private Term[] defaktoauswahl;
	private Random zahl = new Random();
	private int zufall = 0;
	private int zufall2 = 0;

	private ArrayList<String> thisselect;
	private ArrayList<String> thisselectnew;

	private int rest;
	private int zeitbonus;

	private Term checkIfAlreadySaid(List<Term> random) {

		if (newElearnGameVariables.getChosentermsnumber2() == 0) {
			return random.get(0);
		} else {
			for (int j = 0; j < random.size(); j++)
				for (int i = 0; i < newElearnGameVariables
						.getChosentermsnumber2(); i++) {
					if (newElearnGameVariables.getChosenterms2()[i]
							.toLowerCase().equals(
									random.get(j).getTermname().toLowerCase())) {
						break;
					}
					if (i + 1 == newElearnGameVariables.getChosentermsnumber2()) {
						return random.get(j);
					}
				}
			return random
					.get(newElearnGameVariables.getChosentermsnumber2() + 1);
		}
	}

	// select termname from terms order by random()
	@Factory("thisrandom")
	public String choose() {
		/
		 * Bestimmte Variablen der injizierten GameVariablen werden zu jeder
		 * angefangenen neuen Runde wieder auf bestimmte Werte gesetzt. So wird
		 * z.B. die erreichte Ansage der vorigen Runde, wieder auf 0 gesetzt.
		 * Sowie auch die Ansage der neuen Runde auf die in der vorigen Runde
		 * gewählten “Nächsten Ansage” gesetzt.
		 /
//		if (newElearnGameVariables.getFirsttime()) {
//			newElearnGameVariables.setChosenterms2(new String[30]);
//		}

//		java.util.Date now = new java.util.Date();
//		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH.mm.ss");
//		String ausgabe = sdf.format(now);
//		log.info("Start: " + ausgabe);
//		log.info("Punkte: " + newElearnGameVariables.getScore());
//		log.info("Momentane Ansage: " + newElearnGameVariables.getAnsage());
//		log.info("Erreichte Ansage in voriger Runde: "
//				+ newElearnGameVariables.getErreichteAnsage());
//		log.info("Momentane Zeit: " + newElearnGameVariables.getZeit());

//		if (newElearnGameVariables.getAlteZeit() == 60) {
//			zeitbonus = 1;
//		} else if (newElearnGameVariables.getAlteZeit() == 45) {
//			zeitbonus = 2;
//		} else if (newElearnGameVariables.getAlteZeit() == 30) {
//			zeitbonus = 3;
//		} else {
//			zeitbonus = 4;
//		}

//		if (gameRound.getNumber() > 3 && gameRound.getNumber() % 3 == 1
//				&& newElearnGameVariables.getChecked() == false) {
//			newElearnGameVariables.setScoreNormal(newElearnGameVariables
//					.getScore()
//					- newElearnGameVariables.getAnsage()
//					* newElearnGameVariables.getLevel() * zeitbonus);
//
//		} else if (newElearnGameVariables.getFirsttime()) {
//			newElearnGameVariables.setFirsttime(false);
//		} else if (newElearnGameVariables.getErreichteAnsage() == newElearnGameVariables
//				.getAnsage()) {
//			// System.out.println("erreicht gleich ansage");
//		} else {
//
//			newElearnGameVariables.setScoreNormal(newElearnGameVariables
//					.getScore()
//					- newElearnGameVariables.getAnsage()
//					* newElearnGameVariables.getLevel()
//					* zeitbonus
//					+ newElearnGameVariables.getErreichteAnsage()
//					* newElearnGameVariables.getLevel() * zeitbonus);
//			// System.out.println("nicht erreicht gleich ansage");
//			newElearnGameVariables.setChecked(false);
//		}

//		newElearnGameVariables.setErreichteAnsage(0);
//		newElearnGameVariables.setAnsage(newElearnGameVariables
//				.getZukunftigeAnsage());
//		newElearnGameVariables.setAlteZeit(newElearnGameVariables.getZeit());

		/
		 * Die injizierte GameRound teilt mit in welcher Runde sich der Anwender
		 * befindet, anHand dieser Information wird mittels einer Query ein
		 * Zufallsterm aus einer bestimmten Gruppe an Termen gewählt. Befindet
		 * man sich z.B. in einer Runde des Level 2. Wird ein Zufallsterm
		 * gewählt der confirmedTerms besitzt (denn ohne sie könnte man ja keine
		 * Punkte erreichen) und der das Rating=2 besitzt, das sind
		 * Mittel-schwierig-eingestufte Begriffe.
		 /
//		if (gameRound.getNumber() <= 10) {
//			newElearnGameVariables.setLevel(1);
//		} else if (gameRound.getNumber() <= 20) {
//			newElearnGameVariables.setLevel(2);
//		} else {
//			newElearnGameVariables.setLevel(3);
//		}
//		Query q = em.createNamedQuery("term.randomByRating");
//		q.setParameter("level", newElearnGameVariables.getLevel());
//		random = q.getResultList();

		setChosenterm(checkIfAlreadySaid(random));
		newElearnGameVariables.setChosentermsSpecial(
				newElearnGameVariables.getChosentermsnumber2(),
				getChosenterm(), newElearnGameVariables.getChosenterms2());
		newElearnGameVariables.setChosentermsnumber2(newElearnGameVariables
				.getChosentermsnumber2() + 1);
		thisrandom = getChosenterm();

		/
		 * Befindet sich der User in einer Palette-Runde (jede dritte Runde des
		 * Spiels) ist auch eine zweite Query in dieser Methode von Bedeutung.
		 * In dieser werden fünf Zufallsterme gewählt, die keine bestätigten
		 * Terme des Fragebegriffs sind. Sie stellen die falschen
		 * Auswahlmöglichkeiten im Palette-Modus dar. Ebenfalls zufällig
		 * ausgewählt werden die richtigen bestätigten Terme des Fragebegriffs
		 * die zur Auswahl stehen.
		 /

		random2 = em
				.createQuery(
						"select p from Term p where p.id not in (select p2c.id from Term p2 join p2.confirmedTerms p2c where p2.id =:termId) and p.id != :termId order by random()")
				.setParameter("termId", getChosenterm().getId())
				.setMaxResults(5).getResultList();

		List<String> zufallsZahlArray = new ArrayList<String>();
		zufallsZahlArray.add("0");
		zufallsZahlArray.add("1");
		zufallsZahlArray.add("2");
		zufallsZahlArray.add("3");
		zufallsZahlArray.add("4");
		zufallsZahlArray.add("5");
		zufallsZahlArray.add("6");
		zufallsZahlArray.add("7");
		zufallsZahlArray.add("8");
		zufallsZahlArray.add("9");
		Collections.shuffle(zufallsZahlArray);

		/
		 * Die Auswahl an Terme wird dann in eine beliebige Reihenfolge geordnet
		 * und die richtigen Terme in einer ArrayList Variable der Game
		 * Variablen gespeichert.
		 /

		auswahlausrichtig = (getChosenterm()).getConfirmedTags();
		auswahlausfalsch = random2;

		thisselect = new ArrayList<String>();

		defaktoauswahl = null;
		defaktoauswahl = new Term[10];

		// fix sobald jeder term mehr als 5 Assoziationen hat
		for (int i = 0; i < 2 * newElearnGameVariables.getAnsage(); i++) {

			if (i % 2 == 0) {
				defaktoauswahl[Integer.parseInt(zufallsZahlArray.get(i))] = auswahlausrichtig
						.get(zahl.nextInt(auswahlausrichtig.size()));
				thisselect.add(zufallsZahlArray.get(i));

			} else {
				defaktoauswahl[Integer.parseInt(zufallsZahlArray.get(i))] = auswahlausfalsch
						.get(zahl.nextInt(auswahlausfalsch.size()));

			}

			newElearnGameVariables.setAntworten(defaktoauswahl);

		}

		/
		 * Das folgende ist nötig, da die selektierte Reihenfolge in der obrigen
		 * for-Schleife genau falsch gespeichert wird
		 /
		Integer thisselectinzahl[] = new Integer[thisselect.size()];
		String thisselectinstringsort[] = new String[thisselect.size()];

		for (int z = 0; z < thisselect.size(); z++) {
			thisselectinzahl[z] = Integer.parseInt(thisselect.get(z));
		}
		java.util.Arrays.sort(thisselectinzahl);

		for (int q = 0; q < thisselect.size(); q++) {
			thisselectinstringsort[q] = thisselectinzahl[q].toString();
		}
		java.util.Arrays.sort(thisselectinzahl);

		thisselectnew = new ArrayList<String>();

		for (int j = 0; j < thisselect.size(); j++) {
			thisselectnew.add(thisselectinstringsort[j]);
		}

		newElearnGameVariables.setRightSelected(thisselectnew);

		log.info("Das ist der Frageterm der momentanen Runde: "
				+ getChosenterm().getTermname());
		newElearnGameVariables.setQuestionTerm(random.get(0));

		return null;
	}

	public void setChosenterm(Term chosenterm) {
		this.chosenterm = chosenterm;
		chosenterm.getConfirmedTags().size();
	}

	public Term getChosenterm() {
		return chosenterm;
	}
*/
}