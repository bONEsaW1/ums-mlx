package net.pms.plugin.fileimport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moviejukebox.allocine.AllocineApi;
import com.moviejukebox.allocine.AllocineException;
import com.moviejukebox.allocine.model.CastMember;
import com.moviejukebox.allocine.model.Genre;
import com.moviejukebox.allocine.model.Movie;
import com.moviejukebox.allocine.model.Search;

import net.pms.medialibrary.commons.enumarations.FileProperty;
import net.pms.medialibrary.commons.enumarations.FileType;
import net.pms.medialibrary.commons.exceptions.FileImportException;
import net.pms.plugins.FileImportPlugin;
import net.pms.util.PmsProperties;

public class AllocineImportPlugin implements FileImportPlugin {
	private static final Logger LOGGER = LoggerFactory.getLogger(AllocineImportPlugin.class);

	/** Holds only the project version. It's used to always use the maven build number in code */
	private static final PmsProperties PROPERTIES = new PmsProperties();

	static {
		try {
			PROPERTIES.loadFromResourceFile("/allocineimportplugin.properties", AllocineImportPlugin.class);
		} catch (IOException e) {
			LOGGER.error("Could not load allocineimportplugin.properties", e);
		}
	}

	protected static final ResourceBundle MESSAGES = ResourceBundle.getBundle("allocinemovieimportplugin-i18n.messages");

	private static final String ALLOCINE_PARTNER = "100043982026";
	private static final String ALLOCINE_SECRET = "29d185d98c984a359e6e6f26a0474269";

	private Icon pluginIcon;
	private AllocineApi allocineApi;

	private Movie movie;

	/**
	 * Available video tags.
	 */
	private static enum VideoTag {
		Actor, Writer, Producer
	}

	@Override
	public String getName() {
		return "Allociné";
	}

	@Override
	public String getVersion() {
		return PROPERTIES.get("project.version");
	}

	@Override
	public Icon getPluginIcon() {
		if (pluginIcon == null) {
			pluginIcon = new ImageIcon(getClass().getResource("/allocine-plugin-icon-32.png"));
		}
		return pluginIcon;
	}

	@Override
	public String getShortDescription() {
		return MESSAGES.getString("ShortDescription");
	}

	@Override
	public String getLongDescription() {
		return MESSAGES.getString("LongDescription");
	}

	@Override
	public String getUpdateUrl() {
		return null;
	}

	@Override
	public String getWebSiteUrl() {
		return "http://www.universalmediaserver.com/forum/viewtopic.php?f=6&t=3355";
	}

	@Override
	public void initialize() {
		allocineApi = new AllocineApi(ALLOCINE_PARTNER, ALLOCINE_SECRET);
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

	@Override
	public JComponent getGlobalConfigurationPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveConfiguration() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isPluginAvailable() {
		return true;
	}

	@Override
	public void importFile(String title, String filePath) throws FileImportException {
		try {
			Search searchResult = allocineApi.searchMovies(title);
			if (searchResult != null && searchResult.isValid() && searchResult.getMovies().size() > 0) {
				movie = searchResult.getMovies().get(0);
			}
		} catch (AllocineException e) {
			throw new FileImportException(String.format("Failed to import file with title '%s' from Allociné", title), e);
		}
	}

	@Override
	public void importFileById(String id) throws FileImportException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isImportByIdPossible() {
		return true;
	}

	@Override
	public boolean isSearchForFilePossible() {
		return true;
	}

	@Override
	public List<Object> searchForFile(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void importFileBySearchObject(Object searchObject) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<FileProperty> getSupportedFileProperties() {
		// TODO Auto-generated method stub
		return Arrays.asList(FileProperty.VIDEO_NAME, FileProperty.VIDEO_ORIGINALNAME, FileProperty.VIDEO_CERTIFICATION,
				FileProperty.VIDEO_DIRECTOR, FileProperty.VIDEO_GENRES, FileProperty.VIDEO_COVERURL, FileProperty.VIDEO_YEAR,
				FileProperty.VIDEO_OVERVIEW, FileProperty.VIDEO_TAGLINE, FileProperty.VIDEO_TRAILERURL);
	}

	@Override
	public Object getFileProperty(FileProperty property) {
		if (movie == null) {
			return null;
		}

		switch (property) {
			case VIDEO_NAME:
				return movie.getTitle();
			case VIDEO_ORIGINALNAME:
				return movie.getOriginalTitle();
			case VIDEO_CERTIFICATION:
				return movie.getMovieCertificate() == null || movie.getMovieCertificate().getCertificate() == null ? null : movie.getMovieCertificate().getCertificate().getName();
			case VIDEO_DIRECTOR:
				List<String> directors = new ArrayList<>();
				for (CastMember castMember : movie.getCastMember()) {
					if (castMember.isDirector()) {
						if (castMember.getShortPerson() != null) {
							directors.add(castMember.getShortPerson().getName());
						}
					}
				}
			case VIDEO_GENRES:
				List<Genre> genres = movie.getGenre();
				List<String> genreStrings = null;
				if (genres != null && genres.size() > 0) {
					genreStrings = new ArrayList<>();
					for (Genre genre : genres) {
						genreStrings.add(genre.getName());
					}
				}
				return genreStrings;
			case VIDEO_COVERURL:
				return movie.getPoster() == null ? null : movie.getPoster().getHref();
			case VIDEO_YEAR:
				return movie.getProductionYear();
			case VIDEO_OVERVIEW:
				return movie.getSynopsis();
			case VIDEO_TAGLINE:
				return movie.getSynopsisShort();
			case VIDEO_TRAILERURL:
				return movie.getTrailer() == null ? null : movie.getTrailer().getHref();

			default:
				LOGGER.warn(String.format("Unsupported file property (%s) received", property));
				return null;
		}
	}

	@Override
	public List<String> getSupportedTags(FileType fileType) {
		if (fileType != FileType.VIDEO) {
			return null;
		}

		List<String> supportedVideoTags = new ArrayList<String>();
		for (VideoTag t : VideoTag.values()) {
			supportedVideoTags.add(t.toString());
		}
		return supportedVideoTags;
	}

	@Override
	public List<String> getTags(String tagName) {
		List<CastMember> castMembers = movie.getCastMember();
		if (castMembers == null || castMembers.size() == 0) {
			return null;
		}

		if (tagName.equals(VideoTag.Actor.toString())) {
			List<String> actors = new ArrayList<>();
			for (CastMember castMember : castMembers) {
				if (castMember.isActor()) {
					if (castMember.getShortPerson() != null) {
						actors.add(castMember.getShortPerson().getName());
					}
				}
			}
		} else if (tagName.equals(VideoTag.Writer.toString())) {
			List<String> writers = new ArrayList<>();
			for (CastMember castMember : castMembers) {
				if (castMember.isWriter()) {
					if (castMember.getShortPerson() != null) {
						writers.add(castMember.getShortPerson().getName());
					}
				}
			}
		} else if (tagName.equals(VideoTag.Producer.toString())) {
			List<String> writers = new ArrayList<>();
			for (CastMember castMember : castMembers) {
				if (castMember.isProducer()) {
					if (castMember.getShortPerson() != null) {
						writers.add(castMember.getShortPerson().getName());
					}
				}
			}
		}

		return null;
	}

	@Override
	public List<FileType> getSupportedFileTypes() {
		return Arrays.asList(FileType.VIDEO);
	}

	@Override
	public int getMinPollingIntervalMs() {
		return 1000;
	}

}
