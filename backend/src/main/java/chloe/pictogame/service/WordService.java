package chloe.pictogame.service;

import chloe.pictogame.common.WordConstants;
import chloe.pictogame.dto.WordResponse;
import chloe.pictogame.exception.ErrorCode;
import chloe.pictogame.exception.InvalidNewWordException;
import chloe.pictogame.exception.InvalidWordCountException;
import chloe.pictogame.models.Word;
import chloe.pictogame.repository.WordRepository;
import org.springframework.dao.DataIntegrityViolationException;
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
        if (count < WordConstants.MIN_COUNT || count > WordConstants.MAX_COUNT) {
            throw new InvalidWordCountException(count, WordConstants.MIN_COUNT, WordConstants.MAX_COUNT);
        }
        return wordRepository.getRandomWords(count).stream()
                .map(WordResponse::from)
                .toList();
    }

    @Transactional
    public WordResponse addWord(String label) {
        if (label.isEmpty()) {
            throw new InvalidNewWordException(label, ErrorCode.INVALID_LABEL_NULL);
        }
        Word word = new Word(label);
        try {
            word = wordRepository.save(word);
            return WordResponse.from(word);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("Duplicate")) {
                throw new InvalidNewWordException(label, ErrorCode.INVALID_LABEL_DUPLICATE);
            } else {
                throw e;
            }
        }

    }
}