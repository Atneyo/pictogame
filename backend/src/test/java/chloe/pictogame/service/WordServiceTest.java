package chloe.pictogame.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import chloe.pictogame.dto.WordPageResponse;
import chloe.pictogame.exception.ApiException;
import chloe.pictogame.exception.InvalidNewWordException;
import chloe.pictogame.exception.InvalidPaginationException;
import chloe.pictogame.common.WordConstants;
import chloe.pictogame.dto.WordResponse;
import chloe.pictogame.exception.ErrorCode;
import chloe.pictogame.exception.InvalidWordCountException;
import chloe.pictogame.models.Word;
import chloe.pictogame.repository.WordRepository;

@ExtendWith(MockitoExtension.class)
class WordServiceTest {

    @Mock
    private WordRepository wordRepository;

    @InjectMocks
    private WordService wordService;

    @Test
    void getRandomWords_returnsRepositoryWordsAsDtos() {
        // Prepare
        when(wordRepository.getRandomWords(2)).thenReturn(List.of(new Word("chat"), new Word("chien")));

        // Execute
        List<WordResponse> result = wordService.getRandomWords(2);

        // Control
        assertThat(result).extracting(WordResponse::label).containsExactly("chat", "chien");
    }

    @Test
    void getRandomWords_returnsEmptyListWhenTableIsEmpty() {
        // Prepare
        when(wordRepository.getRandomWords(5)).thenReturn(List.of());

        // Execute
        List<WordResponse> result = wordService.getRandomWords(5);

        // Control
        assertThat(result).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(ints = {WordConstants.MIN_COUNT, WordConstants.MAX_COUNT})
    void getRandomWords_acceptsBoundaryValues(int count) {
        // Prepare
        when(wordRepository.getRandomWords(count)).thenReturn(List.of());

        // Execute
        List<WordResponse> result = wordService.getRandomWords(count);

        // Control
        assertThat(result).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(ints = {WordConstants.MIN_COUNT - 1, -5, WordConstants.MAX_COUNT + 1})
    void getRandomWords_rejectsOutOfRangeCount(int count) {
        // Prepare

        // Execute
        assertThatExceptionOfType(InvalidWordCountException.class)
                .isThrownBy(() -> wordService.getRandomWords(count))
                .satisfies(e -> {
                    assertThat(e.getErrorCode()).isEqualTo(ErrorCode.INVALID_WORD_COUNT);
                    assertThat(e.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(e.getMessage()).contains("received: " + count);
                });

        // Control
        // An invalid request must never reach the database
        verifyNoInteractions(wordRepository);
    }

    @Test
    void addWord_success() {
        // Prepare
        Word word = new Word("test");
        when(wordRepository.save(any())).thenReturn(word);

        // Execute
        WordResponse wordResponse = wordService.addWord("test");

        // Control
        assertThat(wordResponse.label()).isEqualTo("test");

    }

    @Test
    void addWord_error_duplicate() {
        // Prepare
        when(wordRepository.save(any())).thenThrow(new DataIntegrityViolationException("Duplicate"));

        // Execute
        ApiException exception = assertThrows(InvalidNewWordException.class, () -> wordService.addWord("test"));

        // Control
        assertEquals(ErrorCode.INVALID_LABEL_DUPLICATE, exception.getErrorCode());
        assertEquals("this word already exist (received: test)", exception.getMessage());
    }

    @Test
    void addWord_error_word_null() {
        // Prepare

        // Execute
        ApiException exception = assertThrows(InvalidNewWordException.class, () -> wordService.addWord(""));

        // Control
        assertEquals(ErrorCode.INVALID_LABEL_NULL, exception.getErrorCode());
        assertEquals("new word can't be null (received: )", exception.getMessage());
    }

    @Test
    void getWords_success() {
        // Prepare
        Page<Word> wordsPage = new PageImpl<>(
                List.of(new Word("chat"), new Word("chien")),
                PageRequest.of(1, 2),
                5
        );
        when(wordRepository.findAll(any(Pageable.class))).thenReturn(wordsPage);

        // Execute
        WordPageResponse result = wordService.getWords(1, 2);

        // Control
        assertThat(result.nbOfPages()).isEqualTo(3);
        assertThat(result.pageNb()).isEqualTo(1);
        assertThat(result.words()).extracting(WordResponse::label).containsExactly("chat", "chien");

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(wordRepository).findAll(pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageNumber()).isEqualTo(1);
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(2);
        assertThat(pageableCaptor.getValue().getSort().getOrderFor("label")).isNotNull();
        assertThat(pageableCaptor.getValue().getSort().getOrderFor("label").isAscending()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {WordConstants.MIN_PAGE_SIZE, WordConstants.MAX_PAGE_SIZE})
    void getWords_acceptsBoundaryPageSizes(int pageSize) {
        // Prepare
        when(wordRepository.findAll(any(Pageable.class)))
                .thenReturn(Page.empty());

        // Execute
        WordPageResponse result = wordService.getWords(WordConstants.DEFAULT_PAGE, pageSize);

        // Control
        assertThat(result.words()).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(ints = {WordConstants.MIN_PAGE_SIZE - 1, WordConstants.MAX_PAGE_SIZE + 1})
    void getWords_rejectsOutOfRangePageSizes(int pageSize) {
        // Prepare

        // Execute
        assertThatExceptionOfType(InvalidPaginationException.class)
                .isThrownBy(() -> wordService.getWords(WordConstants.DEFAULT_PAGE, pageSize))
                .satisfies(exception -> {
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAMETER);
                    assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                });

        // Control
        verifyNoInteractions(wordRepository);
    }

    @Test
    void getWords_rejectsNegativePageNumber() {
        // Prepare

        // Execute
        assertThatExceptionOfType(InvalidPaginationException.class)
                .isThrownBy(() -> wordService.getWords(-1, WordConstants.DEFAULT_PAGE_SIZE))
                .satisfies(exception -> {
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAMETER);
                    assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                });

        // Control
        verifyNoInteractions(wordRepository);
    }
}