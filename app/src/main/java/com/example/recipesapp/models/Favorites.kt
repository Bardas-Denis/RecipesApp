package com.example.recipesapp.models

class Favorites {
    private val favorites: MutableList<Favorite> = mutableListOf()

    fun addFavRecipe(fav: Favorite){
        favorites.add(fav)
    }
    fun getFavRecipes(): MutableList<Favorite> {
        return favorites
    }

    fun getFavRecipe(index: Int): Favorite {
        return favorites[index]
    }
}