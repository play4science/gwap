/*
 * This file is part of gwap
 *
 * Copyright (C) 2013
 * Project play4science
 * Lehr- und Forschungseinheit für Programmier- und Modellierungssprachen
 * Ludwig-Maximilians-Universität München
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package gwap.tools;

import gwap.model.action.Characterization;
import gwap.model.resource.Resource;
import gwap.model.resource.Statement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

/**
 * Manages characterizations and the results of them. Holds a cache of the
 * results to improve timing.
 * 
 * @author Fabian Kneißl
 */
@AutoCreate
@Scope(ScopeType.CONVERSATION)
@Name("characterizationBean")
public class CharacterizationBean implements Serializable {

	/**
	 * Consists of a list of results which are grouped by their value.
	 * 
	 * @author Fabian Kneißl
	 */
	public class ResultForType {
		private List<Pair<Integer, Integer>> result = new ArrayList<Pair<Integer,Integer>>();
		private Integer total = 0;
		/**
		 * Each pair is of the form (value, count) where count is the number 
		 * of occurrences of value.
		 * 
		 * @return list of pairs
		 */
		public List<Pair<Integer, Integer>> getResult() {
			return result;
		}
		public Integer getResult(Integer value) {
			for (Pair<Integer, Integer> p : result) {
				if (p.a.equals(value))
					return p.b;
			}
			return 0;
		}
		public Double getResultAsPercentage(Integer value) {
			if (total == 0)
				return 0.0;
			else
				return 100.0 * getResult(value).doubleValue() / total;
		}
		public Integer getTotal() {
			return total;
		}
		public void addResult(Pair<Integer, Integer> addedResult) {
			result.add(addedResult);
			total += addedResult.b;
		}
	}
	
	private static final long serialVersionUID = 6221630409445440605L;

	@In
	private EntityManager entityManager;
	@Logger
	private Log log;
	
	// ResourceId -> { type -> { (value, count), ... } }
	private Map<Long, Map<Characterization.Name, ResultForType>> resultsByResourceId =
			new HashMap<Long, Map<Characterization.Name, ResultForType>>();
	
	/**
	 * Clears the cache and forces a recomputation of results;
	 */
	public void clearCache() {
		resultsByResourceId.clear();
	}
	
	/**
	 * Retrieves the characterization result for a given Resource and a Characterization Type
	 * 
	 * @param resourceId
	 * @param type name of the characterization
	 * @return
	 */
	public ResultForType getResult(Long resourceId, Characterization.Name type) {
		ResultForType resultForType = getResult(resourceId).get(type);
		if (resultForType == null)
			return new ResultForType();
		else
			return resultForType;
	}
	
	private Map<Characterization.Name, ResultForType> getResult(Long resourceId) {
		if (resultsByResourceId.containsKey(resourceId)) {
			return resultsByResourceId.get(resourceId);
		} else {
			log.info("Calculating characterization results for resource #0", resourceId);
			Query q = entityManager.createNamedQuery("characterization.groupedResults");
			q.setParameter("resourceId", resourceId);
			Map<Characterization.Name, ResultForType> results = new HashMap<Characterization.Name, ResultForType>();
			for (Object[] row : (List<Object[]>)q.getResultList()) {
				Characterization.Name type = (Characterization.Name) row[0];
				Pair<Integer, Integer> result = new Pair<Integer, Integer>((Integer)row[1], ((Long)row[2]).intValue());
				if (!results.containsKey(type))
					results.put(type, new ResultForType());
				results.get(type).addResult(result);
			}
			resultsByResourceId.put(resourceId, results);
			return results;
		}
	}
	
	/**
	 * Builds a list of defined characterizations for the instance type of the resource.
	 * 
	 * @param resource
	 * @return
	 */
	public static List<Characterization> getByResource(Resource resource) {
		List<Characterization> dummies = new ArrayList<Characterization>();
		if (resource instanceof Statement) {
			dummies.add(new Characterization(Characterization.Name.gender));
			dummies.add(new Characterization(Characterization.Name.maturity));
			dummies.add(new Characterization(Characterization.Name.cultivation));
		}
		return dummies;
	}
	
	/**
	 * Checks if the Characterization has a value set. Also performs not-null checks.
	 * 
	 * @param characterization
	 * @return true if the value is != null
	 */
	public static boolean isValueSet(Characterization characterization) {
		return characterization != null && characterization.getValue() != null;
	}
	
	/**
	 * Checks if the Characterization has a value set and the value is 0.
	 * 
	 * @param characterization
	 * @return
	 */
	public static boolean isValueUnknown(Characterization characterization) {
		return isValueSet(characterization) && characterization.getValue() == 0;
	}
	
	/**
	 * Returns the order in which the items of a characterization are displayed.
	 * 
	 * @param characterizationName
	 * @return
	 */
	public Integer[] getAvailableItems(Characterization.Name characterizationName) {
		return new Integer[] { 1, 2, 3, 0 };
	}

	/**
	 * Creates a list as defined in {@link #getByResource(Resource)}.
	 * 
	 * @param resource
	 * @return
	 */
	public static Characterization[] createCharacterizations(Resource resource) {
		List<Characterization> cList = getByResource(resource);
		Characterization[] characterizations = cList.toArray(new Characterization[cList.size()]);
		return characterizations;
	}
	
	/**
	 * Creates a list as defined in {@link #getByResource(Resource)} and
	 * initializes all values with 0.
	 * 
	 * @param resource
	 * @return
	 */
	public static Characterization[] createAndInitializeCharacterizations(Resource resource) {
		Characterization[] characterizations = createCharacterizations(resource);
		for (Characterization c : characterizations)
			c.setValue(0);
		return characterizations;
	}
}
