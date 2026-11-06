package chloe.pictogame.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import chloe.pictogame.service.WordService;
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
}