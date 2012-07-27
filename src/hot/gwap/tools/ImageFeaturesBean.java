/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.tools;

import gwap.model.resource.ArtResource;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.net.URL;

import javax.imageio.ImageIO;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

@Name("imageFeaturesBean")
@Scope(ScopeType.CONVERSATION)
public class ImageFeaturesBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Create	 public void init()    { log.info("Creating"); updateImageFeatures(); }
	@Destroy public void destroy() { log.info("Destroying"); }

	@Logger                  private Log log;
	@In                      private ArtResource artResource;

	private Integer resourceHeight;
    private Integer resourceWidth;

//	@Observer("updateResource")
	public void updateImageFeatures() {
		log.info("Updating Image Features: #0", artResource.getUrl());
		
		try {
			BufferedImage bufferedImage = ImageIO.read(new URL(artResource.getUrl()));
			
			resourceWidth = bufferedImage.getWidth();
			resourceHeight = bufferedImage.getHeight();
		} catch (Exception e) {
			log.info("Image Features could not be determined");
			// Let the browser decide, if no dimensions could be detected.
			this.resourceHeight = null;
			this.resourceWidth = null;
		}
	}
	
	public Integer getResourceHeight() {
		return resourceHeight;
	}

	public Integer getResourceWidth() {
		return resourceWidth;
	}
}
