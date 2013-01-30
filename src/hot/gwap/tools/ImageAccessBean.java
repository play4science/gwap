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

import gwap.model.resource.ArtResource;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.servlet.ContextualHttpServletRequest;

/**
 * Responsible for storing and retrieving hashes for images.
 * 
 * @author Fabian Kneißl
 */
@Name("imageAccessBean")
@Scope(ScopeType.APPLICATION)
@BypassInterceptors
@Install
public class ImageAccessBean extends AbstractAccessBean {
	
	private static final long serialVersionUID = 1L;
	private static final int MAX_IMAGE_SIZE=400;
	private static final int MIN_IMAGE_SIZE=100;
	private static ImageAccessBean instance;
	
	protected Map<String, Map<Integer, String>> resizeCache = new ConcurrentHashMap<String, Map<Integer, String>>();
		
	@Override
	protected String hashToUrl(String hash) {
		String basePath = (String) Component.getInstance("internalBasePath");
		return basePath+"seam/resource/image/"+hash+".jpg";
	}

	public static ImageAccessBean getInstance() {
		if (instance == null)
			instance = (ImageAccessBean) Component.getInstance("imageAccessBean");
		return instance;
	}
	
	private BufferedImage resizeImage(BufferedImage image, int width, int height)
	{
		BufferedImage tempimg = new BufferedImage(width, height, image.getType());
		Graphics2D g = tempimg.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(image, 0, 0, width, height, 0, 0, image.getWidth(), image.getHeight(), null);
		g.dispose();
		
		return tempimg;
	}
	
	@Override
	public void getResource(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException, IOException {
		new ContextualHttpServletRequest(request) {
			@Override
			public void process() throws ServletException, IOException {				
				String hash = request.getRequestURI();
				try {
					hash = hash.substring(hash.lastIndexOf("/")+1, hash.lastIndexOf(".jpg"));
					ImageAccessBean imageAccess = ImageAccessBean.getInstance();
					
					String imagePath = imageAccess.getPath(hash);
					if (imagePath==null)
						log.info("Image #0 not found in image list", hash);
					
					String imageDirectory = imagePath.substring(0, imagePath.lastIndexOf("/"));
					
					log.debug("Getting image #0 -> #1", hash, imagePath);

					String resizeParameter=request.getParameter("size");					
					if (resizeParameter!=null)
					{
						log.debug("ResizeParameter found, looking up image #0 in cache", imagePath);
						String resizedImagePath=null;
						
						//Resize image
						try
						{
							Integer size=Integer.parseInt(resizeParameter);
							
							if (size>MAX_IMAGE_SIZE)
								size=MAX_IMAGE_SIZE;

							if (size<MIN_IMAGE_SIZE)
								size=MIN_IMAGE_SIZE;
							
							//Only allow steps of 100 pixels
							size=((int)(size/100))*100;

							//Try to find image in cache
							Map<Integer, String> imageCache=resizeCache.get(imagePath);
							if (imageCache!=null)
							{
								resizedImagePath=imageCache.get(size);
								
								//Rebuild cache if files are missing
								if (resizedImagePath!=null && !new File(resizedImagePath).exists())
									resizedImagePath=null;								
								else
									log.debug("Found image #0 in cache: #1", imagePath, resizedImagePath);
							}
							
							//Resize image
							if (resizedImagePath==null)
							{							
								log.debug("Resizing image #0", imagePath);
								
								BufferedImage image=ImageIO.read(new File(imagePath));
								
								double aspect=(double)image.getWidth()/image.getHeight();
								
								//Resize the image so that the shorter side is "size" pixels long  
								if (image.getHeight() < image.getWidth())								
									image=resizeImage(image, (int)(size*aspect), size);
								else
									image=resizeImage(image, size, (int)(size/aspect));
								
								log.debug("Image #0 resized", imagePath);
									
								String cachePath=imageDirectory+"/CACHE";
								
								//Create Cache directory if necessary
								File cacheDirectory = new File(cachePath);
								if (!cacheDirectory.exists())
								{
									log.debug("Trying to create directory #0", cachePath);
									cacheDirectory.mkdir();
								}

								//Write image to disk
								resizedImagePath=cachePath+"/"+imagePath.substring(imagePath.lastIndexOf("/")+1, imagePath.lastIndexOf(".jpg"))+"_"+size.toString()+".jpg";

								ImageIO.write(image, "jpg", new File(resizedImagePath));
								log.debug("Image #0 written to cache #1", imagePath, resizedImagePath);
								
								//Put image into cache
								imageCache=resizeCache.get(imagePath);
								if (imageCache==null)
								{
									imageCache=new ConcurrentHashMap<Integer, String>();
									resizeCache.put(imagePath, imageCache);
								}
								imageCache.put(size, resizedImagePath);								
								log.debug("Image #0 added to cache", imagePath);
							}
							imagePath=resizedImagePath;
							
							
						}
						catch (Exception e)
						{
							log.info("Error resizing image: #0", e.getMessage());							
						}
					}
					
					response.setContentType("image/jpeg");
					response.setHeader("Content-Disposition", "inline; filename=\"image.jpg\"");
//					response.setHeader("Cache-Control", "no-store"); // browser should use no cache
//					response.setHeader("Pragma", "no-cache");  // do not save on proxy server
					response.setDateHeader("Expires", URL_LIFETIME_SECS);

					log.info("Getting resource: #0 -> #1", hash, imagePath);
					InputStream in = new FileInputStream(imagePath); 	
			    	OutputStream out = response.getOutputStream();
			    	log.debug("Writing stream: #0 -> outputStream", imagePath);
			    	int nrBytes = writeTo(in, out);
			    	log.debug("#0 bytes written", nrBytes);
			    	out.flush();
			    	response.setHeader("Content-Length", ""+nrBytes); 
			    	out.close();
				} catch (Exception e) {
					log.info("getResource(#0): Could not load image: #1 (#2)", hash, e.getMessage(), e.getClass().getName());
					response.sendError(404);
				}
			}
		}.run();
	}
	
	@Override
	public String getResourcePath() {
		return "/image";
	}
	
	public static void setResourceUrl(ArtResource artResource) {
		artResource.setUrl(ImageAccessBean.getInstance().createUrl(artResource.getSource().getUrl() + artResource.getPath()));
	}
	
	public static String getResourceUrl(String filePath) {
		return ImageAccessBean.getInstance().createUrl(filePath);
	}
}
