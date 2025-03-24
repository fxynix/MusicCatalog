package musiccatalog.repository;

import java.util.List;
import musiccatalog.model.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {

    @Query(value = """
    SELECT t.*\s
    FROM tracks t
    INNER JOIN albums a ON t.album_id = a.id
    INNER JOIN album_artists aa ON a.id = aa.album_id
    INNER JOIN artists art ON aa.artist_id = art.id
    WHERE art.name = :artistName
       \s""", nativeQuery = true)
    List<Track> findTracksByArtistName(@Param("artistName") String artistName);

    List<Track> findTracksByName(String name);

    Track findTrackById(long id);
}
