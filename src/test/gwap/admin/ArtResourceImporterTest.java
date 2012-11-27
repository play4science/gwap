/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
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
 * @author kneissl
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
