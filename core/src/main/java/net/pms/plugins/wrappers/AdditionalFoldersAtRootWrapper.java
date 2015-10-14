package net.pms.plugins.wrappers;

import java.io.IOException;

import javax.swing.Icon;
import javax.swing.JComponent;

import net.pms.dlna.DLNAResource;
import net.pms.external.AdditionalFoldersAtRoot;
import net.pms.plugins.DlnaTreeFolderPlugin;
import net.pms.plugins.Plugin;
import net.pms.util.ImagesUtil;

/**
 * Wraps the old style plugin {@link net.pms.external.AdditionalFoldersAtRoot} to be used by the new plugin system
 */
@SuppressWarnings("deprecation")
public class AdditionalFoldersAtRootWrapper extends BaseWrapper implements Plugin, DlnaTreeFolderPlugin {
	private AdditionalFoldersAtRoot additionalFoldersAtRoot;
	private String displayName;
	private AdditionalFoldersAtRootWrapperDlnaResource dlnaResource;

	/**
	 * The Constructor.
	 *
	 * @param additionalFoldersAtRoot the additional folders at root
	 */
	public AdditionalFoldersAtRootWrapper(AdditionalFoldersAtRoot additionalFoldersAtRoot) {
		super(additionalFoldersAtRoot);
		setAdditionalFoldersAtRoot(additionalFoldersAtRoot);
	}

	/**
	 * The Constructor.
	 */
	public AdditionalFoldersAtRootWrapper() {
	}

	/**
	 * Gets the folders.
	 *
	 * @return the folders
	 */
	public AdditionalFoldersAtRoot getAdditionalFoldersAtRoot() {
		return additionalFoldersAtRoot;
	}

	/**
	 * Sets the additional folders at root.
	 *
	 * @param folders the additional folders at root
	 */
	public void setAdditionalFoldersAtRoot(AdditionalFoldersAtRoot additionalFoldersAtRoot) {
		setExternalListener(additionalFoldersAtRoot);
		this.additionalFoldersAtRoot = additionalFoldersAtRoot;
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
		AdditionalFoldersAtRoot additionalFoldersAtRoot = getAdditionalFoldersAtRoot();
		if (dlnaResource == null && additionalFoldersAtRoot != null) {
			// lazy-initialize the DLNA resource
			dlnaResource = new AdditionalFoldersAtRootWrapperDlnaResource(displayName, null, additionalFoldersAtRoot);
		}

		return dlnaResource;
	}

	@Override
	public void saveInstanceConfiguration(String saveFilePath) throws IOException {
	}

	@Override
	public void loadInstanceConfiguration(String saveFilePath) throws IOException {
	}

	@Override
	public boolean isInstanceAvailable() {
		return true;
	}
}
