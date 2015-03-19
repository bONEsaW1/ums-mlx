package net.pms.plugin.filedetail.opensubtitles.configuration;

import net.pms.PMS;
import net.pms.configuration.BaseConfiguration;
import net.pms.plugin.filedetail.opensubtitles.common.DisplayMode;

public class InstanceConfiguration extends BaseConfiguration {
	
	private final String KEY_SUBTITLE_LANGUAGES = "subtitleLanguages";
	private final String KEY_MAX_NUMBER_OF_SUBTITLES = "maxNumberOfSubtitles";
	private final String KEY_DISPLAY_MODE = "displayMode";

	/**
	 * Gets the subtitle languages.
	 *
	 * @return the subtitle languages
	 */
	public String getSubtitleLanguages() {
		if(!properties.containsKey(KEY_SUBTITLE_LANGUAGES)) {
			setSubtitleLanguages(PMS.getConfiguration().getSubtitlesLanguages());
		}
		
		return (String) properties.get(KEY_SUBTITLE_LANGUAGES);
	}

	/**
	 * Sets the subtitle languages. E.g. 'eng,fra,ita'
	 * See http://en.wikipedia.org/wiki/List_of_ISO_639-2_codes for supported subtitles languages
	 *
	 * @param subtitleLanguages the subtitle languages
	 */
	public void setSubtitleLanguages(String subtitleLanguages) {
		properties.put(KEY_SUBTITLE_LANGUAGES, subtitleLanguages);
	}

	/**
	 * Gets the max number of subtitles if the display mode is folder.
	 *
	 * @return the max number of subtitles
	 */
	public int getMaxNumberOfSubtitles() {
		if(!properties.containsKey(KEY_MAX_NUMBER_OF_SUBTITLES)) {
			setMaxNumberOfSubtitles(50);	
		}
		
		return Integer.parseInt((String) properties.get(KEY_MAX_NUMBER_OF_SUBTITLES));
	}

	/**
	 * Sets the max number of subtitles to show if the display mode is folder.
	 *
	 * @param maxNumberOfSubtitles the max number of subtitles
	 */
	public void setMaxNumberOfSubtitles(int maxNumberOfSubtitles) {
		properties.put(KEY_MAX_NUMBER_OF_SUBTITLES, String.valueOf(maxNumberOfSubtitles));
	}

	/**
	 * Gets the display mode.
	 * 
	 * @return the display mode
	 */
	public DisplayMode getDisplayMode() {
		if(!properties.containsKey(KEY_DISPLAY_MODE)) {
			setDisplayMode(DisplayMode.Folder);
		}
		
		return DisplayMode.valueOf((String) properties.get(KEY_DISPLAY_MODE));
	}

	/**
	 * @param displayMode the display mode to set
	 */
	public void setDisplayMode(DisplayMode displayMode) {
		properties.put(KEY_DISPLAY_MODE, displayMode.toString());
	}

}
