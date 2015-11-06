package net.pms.plugins.wrappers;

import java.util.Iterator;

import net.pms.dlna.DLNAResource;
import net.pms.dlna.virtual.VirtualFolder;
import net.pms.external.AdditionalFoldersAtRoot;

/**
 * The Class AdditionalFoldersAtRootWrapperDlnaResource.
 */
@SuppressWarnings("deprecation")
public class AdditionalFoldersAtRootWrapperDlnaResource extends VirtualFolder {
	private AdditionalFoldersAtRoot additionalFoldersAtRoot;

	/**
	 * The Constructor.
	 *
	 * @param name the name of the resource
	 * @param thumbnailIcon the thumbnail icon of the resource
	 * @param additionalFoldersAtRoot the additional folders at root
	 */
	public AdditionalFoldersAtRootWrapperDlnaResource(String name, String thumbnailIcon, AdditionalFoldersAtRoot additionalFoldersAtRoot) {
		super(name, thumbnailIcon);
		this.additionalFoldersAtRoot = additionalFoldersAtRoot;
	}

	@Override
	public boolean isRefreshNeeded() {
		// A refresh is needed if
		// 1) A child returned by the AdditionalFoldersAtRoot is not contained in the current list of children
		// 2) The number of children is different
		boolean isRefreshNeeded = false;

		int nbAdditionalFoldersAtRoot = 0;
		Iterator<DLNAResource> iterator = additionalFoldersAtRoot.getChildren();
		while (iterator.hasNext()) {
			nbAdditionalFoldersAtRoot++;
			DLNAResource r = iterator.next();
			if (!getChildren().contains(r)) {
				isRefreshNeeded = true;
				break;
			}
		}
		return isRefreshNeeded || nbAdditionalFoldersAtRoot != getChildren().size();
	}

	@Override
	public void doRefreshChildren() {
		// Determine which child is the first that has to be removed
		int firstIndexToRemove = 0;
		Iterator<DLNAResource> iteratorRemove = additionalFoldersAtRoot.getChildren();
		while (iteratorRemove.hasNext()) {
			DLNAResource r = iteratorRemove.next();
			if (getChildren().size() <= firstIndexToRemove || !getChildren().get(firstIndexToRemove).equals(r)) {
				break;
			}
			firstIndexToRemove++;
		}

		// Remove the children after the computed index
		int indexToRemove = firstIndexToRemove;
		while (indexToRemove < getChildren().size()) {
			getChildren().remove(indexToRemove);
			indexToRemove++;
		}

		// Add the new children
		Iterator<DLNAResource> iteratorAdd = additionalFoldersAtRoot.getChildren();
		int indexAdd = 0;
		while (iteratorAdd.hasNext()) {
			DLNAResource r = iteratorAdd.next();
			if (getChildren().size() <= indexAdd || !getChildren().get(indexAdd).equals(r)) {
				addChild(r);
			}
			indexAdd++;
		}
	}

	@Override
	public void discoverChildren() {
		refreshChildren();
	}
}
