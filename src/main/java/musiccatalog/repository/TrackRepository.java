package musiccatalog.repository;

import java.util.List;
import musiccatalog.model.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {

    @Query("SELECT t FROM Track t "
            + "JOIN t.album a "
            + "JOIN a.artists art "
            + "WHERE art.name = :artistName")
    List<Track> findTracksByArtistName(@Param("artistName") String artistName);

    List<Track> findTracksByName(String name);
}
