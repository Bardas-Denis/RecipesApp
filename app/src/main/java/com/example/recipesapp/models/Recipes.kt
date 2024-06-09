package com.example.recipesapp.models

class Recipes {
    private val recipes: MutableList<Recipe> = mutableListOf()

    fun addRecipe(recipe: Recipe){
        recipes.add(recipe)
    }
    fun getRecipes(): MutableList<Recipe> {
        return recipes
    }

    fun getRecipe(index: Int): Recipe {
        return recipes[index]
    }
    fun filterRecipes(currentRecipes: Array<Recipe>, filter: String): List<Recipe> {
        return currentRecipes.filter { recipe ->
            when (filter) {
                //Time
                "Less than 15min" -> recipe.time<=15
                "Less than 30min" -> recipe.time<=30
                "Less than 45min" -> recipe.time<=45
                "Less than 1h" -> recipe.time<=60
                "Less than 1.5h" -> recipe.time<=90
                "Less than 2.5h" -> recipe.time<=150
                //Price
                "Less than 5$" -> recipe.price<=5
                "Less than 15$" -> recipe.price<=15
                "Less than 25$" -> recipe.price<=25
                "Less than 40$" -> recipe.price<=40
                "Less than 60$" -> recipe.price<=60
                "Less than 100$" -> recipe.price<=100
                //Origin
                "Mediterranean" ->recipe.origin=="Mediterranean"
                "Arabic" ->recipe.origin=="Arabic"
                "Chinese" ->recipe.origin=="Chinese"
                "Japanese" ->recipe.origin=="Japanese"
                //Diet
                "Vegetarian" -> recipe.diet=="Vegetarian" || recipe.diet=="Vegan"
                "Vegan" -> recipe.diet =="Vegan"
                //Allergens
                "Milk" -> !recipe.allergens.contains("Milk")
                "Eggs" -> !recipe.allergens.contains("Eggs")
                "Fish" -> !recipe.allergens.contains("Fish")
                "Nuts" -> !recipe.allergens.contains("Nuts")
                "Peanuts" -> !recipe.allergens.contains("Peanuts")
                "Gluten" -> !recipe.allergens.contains("Gluten")
                "Crustaceans" -> !recipe.allergens.contains("Crustaceans")
                "Mustard" -> !recipe.allergens.contains("Mustard")
                "Soybeans" -> !recipe.allergens.contains("Soybeans")
                //Difficulty
                "Low" -> recipe.difficulty =="Low"
                "Medium" -> recipe.difficulty =="Medium"
                "High" -> recipe.difficulty =="High"
                //Type
                "Savoury" ->recipe.type == "Savoury"
                "Dessert" ->recipe.type == "Dessert"
                else -> true
            }
        }
    }
    fun suggestRecipes(currentIngredients: Array<String>,filter: String):List<Recipe>
    {
        val quantities = listOf("cup", "cups", "teaspoon", "teaspoons", "tablespoon", "tablespoons", "large", "small", "medium", "gram", "grams", "kg", "ml", "liter", "liters", "ounce", "ounces")
        val lowercaseCurrentIngredients = currentIngredients.map { it.lowercase()}
        return recipes.filter { recipe ->
            val ingredientNames = recipe.ingredients.map { ingredient ->
                ingredient.split(" ").filterNot { it in quantities || it.matches(Regex("\\d+(/\\d+)?")) }.joinToString(" ").lowercase()
            }
            when (filter) {
                "At least 3 ingredients"-> ingredientNames.intersect(lowercaseCurrentIngredients.toSet()).size>=3
                "At least 5 ingredients"-> ingredientNames.intersect(lowercaseCurrentIngredients.toSet()).size>=5
                "At least 7 ingredients"-> ingredientNames.intersect(lowercaseCurrentIngredients.toSet()).size>=7
                else->true
            }
        }
    }
    fun sortRecipes(currentRecipes: Array<Recipe>, criteria: String): List<Recipe> {
        return when (criteria) {
            "TimeAscending" -> currentRecipes.sortedBy { it.time }
            "TimeDescending" -> currentRecipes.sortedByDescending { it.time }
            "PriceAscending" -> currentRecipes.sortedBy { it.price }
            "PriceDescending" -> currentRecipes.sortedByDescending { it.price }
            else -> currentRecipes.toList()
        }
    }
    fun searchRecipes(criteria: String): List<Recipe>{
        val quantities = listOf("cup", "cups", "teaspoon", "teaspoons", "tablespoon", "tablespoons", "large", "small", "medium", "gram", "grams", "kg", "ml", "liter", "liters", "ounce", "ounces")
        return recipes.filter { recipe ->
            val ingredientNames = recipe.ingredients.map { ingredient ->
                ingredient.split(" ").filterNot { it in quantities || it.matches(Regex("\\d+(/\\d+)?")) }.joinToString(" ").lowercase()
            }
            ingredientNames.contains(criteria.lowercase())
        }
    }
    fun getCount(): Int {
        return recipes.size
    }

}