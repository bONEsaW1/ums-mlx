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
package net.pms.medialibrary.commons.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.pms.configuration.FormatConfiguration;
import net.pms.dlna.DLNAMediaInfo;
import net.pms.dlna.DLNAResource;
import net.pms.dlna.FileTranscodeVirtualFolder;
import net.pms.dlna.RealFile;
import net.pms.dlna.virtual.TranscodeVirtualFolder;
import net.pms.medialibrary.commons.dataobjects.DOVideoFileInfo;

public class DLNAHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(DLNAHelper.class);

	public static DLNAMediaInfo getMediaForVideo(DOVideoFileInfo video) {		
		DLNAMediaInfo dbMedia = new DLNAMediaInfo();
		dbMedia.setDuration(video.getDurationSec());
		dbMedia.setBitrate(video.getBitrate());
		dbMedia.setWidth(video.getWidth());
		dbMedia.setHeight(video.getHeight());
		dbMedia.setSize(video.getSize());
		dbMedia.setCodecV(video.getCodecV());
		dbMedia.setFrameRate(video.getFrameRate());
		dbMedia.setAspectRatioDvdIso(video.getAspectRatioDvdIso() == null || video.getAspectRatioDvdIso().equals("") ? null : video.getAspectRatioDvdIso());
		dbMedia.setAspectRatioContainer(video.getAspectRatioContainer());
		dbMedia.setAspectRatioVideoTrack(video.getAspectRatioVideoTrack());
		dbMedia.setReferenceFrameCount(video.getReferenceFrameCount());
		dbMedia.setAvcLevel(video.getAvcLevel());
		dbMedia.setBitsPerPixel(video.getBitsPerPixel());
		dbMedia.setThumb(getThumb(video.getThumbnailPath()));
		dbMedia.setContainer(video.getContainer());
		dbMedia.setModel(video.getModel());
		if (dbMedia.getModel() != null && !FormatConfiguration.JPG.equals(dbMedia.getContainer())) {
			dbMedia.setExtrasAsString(dbMedia.getModel());
		}

		dbMedia.setAudioTracksList(video.getAudioCodes());
		dbMedia.setSubtitleTracksList(video.getSubtitlesCodes());

		dbMedia.setDvdtrack(video.getDvdtrack());
		dbMedia.setH264AnnexB(video.getH264_annexB());
		dbMedia.setMimeType(video.getMimeType());
		dbMedia.setParsing(false);
		dbMedia.setSecondaryFormatValid(true);
		dbMedia.setMuxingMode(video.getMuxingMode());
		dbMedia.setFrameRateMode(video.getFrameRateMode());

		dbMedia.setStereoscopy(video.getStereoscopy());
		dbMedia.setMatrixCoefficients(video.getMatrixCoefficients());
		dbMedia.setEmbeddedFontExists(video.isEmbeddedFontExists());

		dbMedia.setMediaparsed(true);
		
		return dbMedia;
	}	
	
	public static String[] getSplitLines(String input, int maxLineLength) {
		List<String> lines = new ArrayList<String>();
		if (maxLineLength > 0 && input.length() > maxLineLength) {
			int cutPos;
			do {
				cutPos = getCutOffPosition(input, maxLineLength);
				String text;
				if (cutPos > 0) {
					text = input.substring(0, cutPos).trim();
					input = input.substring(cutPos).trim();
				} else {
					text = input.trim();
					input = "";
				}

				lines.add(text);

			} while (cutPos > 0);
		} else {
			lines.add(input);
		}
		return lines.toArray(new String[lines.size()]);
	}

	private static int getCutOffPosition(String convertedMask, int maxLineLength) {
		int cutOffPos = -1;
		if (maxLineLength > 0 && convertedMask.length() > maxLineLength) {
			cutOffPos = maxLineLength;
			while (cutOffPos > 0 && convertedMask.charAt(cutOffPos) != ' ') {
				cutOffPos--;
			}
			if (cutOffPos == 0) {
				cutOffPos = maxLineLength;
			}
		}
		return cutOffPos;
	}

	private static byte[] getThumb(String thumbnailPath) {
		byte[] bytePic = new byte[0];

		// try to load picture
		File pic = new File(thumbnailPath);
		if (pic.exists()) {
			InputStream is;
			try {
				is = new FileInputStream(pic);
				int sz = is.available();
				if (sz > 0) {
					bytePic = new byte[sz];
					is.read(bytePic);
				}
				is.close();
			} catch (Exception e) {
				LOGGER.error("Failed to read image with path '" + thumbnailPath + "'", e);
			}
		}

		return bytePic;
	}

	public static String formatSecToHHMMSS(int secsIn) {
		int hours = secsIn / 3600, 
		remainder = secsIn % 3600, 
		minutes = remainder / 60, 
		seconds = remainder % 60;

		return ((hours < 10 ? "0" : "") + hours + ":" + (minutes < 10 ? "0" : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds);
	}

	public static void addMultipleFiles(DLNAResource parent, RealFile child, RealFile originalFile) {
		FileTranscodeVirtualFolder rootFolder = new FileTranscodeVirtualFolder("", null);
		rootFolder.setParent(parent);
		rootFolder.addChild(originalFile.clone());
		rootFolder.resolve();
		
		// Get the transcode folder which is hidden a bit deeper. This could break at some point but is an easy solution..
		TranscodeVirtualFolder transcodeVirtualFolder = null;
		for(DLNAResource dlnaResource : rootFolder.getChildren()) {
			if(dlnaResource.getClass() == TranscodeVirtualFolder.class) {
				transcodeVirtualFolder = (TranscodeVirtualFolder)dlnaResource;
				break;
			}
		}
		FileTranscodeVirtualFolder fileTranscodeVirtualFolder = null;
		if(transcodeVirtualFolder != null) {
			for(DLNAResource dlnaResource : transcodeVirtualFolder.getChildren()) {
				if(dlnaResource.getClass() == FileTranscodeVirtualFolder.class) {
					fileTranscodeVirtualFolder = (FileTranscodeVirtualFolder)dlnaResource;
					break;
				}
			}			
		}
		
		if(fileTranscodeVirtualFolder == null) {
			LOGGER.warn("The transcode folder could not be determined. This should never happen!");
		} else {
			fileTranscodeVirtualFolder.resolve();

			for(DLNAResource r : fileTranscodeVirtualFolder.getChildren()) {
				parent.addChild(r);
			}			
		}
		
	}
}
