package com.mymusic.musiccatalog.service;

import java.util.List;
import com.mymusic.musiccatalog.Music;
import org.springframework.stereotype.Service;

@Service
public interface MusicService {
    Music getMusicById(int id);

    List<Music> getMusicByName(String name);
}