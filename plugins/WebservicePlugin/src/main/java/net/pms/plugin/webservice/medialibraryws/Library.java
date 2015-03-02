package net.pms.plugin.webservice.medialibraryws;

import net.pms.medialibrary.commons.exceptions.InitializationException;

public interface Library {
	void scanFolder(String folderPath, boolean scanSubFolders,
			boolean scanVideo, boolean tmdbEnabled, boolean scanAudio,
			boolean scanPictures, boolean useFileImportTemplate, int fileImportTemplateId) throws InitializationException;

	void resetLibrary();

	void cleanLibrary();
}
