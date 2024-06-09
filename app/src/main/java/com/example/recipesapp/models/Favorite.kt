package com.example.recipesapp.models

import com.google.firebase.firestore.Exclude
import java.io.Serializable

data class Favorite(@get:Exclude var id: String, var recipeId: String, var userEmail: String) : Serializable {
    constructor() : this("","","")

    override fun toString(): String {
        return "(id='$id', recipe=$recipeId,email=$userEmail')"
    }
}