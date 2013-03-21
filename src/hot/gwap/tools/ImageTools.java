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

package gwap.tools;

import gwap.model.Source;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

/**
 * @author maders, wieser
 */

@Name("imageTools")
public class ImageTools implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@In              private EntityManager entityManager;
	
	/**
	 * Wandelt einen Base64-String wieder in ein Bild um
	 * @param base64: base64-Repraesentation des Bildes
	 * @param sourceId: ID der Sammlung (source)
	 * @param imageId: ID dieses Userpictures
	 * @throws IOException 
	 */
	public void persistImage(String base64, String sourceName, Long imageId) throws IOException {
		byte decoded[] = new sun.misc.BASE64Decoder().decodeBuffer(base64);
		
		Query query = entityManager.createNamedQuery("source.byName");
		query.setParameter("name", sourceName);
		Source source = (Source) query.getSingleResult();
		
		String path = source.getUrl() + imageId + ".jpg";
		FileOutputStream output = new FileOutputStream(path);
		output.write(decoded);
		output.close();
	}
}
