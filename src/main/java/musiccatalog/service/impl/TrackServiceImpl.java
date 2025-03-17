package musiccatalog.service.impl;

import java.util.List;
import musiccatalog.dto.create.TrackCreateDto;
import musiccatalog.dto.update.TrackUpdateDto;
import musiccatalog.model.Track;
import musiccatalog.repository.TrackRepository;
import musiccatalog.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TrackServiceImpl implements TrackService {

    private final TrackRepository trackRepository;

    @Autowired
    public TrackServiceImpl(TrackRepository trackRepository) {
        this.trackRepository = trackRepository;
    }

    @Override
    public List<Track> getAllTracks() {
        return trackRepository.findAll();
    }

    @Override
    public Track getTrackById(long id) {
        return trackRepository.findAll().stream()
                .filter(track -> track.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Track> getTrackByName(String name)  {
        return trackRepository.findAll().stream()
                .filter(track -> track.getName().equals(name))
                .toList();
    }

    @Override
    public Track createTrack(TrackCreateDto trackDto) {
        Track track = new Track();
        track.setName(trackDto.getName());
        track.setId(trackDto.getId());
        track.setDuration(trackDto.getDuration());
        track.setTrackNumber(trackDto.getTrackNumber());
        track.setAlbum(trackDto.getAlbum());
        track.setGenres(trackDto.getGenres());
        track.setLikedByUsers(trackDto.getLikedByUsers());
        return trackRepository.save(track);
    }

    @Override
    public Track updateTrack(long id, TrackUpdateDto trackDto) {
        Track track = getTrackById(id);
        if (track == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Track not found");
        }
        if (trackDto.getAlbum() != null) {
            track.setAlbum(trackDto.getAlbum());
        }
        if (trackDto.getName() != null) {
            track.setName(trackDto.getName());
        }
        if (trackDto.getId() != null) {
            track.setId(trackDto.getId());
        }
        if (trackDto.getTrackNumber() != 0) {
            track.setTrackNumber(trackDto.getTrackNumber());
        }
        if (trackDto.getDuration() != 0) {
            track.setDuration(trackDto.getDuration());
        }
        if (trackDto.getGenres() != null) {
            track.setGenres(trackDto.getGenres());
        }
        return trackRepository.save(track);
    }

    @Override
    public void deleteTrack(Long id) {
        trackRepository.deleteById(id);
    }
}
