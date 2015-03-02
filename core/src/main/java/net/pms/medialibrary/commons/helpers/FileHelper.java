/*
 * PS3 Media Server, for streaming any medias to your PS3.
 * Copyright (C) 2013  Ph.Waeber
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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileHelper.class);
	
	public static final String NEW_LINE_SEPARATOR = System.getProperty("line.separator");

	/**
	 * Copies the file from srFile to dtFile
	 *
	 * @param srFile the source file
	 * @param dtFile the destination file
	 */
	public static void copyFile(String srFile, String dtFile) {
		try {
			File f1 = new File(srFile);
			File f2 = new File(dtFile);
			InputStream in = new FileInputStream(f1);

			// For Overwrite the file.
			OutputStream out = new FileOutputStream(f2);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			if (LOGGER.isDebugEnabled()) LOGGER.debug(String.format("Copied file %s to %s", srFile, dtFile));
		} catch (FileNotFoundException ex) {
			LOGGER.error(String.format("Failed to copy file %s to %s", srFile, dtFile), ex);
		} catch (IOException e) {
			LOGGER.error(String.format("Failed to copy file %s to %s", srFile, dtFile), e);
		}
	}
	
	/**
	 * Combines the two paths
	 *
	 * @param path1 the path 1
	 * @param path2 the path 2
	 * @return the combined path
	 */
	public static String combine (String path1, String path2) {
	    File file1 = new File(path1);
	    File file2 = new File(file1, path2);
	    return file2.getPath();
	}
	
	/**
	 * Reads a text file and returns its content as a string.
	 *
	 * @param filePath the path of the file
	 * @return the content of the file as a string; or an empty string if a problem occurred
	 */
	public static String getTextFileContent(String filePath) {
		String text = "";
		File file = new File(filePath);
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(file);
			text = getTextFileContent(fis);
		} catch (FileNotFoundException e) {
			LOGGER.error(String.format("File %s not found", filePath));
		} finally {
			// Properly close objects
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					LOGGER.warn("Failed to properly close FileInputStream", e);
				}
				fis = null;
			}
		}
		
		return text;
	}

	/**
	 * Reads a text file from the given input stream and returns its content as a string.
	 *
	 * @param inputStream the input stream
	 * @return the content of the text file as string
	 */
	public static String getTextFileContent(InputStream inputStream) {
		StringBuilder sb = new StringBuilder();
		BufferedInputStream bis = null;
		DataInputStream dis = null;

		try {
			// Here BufferedInputStream is added for fast reading.
			bis = new BufferedInputStream(inputStream);
			dis = new DataInputStream(bis);

			// dis.available() returns 0 if the file does not have more lines.
			while (dis.available() != 0) {
				// this statement reads the line from the file and print it to
				// the console.
				sb.append(dis.readLine());
				sb.append(NEW_LINE_SEPARATOR);
			}
		} catch (IOException e) {
			LOGGER.error("IOException while reading from stream", e);
		} finally {			
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					LOGGER.warn("Failed to properly close BufferedInputStream", e);
				}
				bis = null;
			}
			
			if (dis != null) {
				try {
					dis.close();
				} catch (IOException e) {
					LOGGER.warn("Failed to properly close DataInputStream", e);
				}
				dis = null;
			}
		}

		return sb.toString();
		
	}
}
