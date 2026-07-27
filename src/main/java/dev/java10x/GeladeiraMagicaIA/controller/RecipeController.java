package dev.java10x.GeladeiraMagicaIA.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.java10x.GeladeiraMagicaIA.model.FoodItemModel;
import dev.java10x.GeladeiraMagicaIA.service.ChatGptService;
import dev.java10x.GeladeiraMagicaIA.service.FoodItemService;
import reactor.core.publisher.Mono;

@RestController
public class RecipeController {
    
    private final FoodItemService foodService;
    private final ChatGptService chatGptService;

    public RecipeController(ChatGptService chatGptService, FoodItemService foodService) {
        this.chatGptService = chatGptService;
        this.foodService = foodService;
    }

    @GetMapping("/generate")
    public Mono<ResponseEntity<String>> generateRecipe() {
        List<FoodItemModel> foodItem = foodService.listarTodos();
        return chatGptService.generateRecipe(foodItem)
                .map(recipe -> ResponseEntity.ok(recipe))
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }
}
