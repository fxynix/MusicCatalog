package com.mymusic.musiccatalog.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import com.mymusic.musiccatalog.Music;
import org.springframework.stereotype.Service;

@Service
public class MusicServiceImplementation implements MusicService {
    private final List<Music> musicList = new ArrayList<>(
            List.of(
                    Music.builder()
                            .id(0)
                            .name("Золотые купола")
                            .author("Круг Михаил")
                            .album("Дуэты")
                            .duration(Duration.ofSeconds(308))
                            .build(),

                    Music.builder()
                            .id(1)
                            .name("Зомби апокалипсис")
                            .author("Серега Пират")
                            .album("Сингл")
                            .duration(Duration.ofSeconds(163))
                            .build(),

                    Music.builder()
                            .id(2)
                            .name("MEGALOVANIA")
                            .author("TobyFox")
                            .album("Undertale Soundtracks")
                            .duration(Duration.ofSeconds(156))
                            .build(),

                    Music.builder()
                            .id(3)
                            .name("Judas")
                            .author("Lady Gaga")
                            .album("Сингл")
                            .duration(Duration.ofSeconds(250))
                            .build()
            )
    );

    @Override
    public Music getMusicById(int id) {
        return musicList.stream()
                .filter(music -> music.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Music> getMusicByName(String name)  {
        return musicList.stream()
                .filter(music -> music.getName().equals(name))
                .toList();
    }
}
