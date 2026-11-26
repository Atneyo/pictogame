package chloe.pictogame.endpoint;

import chloe.pictogame.common.WordConstants;
import chloe.pictogame.dto.WordResponse;
import chloe.pictogame.service.WordService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final WordService wordService;

    public ApiController(WordService wordService) {
        this.wordService = wordService;
    }

    // ========================= WORDS =========================

    @GetMapping("/words/random")
    public List<WordResponse> getRandomWords(@RequestParam(defaultValue = "" + WordConstants.DEFAULT_COUNT) int count) {
        return wordService.getRandomWords(count);
    }

    @PostMapping("/words")
    public WordResponse addWord(@RequestParam() String label) {
        return wordService.addWord(label);
    }
}
