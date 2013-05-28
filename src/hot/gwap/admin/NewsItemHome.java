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

import gwap.model.NewsItem;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.LocaleSelector;

/**
 * @author Fabian Kneißl
 */
@Name("adminNewsItemHome")
public class NewsItemHome extends EntityHome<NewsItem> {

	private static final int EXPIRE_AFTER_DAYS = 7;
	@In String platform;
	@In LocaleSelector localeSelector;
	@RequestParameter		Long newsItemId;
	
	@Override
	public Object getId() {
		if (newsItemId == null)
			return super.getId();
		else
			return newsItemId;
	}
	
	@Override
	@Begin(join = true)
	public void create() {
		super.create();
		if (!isManaged()) {
			getInstance().setPlatform(platform);
			getInstance().setLanguage(localeSelector.getLanguage());
			getInstance().setDatePublished(new Date());
			Calendar expiry = GregorianCalendar.getInstance();
			expiry.add(Calendar.DATE, EXPIRE_AFTER_DAYS);
			getInstance().setDateExpired(expiry.getTime());
		}
	}
}
