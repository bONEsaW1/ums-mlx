package net.pms.plugin.filedetail.opensubtitles.configuration;

import java.io.IOException;
import java.nio.file.Paths;

import net.pms.PMS;
import net.pms.configuration.BaseConfiguration;

public class GlobalConfiguration extends BaseConfiguration {
	
	private final String KEY_SUBTITLE_FOLDER = "subtitleFolder";
	private String propertiesFilePath;
	
	public GlobalConfiguration() {
		propertiesFilePath = Paths.get(getGlobalConfigurationDirectory(), "OpenSubtitlesFileDetailPlugin.conf").toString();
	}

	/**
	 * Save the current configuration.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void save() throws IOException {
			save(propertiesFilePath);
	}

	/**
	 * Load the last saved configuration.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void load() throws IOException {
		load(propertiesFilePath);
	}

	/**
	 * Gets the subtitle folder.
	 *
	 * @return the subtitle folder
	 */
	public String getSubtitleFolder() {
		if(!properties.containsKey(KEY_SUBTITLE_FOLDER)) {
			setSubtitleFolder(Paths.get(PMS.getConfiguration().getProfileDirectory(), "LiveSubtitles").toString());	
		}
		
		return (String) properties.get(KEY_SUBTITLE_FOLDER);
	}

	/**
	 * Sets the subtitle folder.
	 *
	 * @param subtitleFolder the subtitle folder
	 */
	public void setSubtitleFolder(String subtitleFolder) {
		properties.put(KEY_SUBTITLE_FOLDER, subtitleFolder);
	}
}
