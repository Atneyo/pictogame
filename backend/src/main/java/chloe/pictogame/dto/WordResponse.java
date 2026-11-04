package chloe.pictogame.dto;

import chloe.pictogame.models.Word;

public record WordResponse(Long id, String label) {

    public static WordResponse from(Word word) {
        return new WordResponse(word.getId(), word.getLabel());
    }
}
