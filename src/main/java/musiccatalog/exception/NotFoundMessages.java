package musiccatalog.exception;

public class NotFoundMessages {
    public static final String USER_NOT_FOUND_MESSAGE = "User not found";
    public static final String TRACK_NOT_FOUND_MESSAGE = "Track not found";
    public static final String ALBUM_NOT_FOUND_MESSAGE = "Album not found";
    public static final String ARTIST_NOT_FOUND_MESSAGE = "Artist not found";
    public static final String PLAYLIST_NOT_FOUND_MESSAGE = "Playlist not found";
    public static final String GENRE_NOT_FOUND_MESSAGE = "Genre not found";
    public static final String BAD_REQUEST = "Bad Request";

    private NotFoundMessages() {
        throw new UnsupportedOperationException("Class cannot be instantiated");
    }
}
