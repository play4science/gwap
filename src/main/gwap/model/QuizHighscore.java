/*
 * This file is part of gwap, an open platform for games with a purpose
 *
 * Copyright (C) 2013
 * Project play4science
 * Lehr- und Forschungseinheit für Programmier- und Modellierungssprachen
 * Ludwig-Maximilians-Universität München
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
 * @author Jonas Hölzler
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
