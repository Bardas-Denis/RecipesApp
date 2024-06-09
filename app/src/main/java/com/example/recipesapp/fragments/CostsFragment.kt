package com.example.recipesapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.recipesapp.R
import com.example.recipesapp.activities.dataStore
import com.example.recipesapp.adapters.CostsAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class CostsFragment: Fragment() {
    private lateinit var addButton: Button
    private lateinit var ingredientEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var costsListView: ListView
    private lateinit var costTextView: TextView
    private var ingredients: MutableMap<String,String> = mutableMapOf()
    private val MAP_DATA_KEY = stringPreferencesKey(Firebase.auth.currentUser?.email.toString()+"costs")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_costs, container, false)
        setUpViews(rootView)
        return rootView
    }
    private fun setUpViews(rootView: View)
    {
        ingredientEditText = rootView.findViewById(R.id.addIngredientEditText)
        ingredientEditText.requestFocus()
        priceEditText = rootView.findViewById(R.id.priceEditText)
        costsListView = rootView.findViewById(R.id.costsListView)
        costTextView = rootView.findViewById(R.id.costTextView)
        addButton = rootView.findViewById(R.id.addButton)

        getIngredients()

        addButton.setOnClickListener {
            lifecycleScope.launch {
                saveTextView(ingredientEditText.text.toString(), priceEditText.text.toString())
                ingredientEditText.text.clear()
                priceEditText.text.clear()
            }
        }
    }
    private fun getIngredients()
    {
        val mapDataFlow: Flow<MutableMap<String, String>> = requireContext().dataStore.data.map { ing ->
            val serializedData = ing[MAP_DATA_KEY]
            Gson().fromJson(serializedData, object : TypeToken<MutableMap<String, String>>() {}.type)
                ?: mutableMapOf()
        }
        lifecycleScope.launch { mapDataFlow.collect{ ing->
            ingredients = ing
            handleData(ingredients) }
        }
    }

    private suspend fun saveTextView(textViewKey: String, price: String)
    {
        ingredients[textViewKey]=price
        handleData(ingredients)
        val serializedData = Gson().toJson(ingredients)
        requireContext().dataStore.edit { costs->
            costs[MAP_DATA_KEY] = serializedData
        }
    }

    private fun handleData(ingredients: MutableMap<String,String>)
    {
        val adapter = CostsAdapter(requireContext(), ingredients) { key ->
            lifecycleScope.launch {
                deleteIngredient(key)
            }
        }
        costsListView.adapter = adapter
        calculateCosts()
    }

    private fun calculateCosts()
    {
        var sum = 0.0
        for (p in ingredients) {
            val price = p.value.toDoubleOrNull()
            if (price != null) {
                sum += price
            }
        }
        costTextView.text = getString(R.string.total_cost,sum.toString())
    }

    private suspend fun deleteIngredient(key: String) {
        ingredients.remove(key)
        handleData(ingredients)
        val serializedData = Gson().toJson(ingredients)
        requireContext().dataStore.edit { costs ->
            costs[MAP_DATA_KEY] = serializedData
        }
    }
}