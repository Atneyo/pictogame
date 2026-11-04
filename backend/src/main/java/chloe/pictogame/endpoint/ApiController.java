package chloe.pictogame.endpoint;

import chloe.pictogame.common.WordConstants;
import chloe.pictogame.dto.WordResponse;
import chloe.pictogame.service.WordService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final WordService wordService;

    public ApiController(WordService wordService) {
        this.wordService = wordService;
    }

    // ========================= WORDS =========================

    @GetMapping("/words")
    public List<WordResponse> getWords(@RequestParam(defaultValue = "" + WordConstants.DEFAULT_COUNT) int count) {
        return wordService.getRandomWords(count);
    }
}
