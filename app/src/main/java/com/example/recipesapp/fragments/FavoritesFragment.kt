package com.example.recipesapp.fragments

import com.example.recipesapp.models.Recipes
import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.recipesapp.models.Favorite
import com.example.recipesapp.models.Favorites
import com.example.recipesapp.interfaces.RecyclerViewInterface
import com.example.recipesapp.R
import com.example.recipesapp.models.Recipe
import com.example.recipesapp.adapters.DataAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class FavoritesFragment:Fragment(), RecyclerViewInterface {
    private lateinit var list: RecyclerView
    private lateinit var adapter: DataAdapter
    private lateinit var recipes: Recipes
    private lateinit var favorites: Favorites
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_favorites, container, false)
        setUpViews(rootView)
        configSwipe(rootView)
        return rootView
    }

    private fun configSwipe(rootView: View) {
        val swipe = rootView.findViewById<SwipeRefreshLayout>(R.id.swipe)
        swipe.setColorSchemeResources(R.color.teal_700)
        swipe.setOnRefreshListener {
            Handler(Looper.getMainLooper()).postDelayed({
                swipe.isRefreshing=false
                setUpViews(rootView)
            },1500)
        }
    }

   private fun setUpViews(rootView:View)
   {
       list = rootView.findViewById(R.id.favoritesListView)
       list.layoutManager = LinearLayoutManager(activity)
       list.itemAnimator = DefaultItemAnimator()
       recipes = Recipes()
       favorites = Favorites()
       val user = auth.currentUser
       var email = ""
       if(user!=null)
       {
           email = user.email.toString()
       }
       db.collection("recipes")
           .get()
           .addOnSuccessListener {result->
               for(document in result)
               {
                   val recipe = document.toObject(Recipe::class.java).copy(id=document.id)
                   recipes.addRecipe(recipe)
               }
               db.collection("favorites")
                   .whereEqualTo("userEmail",email)
                   .get()
                   .addOnSuccessListener { resultFav ->
                       for (document in resultFav)
                       {
                           val favorite = document.toObject(Favorite::class.java).copy(id = document.id)
                           favorites.addFavRecipe(favorite)
                       }
                       val favoriteRecipeIds = favorites.getFavRecipes().map { it.recipeId }
                       val favoriteRecipes = recipes.getRecipes().filter { it.id in favoriteRecipeIds }
                       adapter = DataAdapter(requireActivity(), R.layout.layout_recipe, favoriteRecipes.toTypedArray(), this)
                       list.adapter = adapter
                   }
                   .addOnFailureListener { exception ->
                       Log.w(TAG, "Error getting documents.", exception)
                   }
           }
   }

    override fun onClickItem(position: Int) {
        val recipe = recipes.getRecipe(position)
        val action = FavoritesFragmentDirections.actionFavoritesFragmentToRecipeDetailsFragment(
            recipe.image,
            recipe.ingredients.toTypedArray(),
            recipe.instructions.toTypedArray()
        )
        findNavController().navigate(action)
    }
}