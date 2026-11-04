package chloe.pictogame.service;

import chloe.pictogame.dto.WordResponse;
import chloe.pictogame.repository.WordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WordService {

    private final WordRepository wordRepository;

    public WordService(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    @Transactional(readOnly = true)
    public List<WordResponse> getRandomWords(int count) {
        return wordRepository.getRandomWords(count).stream()
                .map(WordResponse::from)
                .toList();
    }
}