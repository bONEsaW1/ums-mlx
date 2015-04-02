/*
 * PS3 Media Server, for streaming any medias to your PS3.
 * Copyright (C) 2012  Ph.Waeber
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 2
 * of the License only.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package net.pms.plugin.filedetail.opensubtitles;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.pms.dlna.DLNAResource;
import net.pms.medialibrary.dlna.MediaLibraryRealFile;
import net.pms.plugin.filedetail.opensubtitles.common.DisplayMode;
import net.pms.plugin.filedetail.opensubtitles.configuration.GlobalConfiguration;
import net.pms.plugin.filedetail.opensubtitles.configuration.InstanceConfiguration;
import net.pms.plugin.filedetail.opensubtitles.dlna.OpenSubtitlesDlnaResource;
import net.pms.plugin.filedetail.opensubtitles.gui.GlobalConfigurationPanel;
import net.pms.plugin.filedetail.opensubtitles.gui.InstanceConfigurationPanel;
import net.pms.plugins.FileDetailPlugin;
import net.pms.util.PmsProperties;

/**
 * The Class OpenSubtitlesPlugin.
 */
public class OpenSubtitlesPlugin implements FileDetailPlugin {
	
	/** The Constant logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(OpenSubtitlesPlugin.class);
	
	/** Resource used for localization. */
	public static final ResourceBundle messages = ResourceBundle.getBundle("i18n.messages");
	
	/** Holds only the project version. It's used to always use the Maven build number in code */
	private static final PmsProperties properties = new PmsProperties();
	static {
		try {
			properties.loadFromResourceFile("/opensubtitlesfiledetail.properties", OpenSubtitlesPlugin.class);
		} catch (IOException e) {
			LOGGER.error("Could not load opensubtitlesfiledetail.properties", e);
		}
	}
	
	private InstanceConfigurationPanel instanceConfigurationPanel;
	private InstanceConfiguration instanceConfiguration;

	private GlobalConfigurationPanel globalConfigurationPanel;
	private static final GlobalConfiguration globalConfiguration;
	static {
		globalConfiguration = new GlobalConfiguration();
		try {
			globalConfiguration.load();
		} catch (IOException e) {
			LOGGER.error("Failed to load global configuration", e);
		}
	}
	
	private OpenSubtitlesDlnaResource dlnaResource;
	
	@Override
	public String getName() {
		return messages.getString("OpenSubtitlesPlugin.Name");
	}

	@Override
	public String getVersion() {
		return properties.get("project.version");
	}

	@Override
	public Icon getPluginIcon() {
		return new ImageIcon(getClass().getResource("/OpenSubtitles_FileDetailPlugin_Icon-32.png"));
	}

	@Override
	public String getShortDescription() {
		return messages.getString("OpenSubtitlesPlugin.ShortDescription");
	}

	@Override
	public String getLongDescription() {
		return messages.getString("OpenSubtitlesPlugin.LongDescription");
	}

	@Override
	public String getUpdateUrl() {
		return null;
	}

	@Override
	public String getWebSiteUrl() {
		return "http://www.ps3mediaserver.org/";
	}

	@Override
	public void initialize() {
		instanceConfiguration = new InstanceConfiguration();
		instanceConfigurationPanel = new InstanceConfigurationPanel(instanceConfiguration);
		globalConfigurationPanel = new GlobalConfigurationPanel(globalConfiguration);		
		dlnaResource = new OpenSubtitlesDlnaResource(globalConfiguration, instanceConfiguration);
	}

	@Override
	public void shutdown() { }

	@Override
	public JComponent getGlobalConfigurationPanel() {
		return globalConfigurationPanel;
	}

	@Override
	public void saveConfiguration() {
		globalConfigurationPanel.updateConfiguration(globalConfiguration);
		try {
			globalConfiguration.save();
		} catch (IOException e) {
			LOGGER.error("Failed to save global configuration", e);
		}
	}

	@Override
	public boolean isPluginAvailable() {
		return true;
	}

	@Override
	public boolean isFolder() {
		return instanceConfiguration.getDisplayMode() == DisplayMode.Folder;
	}

	@Override
	public Icon getTreeIcon() {
		return new ImageIcon(getClass().getResource("/OpenSubtitles_FileDetailPlugin_Icon-16.png"));
	}

	@Override
	public JPanel getConfigurationPanel() {
		return instanceConfigurationPanel;
	}

	@Override
	public void saveConfiguration(String saveFilePath) throws IOException {		
		instanceConfiguration = instanceConfigurationPanel.getConfiguration();
		instanceConfiguration.save(saveFilePath);
	}

	@Override
	public void loadConfiguration(String saveFilePath) throws IOException {
		instanceConfiguration.load(saveFilePath);
		
		instanceConfigurationPanel.setConfiguration(instanceConfiguration);
		dlnaResource.setInstanceConfiguration(instanceConfiguration);
	}

	@Override
	public void setDisplayName(String name) {
		dlnaResource.setDisplayName(name);
	}

	@Override
	public DLNAResource getResource() {
		return dlnaResource;
	}

	@Override
	public boolean isInstanceAvailable() {
		return true;
	}

	@Override
	public void setOriginalResource(MediaLibraryRealFile originalResource) {
		dlnaResource.setOriginalResource(originalResource);
	}
}
