package net.pms.plugin.filedetail.opensubtitles.dlna;

import java.io.IOException;
import java.io.InputStream;

import net.pms.dlna.DLNAResource;
import net.pms.medialibrary.commons.dataobjects.DOVideoFileInfo;
import net.pms.plugin.filedetail.opensubtitles.common.DisplayMode;
import net.pms.plugin.filedetail.opensubtitles.configuration.InstanceConfiguration;

public class OpenSubtitlesDlnaResource extends DLNAResource {
	
	private String displayName;
	private DisplayMode displayMode;
	private DOVideoFileInfo videoFileInfo;

	@Override
	public String getName() {
		return displayName;
	}

	@Override
	public String getSystemName() {
		return displayName;
	}

	@Override
	public long length() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isFolder() {
		return displayMode == DisplayMode.Folder;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	public void setVideo(DOVideoFileInfo videoFileInfo) {
		this.videoFileInfo = videoFileInfo;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setConfiguration(InstanceConfiguration instanceConfiguration) {
		// TODO Auto-generated method stub
		
	}

}
