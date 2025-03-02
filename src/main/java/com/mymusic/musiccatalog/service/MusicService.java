package com.mymusic.musiccatalog.service;

import com.mymusic.musiccatalog.Music;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface MusicService {
    Music getMusicById(int id);

    List<Music> getMusicByName(String name);
}