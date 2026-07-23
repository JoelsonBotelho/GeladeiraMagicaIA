package dev.java10x.GeladeiraMagicaIA.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import dev.java10x.GeladeiraMagicaIA.model.FoodItemModel;

public interface FoodItemRepository extends JpaRepository<FoodItemModel, Long> {
    
}
