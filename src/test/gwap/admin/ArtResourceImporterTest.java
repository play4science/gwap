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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gwap.model.Source;
import gwap.wrapper.ImportedArtResource;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Fabian Kneißl
 */
public class ArtResourceImporterTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testUpload() throws Exception {
		ArtResourceImporter importer = new ArtResourceImporter();
		InputStream inputStream = ArtResourceImporterTest.class.getResourceAsStream("/artigo-template.csv");
		Source source = new Source();
		source.setUrl("/tmp/");
		new File("/tmp/image1.jpg").createNewFile();
		new File("/tmp/image2.jpg").createNewFile();
		importer.parse(new InputStreamReader(inputStream), source);
		
		assertEquals(2, importer.getResources().size());
		
		ImportedArtResource r = importer.getResources().get(0);
		assertEquals("image1.jpg", r.getPath());
		assertEquals("img1", r.getExternalId());
		assertEquals("Die Ursprünge von ARTigo", r.getTitle());
		assertEquals("Hubertus", r.getArtistForename());
		assertEquals("Kohle", r.getArtistSurname());
		assertEquals("2004", r.getDateCreated());
		assertEquals("München", r.getLocation());
		assertEquals("Ludwig-Maximilians-Universität München", r.getInstitution());
		assertEquals("Privatbesitz", r.getOrigin());
		assertTrue(r.getEasement());
		
		r = importer.getResources().get(1);
		assertEquals("image2.jpg", r.getPath());
		assertEquals("François", r.getArtistForename());
		assertEquals("Bry", r.getArtistSurname());
	}

}
