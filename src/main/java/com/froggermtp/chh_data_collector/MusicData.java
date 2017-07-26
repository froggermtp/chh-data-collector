package com.froggermtp.chh_data_collector;

public final class MusicData {	
	/**
	 * The URL from where the data was scraped.
	 */
	private final String url;
	/**
	 * The name of the song or project.
	 */
	private final String project;
	/**
	 * The name of the artist.
	 */
	private final String artist;
	/**
	 * The date of when the article was released that contained the music data.
	 * This date should be close to when the music was actually released.
	 */
	private final String date;
	
	public MusicData(String url, String contentName, String artist, String date) {
		this.url = url;
		this.project = contentName;
		this.artist = artist;
		this.date = date;
	}

	/**
	 * @return the URL
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return the project
	 */
	public String getProject() {
		return project;
	}

	/**
	 * @return the artist
	 */
	public String getArtist() {
		return artist;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	@Override
	public String toString() {
		StringBuilder sbuf = new StringBuilder();
		sbuf.append("MusicData ")
		.append("[url=").append(getUrl())
		.append(", project=").append(project)
		.append(", artist=").append(artist)
		.append(", date=").append(date)
		.append("]");
		
		return sbuf.toString();
	}
}
