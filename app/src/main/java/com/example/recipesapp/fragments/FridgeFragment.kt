package com.example.recipesapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.recipesapp.R
import com.example.recipesapp.activities.dataStore
import com.example.recipesapp.adapters.FridgeAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class FridgeFragment: Fragment() {
    private lateinit var addButton: Button
    private lateinit var ingredientEditText: EditText
    private lateinit var costsListView: ListView
    private var ingredients: MutableList<String> = mutableListOf()
    private val MAP_DATA_KEY = stringPreferencesKey(Firebase.auth.currentUser?.email.toString())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_fridge, container, false)
        setUpViews(rootView)
        return rootView
    }

    private fun setUpViews(rootView: View)
    {
        ingredientEditText = rootView.findViewById(R.id.addIngredientEditText)
        costsListView = rootView.findViewById(R.id.costsListView)
        addButton = rootView.findViewById(R.id.addButton)

        getIngredients()

        addButton.setOnClickListener {
            lifecycleScope.launch {
                saveTextView(ingredientEditText.text.toString())
                ingredientEditText.text.clear()
            }
        }
    }

    private fun getIngredients()
    {
        val listDataFlow: Flow<MutableList<String>> = requireContext().dataStore.data.map { ing ->
            val serializedData = ing[MAP_DATA_KEY]
            Gson().fromJson(serializedData, object : TypeToken<MutableList<String>>() {}.type)
                ?: mutableListOf()
        }
        lifecycleScope.launch { listDataFlow.collect{ ing->
            ingredients = ing
            handleData(ingredients)
        } }
    }

    private suspend fun saveTextView(textViewKey: String)
    {
        ingredients.add(textViewKey)
        handleData(ingredients)
        val serializedData = Gson().toJson(ingredients)
        requireContext().dataStore.edit { costs->
            costs[MAP_DATA_KEY] = serializedData
        }
    }

    private fun handleData(ingredients: MutableList<String>)
    {
        val fridgeAdapter = FridgeAdapter(requireContext(), ingredients) { deletedIngredient ->
            lifecycleScope.launch {
                deleteIngredient(deletedIngredient)
            }
        }
        costsListView.adapter = fridgeAdapter
    }

    private suspend fun deleteIngredient(key: String)
    {
        ingredients.remove(key)
        handleData(ingredients)
        val serializedData = Gson().toJson(ingredients)
        requireContext().dataStore.edit { costs ->
            costs[MAP_DATA_KEY] = serializedData
        }
    }
}