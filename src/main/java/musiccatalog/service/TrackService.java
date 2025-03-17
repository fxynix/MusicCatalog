package musiccatalog.service;

import java.util.List;
import musiccatalog.dto.create.TrackCreateDto;
import musiccatalog.dto.update.TrackUpdateDto;
import musiccatalog.model.Track;
import org.springframework.stereotype.Service;

@Service
public interface TrackService {
    List<Track> getAllTracks();

    Track getTrackById(long id);

    List<Track> getTrackByName(String name);

    Track createTrack(TrackCreateDto trackDto);

    Track updateTrack(long id, TrackUpdateDto trackDto);

    void deleteTrack(Long id);

}