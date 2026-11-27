package chloe.pictogame.dto;

import chloe.pictogame.models.Word;
import org.springframework.data.domain.Page;

import java.util.List;

public record WordPageResponse(int nbOfPages, int pageNb, List<WordResponse> words) {

    public static WordPageResponse from(Page<Word> page){
        return new WordPageResponse(
                page.getTotalPages(),
                page.getNumber(),
                page.getContent().stream().map(WordResponse::from).toList()
        );
    }
}
