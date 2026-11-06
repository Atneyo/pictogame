package chloe.pictogame.integration.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import chloe.pictogame.integration.MySqlIntegrationTest;
import chloe.pictogame.models.Word;
import chloe.pictogame.repository.WordRepository;

@SpringBootTest
class WordRepositoryIT extends MySqlIntegrationTest {

    @Autowired
    private WordRepository wordRepository;

    @Test
    void flywayMigration_insertsInitialWords() {
        // Prepare

        // Execute

        // Control
        assertThat(wordRepository.count()).isGreaterThanOrEqualTo(30);
    }

    @Test
    void findRandom_returnsRequestedNumberOfDistinctWords() {
        // Prepare

        // Execute
        List<Word> words = wordRepository.getRandomWords(10);

        // Control
        assertThat(words).hasSize(10);
        assertThat(words).extracting(Word::getId).doesNotHaveDuplicates();
    }

    @Test
    void findRandom_withMoreThanAvailable_returnsAllWords() {
        // Prepare

        // Execute
        List<Word> words = wordRepository.getRandomWords((int) wordRepository.count() + 10);

        // Control
        assertThat(words).hasSize((int) wordRepository.count());
    }

    @Test
    void findRandom_givesDifferentResultsAcrossCalls() {
        // Prepare
        Set<List<Long>> draws = new HashSet<>();

        // Execute
        for (int i = 0; i < 5; i++) {
            draws.add(wordRepository.getRandomWords(10).stream().map(Word::getId).toList());
        }

        // Control
        // Five identical draws of 10 out of 30 words would be astronomically unlikely
        assertThat(draws).hasSizeGreaterThan(1);
    }

    @Test
    void accentedWords_areStoredWithCorrectEncoding() {
        // Prepare

        // Execute

        // Control
        assertThat(wordRepository.findAll())
                .extracting(Word::getLabel)
                .contains("fenêtre", "école", "éléphant");
    }
}