package net.pms.plugins.wrappers;

import java.io.IOException;

import javax.swing.Icon;
import javax.swing.JComponent;

import net.pms.dlna.DLNAResource;
import net.pms.dlna.virtual.VirtualFolder;
import net.pms.external.AdditionalFolderAtRoot;
import net.pms.plugins.DlnaTreeFolderPlugin;
import net.pms.plugins.Plugin;
import net.pms.util.ImagesUtil;

/**
 * Wraps the old style plugin {@link net.pms.external.AdditionalFolderAtRoot} to be used by the new plugin system
 */
@SuppressWarnings("deprecation")
public class AdditionalFolderAtRootWrapper extends BaseWrapper implements Plugin, DlnaTreeFolderPlugin {
	private AdditionalFolderAtRoot additionalFolderAtRoot;
	private String displayName;
	private DLNAResource dlnaResource;

	/**
	 * The Constructor.
	 *
	 * @param additionalFolderAtRoot the additional folder at root
	 */
	public AdditionalFolderAtRootWrapper(AdditionalFolderAtRoot additionalFolderAtRoot) {
		super(additionalFolderAtRoot);
		setAdditionalFolderAtRoot(additionalFolderAtRoot);
	}
	
	/**
	 * The Constructor.
	 */
	public AdditionalFolderAtRootWrapper() { }

	/**
	 * Gets the folder.
	 *
	 * @return the additional folder at root
	 */
	public AdditionalFolderAtRoot getAdditionalFolderAtRoot() {
		return additionalFolderAtRoot;
	}

	/**
	 * Sets the additional folder at root.
	 *
	 * @param additionalFolderAtRoot the additional folder at root
	 */
	public void setAdditionalFolderAtRoot(AdditionalFolderAtRoot additionalFolderAtRoot) {
		setExternalListener(additionalFolderAtRoot);
		this.additionalFolderAtRoot = additionalFolderAtRoot;
	}

	@Override
	public Icon getTreeNodeIcon() {
		return ImagesUtil.readImageIcon("icon-16.png");
	}

	@Override
	public void setDisplayName(String name) {
		this.displayName = name;
	}

	@Override
	public JComponent getInstanceConfigurationPanel() {
		return null;
	}

	@Override
	public DLNAResource getDLNAResource() {
		AdditionalFolderAtRoot additionalFolderAtRoot = getAdditionalFolderAtRoot();
		if(dlnaResource == null && additionalFolderAtRoot != null) {
			// lazy-initialize the DLNA resource
			
			// 1) Create a new VirtualFolder in order to set the specified name for it
			dlnaResource = new VirtualFolder(displayName, null);
			
			// 2) Add all children of the plugin
			for(DLNAResource childResource : additionalFolderAtRoot.getChild().getChildren()) {
				dlnaResource.addChild(childResource);
			}
		}
		
		return dlnaResource;
	}

	@Override
	public void saveInstanceConfiguration(String saveFilePath) throws IOException { }

	@Override
	public void loadInstanceConfiguration(String saveFilePath) throws IOException { }

	@Override
	public boolean isInstanceAvailable() {
		return true;
	}
}
