package chloe.pictogame.service;

import chloe.pictogame.common.WordConstants;
import chloe.pictogame.dto.WordPageResponse;
import chloe.pictogame.dto.WordResponse;
import chloe.pictogame.exception.ErrorCode;
import chloe.pictogame.exception.InvalidNewWordException;
import chloe.pictogame.exception.InvalidPaginationException;
import chloe.pictogame.exception.InvalidWordCountException;
import chloe.pictogame.models.Word;
import chloe.pictogame.repository.WordRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Transactional(readOnly = true)
    public WordPageResponse getWords(int pageNb, int pageSize) {
        if (pageSize<WordConstants.MIN_PAGE_SIZE || pageSize>WordConstants.MAX_PAGE_SIZE){
            throw new InvalidPaginationException("page size must be between %d and %d (received: %d)".formatted(WordConstants.MIN_PAGE_SIZE,WordConstants.MAX_PAGE_SIZE,pageSize));
        }
        if (pageNb<0) {
            throw new InvalidPaginationException("page number must be positive (received: %d)".formatted(pageNb));
        }
        Pageable page = PageRequest.of(pageNb, pageSize, Sort.by("label"));
        Page<Word> pageResult = wordRepository.findAll(page);
        return WordPageResponse.from(pageResult);
    }
}