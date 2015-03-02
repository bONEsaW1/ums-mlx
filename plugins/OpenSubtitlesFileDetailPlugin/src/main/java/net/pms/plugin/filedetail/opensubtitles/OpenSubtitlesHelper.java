package net.pms.plugin.filedetail.opensubtitles;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.pms.medialibrary.commons.dataobjects.DOVideoFileInfo;
import net.pms.medialibrary.commons.helpers.FileHelper;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenSubtitlesHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(OpenSubtitlesHelper.class);
	
	private static final String USER_AGENT = "OSTestUserAgent";
	private static final long MAX_TOKEN_AGE_MILLIS = 10 * 60 * 1000; // 10 minutes
	private static final String OPENSUBTITLES_URL = "http://api.opensubtitles.org/xml-rpc";
	
	// The placeholders used to replace values in the OpenSubtitle queries
	private static final String PLACEHOLDER_TOKEN = "${token}";
	private static final String PLACEHOLDER_SUTITLELANGUAGEID = "${sublanguageid}";
	private static final String PLACEHOLDER_MOVIEHASH = "${moviehash}";
	private static final String PLACEHOLDER_MOVIEBYTESIZE = "${moviebytesize}";
	private static final String PLACEHOLDER_IMDBID = "${imdbid}";
	private static final String PLACEHOLDER_USERNAME = "${username}";
	private static final String PLACEHOLDER_PASSWORD = "${password}";
	private static final String PLACEHOLDER_USER_LANGUAGE = "${userlanguage}";
	private static final String PLACEHOLDER_USER_AGENT = "${useragent}";
	
	private static final String MATCHERPATTERN_SEARCHSUBTITLES = "SubFileName</name>.*?<string>([^<]+)</string>.*?SubLanguageID</name>.*?<string>([^<]+)</string>.*?SubDownloadLink</name>.*?<string>([^<]+)</string>";

	private static boolean isInitialized = false;
	private static String token = null;
	private static long tokenAge;

	// These variables will be initialized in the static constructor
	private static URL openSubtitlesUrl;
	private static String searchSubtitlesByHashRequestXml;
	private static String searchSubtitlesByImdbIdRequestXml;
	private static String loginRequestXml;
	
	// Static constructor
	static {
		// Load the XML files which will be used to communicate with opensubtitles.org
		searchSubtitlesByHashRequestXml = FileHelper.getTextFileContent(OpenSubtitlesHelper.class.getResourceAsStream("/RequestXml/SearchSubtitlesByHash.xml"));
		searchSubtitlesByImdbIdRequestXml = FileHelper.getTextFileContent(OpenSubtitlesHelper.class.getResourceAsStream("/RequestXml/SearchSubtitlesByImdbId.xml"));
		loginRequestXml = FileHelper.getTextFileContent(OpenSubtitlesHelper.class.getResourceAsStream("/RequestXml/LogIn.xml"));
		
		// Create the URL to communicate with opensubtitles.org
		try {
			openSubtitlesUrl = new URL(OPENSUBTITLES_URL);
		} catch (MalformedURLException e) {
			LOGGER.error(String.format("Failed to create OpenSubtitles URL with URL=%s", OPENSUBTITLES_URL), e);
		}
		
		// Make sure the helper has been initialized correctly
		if(searchSubtitlesByHashRequestXml != null && !searchSubtitlesByHashRequestXml.equals("") &&
				searchSubtitlesByImdbIdRequestXml != null && !searchSubtitlesByImdbIdRequestXml.equals("") &&
				loginRequestXml != null && !loginRequestXml.equals("") &&
				openSubtitlesUrl != null) {
			isInitialized = true;
		}
	}

	/**
	 * Finds subtitles for the given parameters.</br></br>
	 * 
	 * 1) Try to get subtitles by file hash for the file specified by fileInfo.getFilePath().</br>
	 * 2) If no subtitles could be found, try to get subtitles by IMDB id specified by fileInfo.getImdbId(),
	 *
	 * @param videoFileInfo the video file info
	 * @param subtitleLanguages the subtitle languages. Multiple languages can be specified comma separated. E.g. 'eng,fra,ger'. To get all available subtitles either specify 'all' or an empty string.
	 * @param maxSubtitles the maximum number of subtitles which will be returned.
	 * @return the result map<string, string> with key=subtitle name, value=subtitle URL. If no results were found or an error occurred, an empty map will be returned.
	 */
	public static Map<String, String> findSubtitles(DOVideoFileInfo videoFileInfo, String subtitleLanguages, int maxSubtitles) {
		Map<String, String> res = new TreeMap<String, String>();
		
		// Entry checks
		if(!isInitialized) {
			LOGGER.warn("OpenSubtitlesHelper hasn't been properly initialized before calling findSubs");
			return res;
		}
		if(videoFileInfo == null) {
			LOGGER.warn("The received video file info is NULL");
			return res;
		}

		res = findSubtitles(videoFileInfo.getFilePath(), videoFileInfo.getImdbId().replaceFirst("tt", ""), subtitleLanguages, maxSubtitles);
		
		return res;
	}

	/**
	 * Finds subtitles for the given parameters.</br></br>
	 * 
	 * 1) Try to get subtitles by file hash for the file specified by filePath.</br>
	 * 2) If no subtitles could be found, try to get subtitles by IMDB id specified by imdbId.
	 *
	 * @param filePath the path of the file
	 * @param imdbId the IMDB id
	 * @param subtitleLanguages the subtitle languages. Multiple languages can be specified comma separated. E.g. 'eng,fra,ger'. To get all available subtitles either specify 'all' or an empty string.
	 * @param maxSubtitles the maximum number of subtitles which will be returned.
	 * @return the result map<string, string> with key=subtitle name, value=subtitle URL. If no results were found or an error occurred, an empty map will be returned.
	 */
	public static Map<String, String> findSubtitles(String filePath, String imdbId, String subtitleLanguages, int maxSubtitles) {
		Map<String, String> res = new TreeMap<String, String>();
		
		// Entry checks
		if(!isInitialized) {
			LOGGER.warn("OpenSubtitlesHelper hasn't been properly initialized before calling findSubs");
			return res;
		}
		
		// Compute file hash
		File file = new File(filePath);
		
		long fileSizeBytes = 0;
		String fileHash = null;
		if(file.exists() && file.canRead()) {
			fileSizeBytes = file.length();
			try {
				fileHash = computeFileHash(file);
			} catch (IOException e) {
				LOGGER.error(String.format("Failed to compute file hash for file='%s'", file.getAbsolutePath()), e);
			}
		}

		res = findSubtitles(fileHash, fileSizeBytes, imdbId, subtitleLanguages, maxSubtitles);
		
		return res;
	}
	
	/**
	 * Finds subtitles for the given parameters.</br></br>
	 * 
	 * 1) Try to get subtitles by file hash for the file specified by fileHash and sizeBytes.</br>
	 * 2) If no subtitles could be found, try to get subtitles by IMDB id specified by imdbId.
	 * 
	 * @param fileHash the hash of the video file (can be null or empty)
	 * @param fileSizeBytes the size of the video file in Bytes
	 * @param imdbId the IMDB id (can be null or empty)
	 * @param subtitleLanguages the subtitle languages. Multiple languages can be specified comma separated. E.g. 'eng,fra,ger'. To get all available subtitles either specify 'all' or an empty string.
	 * @param maxSubtitles the maximum number of subtitles which will be returned.
	 * @return the result map<string, string> with key=subtitle name, value=subtitle URL. If no results were found or an error occurred, an empty map will be returned.
	 */
	public static Map<String, String> findSubtitles(String fileHash, long fileSizeBytes, String imdbId, String subtitleLanguages, int maxSubtitles) {
		Map<String, String> res = new TreeMap<String, String>();

		// Entry checks
		if(!isInitialized) {
			LOGGER.warn("OpenSubtitlesHelper hasn't been properly initialized before calling findSubtitles");
			return res;
		}
		
		// If no subtitle languages have been specified, get all languages
		if(subtitleLanguages == null) {
			subtitleLanguages = "all";
		}
		
		// Obtain a new token if required
		try {
			login();
		} catch (IOException e) {
			LOGGER.error("Failed to log in", e);
		}		
		if (token == null) {
			LOGGER.warn("Failed to find subtitles because the token is NULL");
			return res;
		}
		
		// Try to get subtitles by file hash if we got one
		if(fileHash != null && !fileHash.equals("")) {
			try {
				res = findSubtitlesByFileHash(fileHash, fileSizeBytes, subtitleLanguages, maxSubtitles);
				
				if(res.size() > 0) {
					LOGGER.trace(String.format("Found %s subtitles for file hash '%s' and size %s Bytes", res.size(), fileHash, fileSizeBytes));
				}
			} catch (IOException e) {
				LOGGER.error(String.format("Failed to find subtitles for file hash '%s' and size %s Bytes", res.size(), fileHash, fileSizeBytes), e);
			}
		}
		
		// If no results have been found by file hash, try to get them by IMDB id if we got one
		if(res.size() == 0 && imdbId != null && !imdbId.equals("")) {
			try {
				res = findSubtitlesByImdbId(imdbId, subtitleLanguages, maxSubtitles);

				if(res.size() > 0) {
					LOGGER.trace(String.format("Found %s subtitles by IMDB id '%s'", res.size(), imdbId));
				}
			} catch (IOException e) {
				LOGGER.error(String.format("Failed to find subtitles for IMDB id '%s'", imdbId), e);
			}
		}
		
		return res;
	}

	private static Map<String, String> findSubtitlesByFileHash(String hash, long fileSizeBytes, String queryLanguage, int maxSubtitles) throws IOException {
		// Prepare the XML to query opensubtitles.org
		String request = searchSubtitlesByHashRequestXml;
		request = request.replace(PLACEHOLDER_TOKEN, token);
		request = request.replace(PLACEHOLDER_SUTITLELANGUAGEID, queryLanguage);
		request = request.replace(PLACEHOLDER_MOVIEHASH, hash);
		request = request.replace(PLACEHOLDER_MOVIEBYTESIZE, Long.toString(fileSizeBytes));

		return getFindSubtitlesResult(request, maxSubtitles);
	}

	private static Map<String, String> findSubtitlesByImdbId(String imdbId, String queryLanguage, int maxSubtitles) throws IOException {
		// Prepare the XML to query opensubtitles.org
		String request = searchSubtitlesByImdbIdRequestXml;
		request = request.replace(PLACEHOLDER_TOKEN, token);
		request = request.replace(PLACEHOLDER_SUTITLELANGUAGEID, queryLanguage);
		request = request.replace(PLACEHOLDER_IMDBID, imdbId);

		return getFindSubtitlesResult(request, maxSubtitles);
	}
	
	private static Map<String, String> getFindSubtitlesResult(String request, int maxSubtitles) throws IOException {
		TreeMap<String, String> res = new TreeMap<String, String>();
		
		// Get the results from opensubtitles.org
		String response = getResponse((HttpURLConnection) openSubtitlesUrl.openConnection(), request);

		// Compute the received result
		Pattern pattern = Pattern.compile(MATCHERPATTERN_SEARCHSUBTITLES, Pattern.DOTALL);
		Matcher matcher = pattern.matcher(response);
		while (matcher.find()) {
			res.put(matcher.group(2) + ":" + matcher.group(1), matcher.group(3));
			if (res.size() >= maxSubtitles) {
				break;
			}
		}
		
		return res;
	}
	
	private static String getResponse(HttpURLConnection connection, String query) throws IOException {
		// Prepare the request
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setUseCaches(false);
		connection.setDefaultUseCaches(false);
		connection.setRequestProperty("Content-Type", "text/xml");
		connection.setRequestProperty("Content-Length", Integer.toString(query.length()));
		connection.setRequestMethod("POST");

		// Open up the output stream of the connection
		if (!StringUtils.isEmpty(query)) {
			try (DataOutputStream output = new DataOutputStream(connection.getOutputStream())) {
				output.writeBytes(query);
				output.flush();
			}
		}

		// Compute the response
		StringBuilder response;
		try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			response = new StringBuilder();
			String str;
			while ((str = in.readLine()) != null) {
				response.append(str.trim());
				response.append(FileHelper.NEW_LINE_SEPARATOR);
			}
		}

		return response.toString();
	}
	
	private static synchronized void login() throws IOException {
		if ((token != null) && tokenIsYoung()) {
			// Don't do anything if we've already got a valid token
			return;
		}

		// Prepare the XML to query opensubtitles.org
		String request = loginRequestXml;
		request = request.replace(PLACEHOLDER_USERNAME, "");
		request = request.replace(PLACEHOLDER_PASSWORD, "");
		request = request.replace(PLACEHOLDER_USER_LANGUAGE, "");
		request = request.replace(PLACEHOLDER_USER_AGENT, USER_AGENT);
		
		// Set the token
		Pattern pattern = Pattern.compile("token.*?<string>([^<]+)</string>", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(getResponse((HttpURLConnection) openSubtitlesUrl.openConnection(), request));
		if (matcher.find()) {
			token = matcher.group(1);
			tokenAge = System.currentTimeMillis();
		}
	}
	
	/**
	 * Determines whether the token is younger than the max token age
	 *
	 * @return true, if token is younger than the token age; otherwise false
	 */
	private static boolean tokenIsYoung() {
		return ((System.currentTimeMillis() - tokenAge) < MAX_TOKEN_AGE_MILLIS);
	}

	/**
	 *  From opensubtitles.orgn (with little modifications)
	 *  http://trac.opensubtitles.org/projects/opensubtitles/wiki/HashSourceCodes#Java
	 *  
	 */
	
	/**
	 * Size of the chunks that will be hashed in bytes (64 KB)
	 */
	private static final int HASH_CHUNK_SIZE = 64 * 1024;

	/**
	 * Computes the hash of a file.
	 *
	 * @param file the file
	 * @return the file hash as string
	 * @throws IOException thrown when an error occurs while reading the file
	 */
	public static String computeFileHash(File file) throws IOException {
		long size = file.length();
		long chunkSizeForFile = Math.min(HASH_CHUNK_SIZE, size);
		
		FileInputStream fis = null;
		FileChannel fileChannel = null;

		try {
			fis = new FileInputStream(file);
			fileChannel = fis.getChannel();
			
			long head = computeHashForChunk(fileChannel.map(MapMode.READ_ONLY, 0, chunkSizeForFile));
			long tail = computeHashForChunk(fileChannel.map(MapMode.READ_ONLY, Math.max(size - HASH_CHUNK_SIZE, 0), chunkSizeForFile));

			return String.format("%016x", size + head + tail);
		} finally {
			// Properly close objects
			if(fileChannel != null) {
				try {
					fileChannel.close();
				} catch (IOException e) {
					LOGGER.warn("Failed to properly close FileChannel", e);
				} finally {
					fileChannel = null;
				}				
			}

			if(fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					LOGGER.warn("Failed to properly close FileInputStream", e);
				} finally {
					fis = null;
				}				
			}
		}
	}

	private static long computeHashForChunk(ByteBuffer buffer) {
		LongBuffer longBuffer = buffer.order(ByteOrder.LITTLE_ENDIAN).asLongBuffer();
		long hash = 0;

		while (longBuffer.hasRemaining()) {
			hash += longBuffer.get();
		}

		return hash;
	}
}
