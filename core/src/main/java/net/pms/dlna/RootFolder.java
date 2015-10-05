package net.pms.dlna;

import java.util.ArrayList;

/**
 * RootFolder implementation used to guarantee backwards compatibility fore.g. plugins accessing Pms.getRoot()
 */
public class RootFolder extends net.pms.medialibrary.dlna.RootFolder {

	/**
	 * The Constructor.
	 */
	public RootFolder() {
		super();
	}

	/**
	 * The Constructor.
	 *
	 * @param tags the tags
	 */
	public RootFolder(ArrayList<String> tags) {
		super(tags);
	}

}
