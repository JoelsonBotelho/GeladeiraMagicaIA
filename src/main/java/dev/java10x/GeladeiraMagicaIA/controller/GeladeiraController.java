package dev.java10x.GeladeiraMagicaIA.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import dev.java10x.GeladeiraMagicaIA.model.FoodItemModel;
import dev.java10x.GeladeiraMagicaIA.service.ChatGptService;
import dev.java10x.GeladeiraMagicaIA.service.FoodItemService;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/geladeira")
public class GeladeiraController {

    private final FoodItemService foodItemService;
    private final ChatGptService chatGptService;

    public GeladeiraController(
        FoodItemService foodItemService,
        ChatGptService chatGptService
    ) {
        this.foodItemService = foodItemService;
        this.chatGptService = chatGptService;
    }

    @GetMapping
    public String mostrarPagina(Model model) {
        carregarPagina(model);
        return "geladeira";
    }

    @PostMapping("/adicionar")
    public String adicionarAlimento(
        @ModelAttribute("foodItem") FoodItemModel foodItem
    ) {
        foodItemService.guardar(foodItem);

        return "redirect:/geladeira?sucesso";
    }

    @PostMapping("/gerar-receita")
    public Mono<String> gerarReceita(Model model) {
        List<FoodItemModel> alimentos =
            foodItemService.listarTodos();

        model.addAttribute("foodItem", new FoodItemModel());
        model.addAttribute("alimentos", alimentos);

        if (alimentos.isEmpty()) {
            model.addAttribute(
                "erroReceita",
                "Adicione pelo menos um alimento antes de gerar a receita."
            );

            return Mono.just("geladeira");
        }

        return chatGptService.generateRecipe(alimentos)
            .map(receita -> {
                model.addAttribute("receita", receita);
                return "geladeira";
            })
            .onErrorResume(exception -> {
                model.addAttribute(
                    "erroReceita",
                    exception.getMessage()
                );

                return Mono.just("geladeira");
            });
    }

    @PostMapping("/deletar")
    public String deletarAlimento(Long id) {
        foodItemService.deletar(id);

        return "redirect:/geladeira?deletado";
    }

    private void carregarPagina(Model model) {
        model.addAttribute("foodItem", new FoodItemModel());
        model.addAttribute(
            "alimentos",
            foodItemService.listarTodos()
        );
    }
}