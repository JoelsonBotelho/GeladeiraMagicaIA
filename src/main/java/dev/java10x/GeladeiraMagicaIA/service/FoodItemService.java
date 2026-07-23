package dev.java10x.GeladeiraMagicaIA.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import dev.java10x.GeladeiraMagicaIA.model.FoodItemModel;
import dev.java10x.GeladeiraMagicaIA.repository.FoodItemRepository;

@Service
public class FoodItemService {
    
    private final FoodItemRepository foodItemRepository;
    public FoodItemService(FoodItemRepository foodItemRepository) {
        this.foodItemRepository = foodItemRepository;
    }

    public FoodItemModel guardar(FoodItemModel foodItem) {
        return foodItemRepository.save(foodItem);
    }

    public List<FoodItemModel> listarTodos() {
        return foodItemRepository.findAll();
    }

    public Optional<FoodItemModel> buscarPorId(Long id) {
        return foodItemRepository.findById(id);
    }

    public void deletar(Long id) {
        foodItemRepository.deleteById(id);
    }

    public FoodItemModel atualizar(Long id, FoodItemModel foodItemAtualizado) {
        FoodItemModel foodItemExistente = foodItemRepository.findById(id).orElse(null);
        if (foodItemExistente != null) {
            foodItemExistente.setNome(foodItemAtualizado.getNome());
            foodItemExistente.setCategoria(foodItemAtualizado.getCategoria());
            foodItemExistente.setQuantidade(foodItemAtualizado.getQuantidade());
            foodItemExistente.setData_validade(foodItemAtualizado.getData_validade());
            return foodItemRepository.save(foodItemExistente);
        }
        return null;
    }
}
