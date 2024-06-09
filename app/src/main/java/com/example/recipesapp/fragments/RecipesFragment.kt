package com.example.recipesapp.fragments

import com.example.recipesapp.models.Recipe
import com.example.recipesapp.models.Recipes
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipesapp.interfaces.RecyclerViewInterface
import com.example.recipesapp.R
import com.example.recipesapp.activities.dataStore
import com.example.recipesapp.adapters.DataAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class RecipesFragment : Fragment(), RecyclerViewInterface {
    private lateinit var list: RecyclerView
    private lateinit var adapter: DataAdapter
    private lateinit var recipes: Recipes
    private lateinit var ingredients: MutableList<String>
    private lateinit var selectedSort: String
    private lateinit var selectedFilter: String
    private lateinit var selectedFilterType: String
    private lateinit var sortSpinner: Spinner
    private lateinit var filterTypeSpinner: Spinner
    private lateinit var filterSpinner: Spinner
    private lateinit var sortAdapter: ArrayAdapter<String>
    private lateinit var filterTypeAdapter: ArrayAdapter<String>
    private lateinit var filterAdapter: ArrayAdapter<String>
    private lateinit var searchET: EditText
    private lateinit var searchButton: ImageButton

    private val sort: Array<String> by lazy {
        requireContext().resources.getStringArray(R.array.sort_options)
    }
    private val filterTypes: Array<String> by lazy {
        requireContext().resources.getStringArray(R.array.filter_types)
    }
    private val filtersTime: Array<String> by lazy {
        requireContext().resources.getStringArray(R.array.filters_time)
    }
    private val filtersPrice: Array<String> by lazy {
        requireContext().resources.getStringArray(R.array.filters_price)
    }
    private val filtersOrigin: Array<String> by lazy {
        requireContext().resources.getStringArray(R.array.filters_origin)
    }
    private val filtersDiet: Array<String> by lazy {
        requireContext().resources.getStringArray(R.array.filters_diet)
    }
    private val filtersAllergens: Array<String> by lazy {
        requireContext().resources.getStringArray(R.array.filters_allergens)
    }
    private val filtersDifficulty: Array<String> by lazy {
        requireContext().resources.getStringArray(R.array.filters_difficulty)
    }
    private val filtersType: Array<String> by lazy {
        requireContext().resources.getStringArray(R.array.filters_type)
    }
    private val filtersFridge: Array<String> by lazy {
        requireContext().resources.getStringArray(R.array.filters_fridge)
    }

    private val db = Firebase.firestore
    private val MAP_DATA_KEY = stringPreferencesKey(Firebase.auth.currentUser?.email.toString())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_recipes, container, false)
        setUpViews(rootView)
        return rootView
    }

    private fun setUpViews(rootView:View)
    {
        list = rootView.findViewById(R.id.listView)
        list.layoutManager = LinearLayoutManager(activity)
        list.itemAnimator = DefaultItemAnimator()
        lifecycleScope.launch {
            ingredients = fetchData()
        }
        getRecipes(rootView)
    }

    private fun getRecipes(rootView: View)
    {
        recipes = Recipes()
        db.collection("recipes")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val recipe = document.toObject(Recipe::class.java).copy(id = document.id)
                    recipes.addRecipe(recipe)
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
                adapter = DataAdapter(requireActivity(), R.layout.layout_recipe, recipes.getRecipes().toTypedArray(), this)
                list.adapter = adapter

                searchET = rootView.findViewById(R.id.searchEditText)
                searchButton = rootView.findViewById(R.id.searchImageButton)
                var searchedRecipes = recipes.getRecipes()
                searchButton.setOnClickListener {
                    val searchText = searchET.text.toString().trim()
                    if(::sortSpinner.isInitialized)
                        sortSpinner.setSelection(0)
                    if(::filterTypeSpinner.isInitialized)
                        filterTypeSpinner.setSelection(0)
                    if (searchText.isNotEmpty())
                    {
                        searchedRecipes = recipes.searchRecipes(searchText).toMutableList()
                        searchET.setText("")
                    }
                    else
                    {
                        searchedRecipes = recipes.getRecipes()
                    }
                    adapter.updateData(searchedRecipes.toTypedArray())
                    adapter.notifyDataSetChanged()
                }

                sortSpinner = rootView.findViewById(R.id.sortSpinner)
                sortAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, sort)
                sortSpinner.adapter = sortAdapter

                sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                        selectedSort = sortSpinner.selectedItem.toString()
                        val sortedRecipes = when (selectedSort) {
                            "Time - ascending" -> recipes.sortRecipes(adapter.getData(),"TimeAscending")
                            "Time - descending" -> recipes.sortRecipes(adapter.getData(),"TimeDescending")
                            "Price - ascending" -> recipes.sortRecipes(adapter.getData(),"PriceAscending")
                            "Price - descending" -> recipes.sortRecipes(adapter.getData(),"PriceDescending")
                            else -> adapter.getData().toList()
                        }
                        adapter.updateData(sortedRecipes.toTypedArray())
                        adapter.notifyDataSetChanged()
                    }
                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }

                filterTypeSpinner = rootView.findViewById(R.id.filterSpinner)
                filterTypeAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, filterTypes)
                filterTypeSpinner.adapter = filterTypeAdapter

                filterTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                        filterSpinner = rootView.findViewById(R.id.filtersSpinner)
                        selectedFilterType = filterTypeSpinner.selectedItem.toString()

                        val parentID = parent.id
                        if (parentID == R.id.filterSpinner) {
                            filterAdapter = when (selectedFilterType) {
                                "Time" -> ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, filtersTime)
                                "Price" -> ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, filtersPrice)
                                "Origin" -> ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, filtersOrigin)
                                "Diet" -> ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, filtersDiet)
                                "Allergens" -> ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, filtersAllergens)
                                "Difficulty" -> ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, filtersDifficulty)
                                "Type" -> ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, filtersType)
                                "Suggest based of ingredients in fridge" ->ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, filtersFridge)
                                else -> ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, arrayOf())
                            }
                        }
                        filterSpinner.adapter = filterAdapter

                        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                selectedFilter = filterSpinner.selectedItem.toString()
                                if(::sortSpinner.isInitialized)
                                    sortSpinner.setSelection(0)
                                val filteredRecipes = when (selectedFilter) {
                                    //Time
                                    "Less than 15 min" -> recipes.filterRecipes(searchedRecipes.toTypedArray(),"Less than 15min")
                                    "Less than 30 min" -> recipes.filterRecipes(searchedRecipes.toTypedArray(),"Less than 30min")
                                    "Less than 45 min" -> recipes.filterRecipes(searchedRecipes.toTypedArray(),"Less than 45min")
                                    "Less than 1 h" -> recipes.filterRecipes(searchedRecipes.toTypedArray(),"Less than 1h")
                                    "Less than 1.5 h" -> recipes.filterRecipes(searchedRecipes.toTypedArray(),"Less than 1.5h")
                                    "Less than 2.5 h" -> recipes.filterRecipes(searchedRecipes.toTypedArray(),"Less than 2.5h")
                                    //Price
                                    "Less than 5$" -> recipes.filterRecipes(searchedRecipes.toTypedArray(),"Less than 5$")
                                    "Less than 15$" -> recipes.filterRecipes(searchedRecipes.toTypedArray(),"Less than 15$")
                                    "Less than 25$" -> recipes.filterRecipes(searchedRecipes.toTypedArray(),"Less than 25$")
                                    "Less than 40$" -> recipes.filterRecipes(searchedRecipes.toTypedArray(),"Less than 40$")
                                    "Less than 60$" -> recipes.filterRecipes(searchedRecipes.toTypedArray(),"Less than 60$")
                                    "Less than 100$" -> recipes.filterRecipes(searchedRecipes.toTypedArray(),"Less than 100$")
                                    //Origin
                                    "Mediterranean" -> recipes.filterRecipes(searchedRecipes.toTypedArray(),"Mediterranean")
                                    "Arabic" -> recipes.filterRecipes(searchedRecipes.toTypedArray(),"Arabic")
                                    "Chinese" -> recipes.filterRecipes(searchedRecipes.toTypedArray(),"Chinese")
                                    "Japanese" -> recipes.filterRecipes(searchedRecipes.toTypedArray(),"Japanese")
                                    //Diet
                                    "Vegetarian" -> recipes.filterRecipes(searchedRecipes.toTypedArray(),"Vegetarian")
                                    "Vegan" -> recipes.filterRecipes(searchedRecipes.toTypedArray(),"Vegan")
                                    //Allergens
                                    "Milk" -> recipes.filterRecipes(searchedRecipes.toTypedArray(),"Milk")
                                    "Eggs" -> recipes.filterRecipes(searchedRecipes.toTypedArray(),"Eggs")
                                    "Fish" -> recipes.filterRecipes(searchedRecipes.toTypedArray(),"Fish")
                                    "Nuts" -> recipes.filterRecipes(searchedRecipes.toTypedArray(),"Nuts")
                                    "Peanuts" -> recipes.filterRecipes(searchedRecipes.toTypedArray(),"Peanuts")
                                    "Gluten" -> recipes.filterRecipes(searchedRecipes.toTypedArray(),"Gluten")
                                    "Crustaceans" -> recipes.filterRecipes(searchedRecipes.toTypedArray(),"Crustaceans")
                                    "Mustard" -> recipes.filterRecipes(searchedRecipes.toTypedArray(),"Mustard")
                                    "Soybeans" -> recipes.filterRecipes(searchedRecipes.toTypedArray(),"Soybeans")
                                    //Difficulty
                                    "Low" -> recipes.filterRecipes(searchedRecipes.toTypedArray(),"Low")
                                    "Medium" -> recipes.filterRecipes(searchedRecipes.toTypedArray(),"Medium")
                                    "High" -> recipes.filterRecipes(searchedRecipes.toTypedArray(),"High")
                                    //Type
                                    "Savoury" -> recipes.filterRecipes(searchedRecipes.toTypedArray(),"Savoury")
                                    "Dessert" -> recipes.filterRecipes(searchedRecipes.toTypedArray(),"Dessert")
                                    //Fridge
                                    "At least 3 ingredients" -> recipes.suggestRecipes(ingredients.toTypedArray(),"At least 3 ingredients")
                                    "At least 5 ingredients" -> recipes.suggestRecipes(ingredients.toTypedArray(),"At least 5 ingredients")
                                    "At least 7 ingredients" -> recipes.suggestRecipes(ingredients.toTypedArray(),"At least 7 ingredients")
                                    else -> searchedRecipes
                                }
                                // Update the adapter with the filtered data
                                adapter.updateData(filteredRecipes.toTypedArray())
                                adapter.notifyDataSetChanged()
                            }

                            override fun onNothingSelected(parent: AdapterView<*>) {}
                        }
                    }
                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    override fun onClickItem(position: Int) {
        val recipe = recipes.getRecipe(position)
        Toast.makeText(activity, "Selected ${recipes.getRecipe(position).name}", Toast.LENGTH_SHORT).show()
        val action = RecipesFragmentDirections.actionRecipesFragmentToRecipeDetailsFragment(
            recipe.image,
            recipe.ingredients.toTypedArray(),
            recipe.instructions.toTypedArray()
        )
        findNavController().navigate(action)
    }

    private suspend fun fetchData(): MutableList<String> {
        val dataStore = requireContext().dataStore
        val listDataFlow: Flow<MutableList<String>> = dataStore.data.map { ing ->
            val serializedData = ing[MAP_DATA_KEY]
            Gson().fromJson(serializedData, object : TypeToken<MutableList<String>>() {}.type)
                ?: mutableListOf()
        }
        return listDataFlow.first()
    }
}
