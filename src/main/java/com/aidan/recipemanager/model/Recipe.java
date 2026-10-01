package com.aidan.recipemanager.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {
    private int id;
    private String name;
    private String description;
    private Category category;
    private int prepTime;
    private int cookTime;
    private int servings;
    private String instructions;
    private boolean favorite;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
