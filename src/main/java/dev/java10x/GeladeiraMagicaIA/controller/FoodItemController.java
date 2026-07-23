package dev.java10x.GeladeiraMagicaIA.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.java10x.GeladeiraMagicaIA.model.FoodItemModel;
import dev.java10x.GeladeiraMagicaIA.service.FoodItemService;

@RestController
@RequestMapping("/food")
public class FoodItemController {
    
    private final FoodItemService foodItemService;
    public FoodItemController(FoodItemService foodItemService) {
        this.foodItemService = foodItemService;
    }

    @PostMapping
    public ResponseEntity<FoodItemModel> guardar(@RequestBody FoodItemModel foodItem) {
        FoodItemModel savedFoodItem = foodItemService.guardar(foodItem);
        return ResponseEntity.ok(savedFoodItem);
    }

    @GetMapping
    public ResponseEntity<?> listarTodos() {
        return ResponseEntity.ok(foodItemService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<FoodItemModel> foodItem = foodItemService.buscarPorId(id);
        if (foodItem != null) {
            return ResponseEntity.ok(foodItem);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        if (foodItemService.buscarPorId(id) == null) {
            return ResponseEntity.noContent().build();
        }
        foodItemService.deletar(id);
        return ResponseEntity.ok().body("Item deletado com sucesso.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodItemModel> atualizar(@PathVariable Long id, @RequestBody FoodItemModel foodItemAtualizado) {
        FoodItemModel updatedFoodItem = foodItemService.atualizar(id, foodItemAtualizado);
        if (updatedFoodItem != null) {
            return ResponseEntity.ok(updatedFoodItem);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
