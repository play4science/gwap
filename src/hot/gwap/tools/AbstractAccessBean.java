/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.annotations.Logger;
import org.jboss.seam.log.Log;
import org.jboss.seam.util.RandomStringUtils;
import org.jboss.seam.web.AbstractResource;

/**
 * Responsible for storing and retrieving hashes for images.
 * 
 * @author Fabian Kneißl
 */
public abstract class AbstractAccessBean extends AbstractResource implements Serializable {
	
	protected static final long serialVersionUID = 1L;
	
	protected static final int URL_LIFETIME_SECS = 600; // 10 Minutes
	protected static final int HASH_LENGTH = 40;
	
	@Logger   protected Log log;
	
	// Map<temporary image hash, real path>
	protected Map<String, HashBean> hashMap = new ConcurrentHashMap<String, HashBean>();
	protected List<HashBean> hashList = new ArrayList<HashBean>(); 
	protected Boolean cleanupInProgress = false;
	
	abstract protected String hashToUrl(String hash);
	
	@Override
	public abstract String getResourcePath();

	public String createUrl(String filePath) {
		String hash = createNewHash();
		HashBean value = new HashBean(filePath, hash);
		hashMap.put(hash, value);
		hashList.add(value);
		log.info("createUrl(#0) => #1", filePath, hash);
		return hashToUrl(hash);
	}
	
	public String getPath(String hash) {
		cleanup();
		
		if (hash != null) {
			HashBean hashBean = hashMap.get(hash);
			if (hashBean != null)
				return hashBean.getPath();
		}
		return null;
	}
	
	private void cleanup() {
		synchronized (cleanupInProgress) {
			if (cleanupInProgress) // Check if cleanup is running, if yes, just return
				return;
			else
				cleanupInProgress = true;
		}
		Calendar beforeCal = GregorianCalendar.getInstance();
		beforeCal.add(Calendar.SECOND, -URL_LIFETIME_SECS);
		Date timeout = beforeCal.getTime();
		while (hashList.size() > 0 && 
				timeout.after(hashList.get(0).getDate())) {
			HashBean hashBean = hashList.remove(0);
			hashMap.remove(hashBean.getHash());
		}
		synchronized (cleanupInProgress) {
			cleanupInProgress = false;
		}
	}
	
	private String createNewHash() {
		return RandomStringUtils.randomAlphanumeric(HASH_LENGTH);
	}
	
	@Override
	public abstract void getResource(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException, IOException;
	
	protected static int writeTo(InputStream in, OutputStream out) throws IOException {
		int bytesWritten = 0;
		int bufSize = 1024;  // 1KB
     	byte[] buffer = new byte[bufSize];
     	int n;
		n = in.read(buffer);
		while (!(n < buffer.length)) {
			bytesWritten+=1024;
			out.write(buffer, 0, bufSize);
			out.flush();
			n = in.read(buffer);
		}
		if (n > 0) {
			out.write(buffer, 0, n);
			bytesWritten+=n;
		}
		return bytesWritten;
	}
}