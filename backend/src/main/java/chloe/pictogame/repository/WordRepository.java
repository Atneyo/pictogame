package chloe.pictogame.repository;

import chloe.pictogame.models.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WordRepository extends JpaRepository<Word, Long> {

    // If there are not enough words, it just sends all words
    @Query(value = "SELECT * FROM word ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<Word> getRandomWords(@Param("count") int count);
}