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

package gwap.admin;

import gwap.model.GameConfiguration;
import gwap.model.Topic;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.log.Log;

/**
 * @author elearning
 */
@Name("gameConfigurationHome")
public class GameConfigurationHome extends EntityHome<GameConfiguration>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 52097429085092384L;
	@RequestParameter		
	Long gameConfigurationId;
	@In
	private FacesMessages facesMessages;
	@In
	private EntityManager entityManager;
	@In
	private LocaleSelector localeSelector;
	@Logger
	private Log log;

	private Topic selectedTopic; // +getter +setter
	private Long selectedTopicId;
	private List<Topic> selectTopics = null; // +getter
	private List<SelectItem> option; 

	public Topic getSelectedTopic() {
		return selectedTopic;
	}

	public void setSelectedTopic(Topic selectedTopic) {
		this.selectedTopic = selectedTopic;
	}	
	
	public List<SelectItem> getSelectTopics() {
		option = new ArrayList<SelectItem>();
		if (selectTopics == null) {
			Query q = entityManager.createNamedQuery("topic.all");
			selectTopics = q.getResultList();
		}
		for (Topic t : selectTopics){
			option.add(new SelectItem(t.getId(), t.getName()));
		}
		return option;
	}

	@Override
	@Begin(join = true)
	public void create() {
		super.create();
		if (gameConfigurationId == null) {
			getInstance().setBid(1);
			getInstance().setLevel(1);
			getInstance().setRoundDuration(60);
		}
	}

    @Override
	public String persist() {
    	String persist = super.persist();
    	selectedTopic = entityManager.find(Topic.class, selectedTopicId);
    	getInstance().setTopic(selectedTopic);
    	return persist;
	}
	
    @Override
    public String update() {
    	String update = super.update();
    	selectedTopic = entityManager.find(Topic.class, selectedTopicId);
    	getInstance().setTopic(selectedTopic);
    	return update;
    };
	
	@Override
	public Object getId() {
		if (gameConfigurationId == null)
			return super.getId();
		else
			return gameConfigurationId;
	}
	
	private String newGameConfiguration;

	public String getNewGameConfiguration() {
		return newGameConfiguration;
	}

	public void setNewGameConfiguration(String newGameConfiguration) {
		this.newGameConfiguration = newGameConfiguration;
	}

	public Long getSelectedTopicId() {
		return selectedTopicId;
	}

	public void setSelectedTopicId(Long selectedTopicId) {
		this.selectedTopicId = selectedTopicId;
	}
}
