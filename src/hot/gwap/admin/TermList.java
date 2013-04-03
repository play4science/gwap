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

import gwap.model.resource.Term;
import gwap.tools.CustomSourceBean;

import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.framework.EntityQuery;

/**
 * @author Fabian Kneißl
 */
@Name("termList")
public class TermList extends EntityQuery<Term> {

	@In private CustomSourceBean customSourceBean;
	
	public TermList() {
		setEjbql("select t from Term t");
		setOrder("t.tag.name");
	}
	
	@Override
	@Transactional
	public List<Term> getResultList() {
		if (customSourceBean != null && customSourceBean.getCustomized() && getRestrictions().size() == 0) {
			getRestrictions().add(createValueExpression("source.id = #{customSource.id}"));
			refresh();
		}
		return super.getResultList();
	}
}
