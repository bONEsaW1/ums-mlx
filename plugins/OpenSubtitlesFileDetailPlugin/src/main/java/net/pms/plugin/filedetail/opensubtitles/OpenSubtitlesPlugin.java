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
import net.pms.medialibrary.commons.dataobjects.DOVideoFileInfo;
import net.pms.plugins.FileDetailPlugin;
import net.pms.util.PmsProperties;

/**
 * The Class OpenSubtitlesPlugin.
 */
public class OpenSubtitlesPlugin implements FileDetailPlugin {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(OpenSubtitlesPlugin.class);
	
	/** Resource used for localization. */
	public static final ResourceBundle messages = ResourceBundle.getBundle("net.pms.plugin.filedetail.opensubtitles.lang.messages");
	
	/** Holds only the project version. It's used to always use the maven build number in code */
	private static final PmsProperties properties = new PmsProperties();
	static {
		try {
			properties.loadFromResourceFile("/opensubtitlesfiledetail.properties", OpenSubtitlesPlugin.class);
		} catch (IOException e) {
			logger.error("Could not load opensubtitlesfiledetail.properties", e);
		}
	}
	
	/* (non-Javadoc)
	 * @see net.pms.plugins.PluginBase#getName()
	 */
	@Override
	public String getName() {
		return messages.getString("OpenSubtitlesPlugin.Name");
	}

	/* (non-Javadoc)
	 * @see net.pms.plugins.PluginBase#getVersion()
	 */
	@Override
	public String getVersion() {
		return properties.get("project.version");
	}

	/* (non-Javadoc)
	 * @see net.pms.plugins.PluginBase#getPluginIcon()
	 */
	@Override
	public Icon getPluginIcon() {
		return new ImageIcon(getClass().getResource("/OpenSubtitles_FileDetailPlugin_Icon-32.png"));
	}

	/* (non-Javadoc)
	 * @see net.pms.plugins.PluginBase#getShortDescription()
	 */
	@Override
	public String getShortDescription() {
		return messages.getString("OpenSubtitlesPlugin.ShortDescription");
	}

	/* (non-Javadoc)
	 * @see net.pms.plugins.PluginBase#getLongDescription()
	 */
	@Override
	public String getLongDescription() {
		return messages.getString("OpenSubtitlesPlugin.LongDescription");
	}

	/* (non-Javadoc)
	 * @see net.pms.plugins.PluginBase#getUpdateUrl()
	 */
	@Override
	public String getUpdateUrl() {
		return null;
	}

	/* (non-Javadoc)
	 * @see net.pms.plugins.PluginBase#getWebSiteUrl()
	 */
	@Override
	public String getWebSiteUrl() {
		return "http://www.ps3mediaserver.org/";
	}

	/* (non-Javadoc)
	 * @see net.pms.plugins.PluginBase#initialize()
	 */
	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.pms.plugins.PluginBase#shutdown()
	 */
	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.pms.plugins.PluginBase#getGlobalConfigurationPanel()
	 */
	@Override
	public JComponent getGlobalConfigurationPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.pms.plugins.PluginBase#saveConfiguration()
	 */
	@Override
	public void saveConfiguration() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.pms.plugins.PluginBase#isPluginAvailable()
	 */
	@Override
	public boolean isPluginAvailable() {
		return true;
	}

	/* (non-Javadoc)
	 * @see net.pms.plugins.FileDetailPlugin#isFolder()
	 */
	@Override
	public boolean isFolder() {
		return true;
	}

	/* (non-Javadoc)
	 * @see net.pms.plugins.FileDetailPlugin#getTreeIcon()
	 */
	@Override
	public Icon getTreeIcon() {
		return new ImageIcon(getClass().getResource("/OpenSubtitles_FileDetailPlugin_Icon-16.png"));
	}

	/* (non-Javadoc)
	 * @see net.pms.plugins.FileDetailPlugin#getConfigurationPanel()
	 */
	@Override
	public JPanel getConfigurationPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.pms.plugins.FileDetailPlugin#saveConfiguration(java.lang.String)
	 */
	@Override
	public void saveConfiguration(String saveFilePath) throws IOException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.pms.plugins.FileDetailPlugin#loadConfiguration(java.lang.String)
	 */
	@Override
	public void loadConfiguration(String saveFilePath) throws IOException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.pms.plugins.FileDetailPlugin#setDisplayName(java.lang.String)
	 */
	@Override
	public void setDisplayName(String name) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.pms.plugins.FileDetailPlugin#setVideo(net.pms.medialibrary.commons.dataobjects.DOVideoFileInfo)
	 */
	@Override
	public void setVideo(DOVideoFileInfo videoFileInfo) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.pms.plugins.FileDetailPlugin#getResource()
	 */
	@Override
	public DLNAResource getResource() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.pms.plugins.FileDetailPlugin#isInstanceAvailable()
	 */
	@Override
	public boolean isInstanceAvailable() {
		return true;
	}

}
