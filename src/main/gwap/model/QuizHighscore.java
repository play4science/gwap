/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.model;


import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.jboss.seam.annotations.Name;

/**
 * Responsible for the Highscore for the Woelfflin-QuizGame
 * 
 * @author Jonas Hoelzler
 */

@NamedQueries({
		@NamedQuery(name = "quizHighscore.allTime", query = "select t from QuizHighscore t ORDER BY score DESC"),
		@NamedQuery(name = "quizHighscore.thisWeek", query = "select t from QuizHighscore t WHERE created > current_date - 7 ORDER BY score DESC"),
		@NamedQuery(name = "quizHighscore.placeThisWeek", query = "select count(*) from QuizHighscore t WHERE score > :score and created > current_date - 7"),
		@NamedQuery(name = "quizHighscore.placeAllTime", query = "select count(*) from QuizHighscore t WHERE score > :score") })
@Entity
@Name("quizHighscore")
public class QuizHighscore implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue
	private Long id;
	
	private Date created;
	private String username;
	private int score;
	private int joker;
	private int question;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getScore() {
		return score;
	}

	public void setJoker(int joker) {
		this.joker = joker;
	}

	public int getJoker() {
		return joker;
	}

	public void setQuestion(int question) {
		this.question = question;
	}

	public int getQuestion() {
		return question;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	// @Override
	// public String toString() {
	// if (person!=null)
	// return getPerson().toString() + " rated " + resourceId + " with " +
	// perceptionPair;
	// else
	// return "The player rated " + resourceId + " with " + perceptionPair;
	// }

}