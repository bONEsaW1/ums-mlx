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
package net.pms.medialibrary.commons.interfaces;

import net.pms.medialibrary.commons.dataobjects.DOManagedFile;
import net.pms.medialibrary.commons.dataobjects.DOScanReport;
import net.pms.medialibrary.commons.exceptions.ScanStateException;

public interface ILibraryManager {
	DOScanReport getScanState();

	void scanFolder(DOManagedFile mf);

	void cleanStorage();

	void resetStorage();

	void clearVideo();

	void clearPictures();

	void clearAudio();

	int getVideoCount();

	int getAudioCount();

	int getPictureCount();

	void pauseScan() throws ScanStateException;

	void stopScan();

	void unPauseScan() throws ScanStateException;

	void addFileScannerEventListener(IFileScannerEventListener listener);

	void addLibraryManagerEventListener(ILibraryManagerEventListener l);
}
