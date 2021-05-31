package ru.skillbranch.gameofthrones.ui.fragments

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.houses_screen.*
import ru.skillbranch.gameofthrones.AppConfig
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.data.remote.res.CharacterRes
import ru.skillbranch.gameofthrones.ui.RootActivity
import ru.skillbranch.gameofthrones.ui.adapters.CharactersAdapter
import ru.skillbranch.gameofthrones.viewmodel.CharactersViewModel


class HousesScreenFragment : Fragment() {
    
    val TAG = "Tab"

    private var position = 0
    private lateinit var viewModel: CharactersViewModel


    companion object {


        fun getInstance(position : Int) : HousesScreenFragment {


            val fragment = HousesScreenFragment()
            val bundle = Bundle()
            bundle.putInt("position", position)
            fragment.arguments = bundle
            return fragment
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CharactersViewModel::class.java)
        position = arguments?.getInt("position") ?: 0
        Log.d(TAG, "onCreate: Fragment ${position} ")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.houses_screen, container, false)
        setHasOptionsMenu(true)
        Log.d(TAG, "onCreateView: Fragment ${position}")
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Log.d(TAG, "onActivityCreated: Fragment ${position}")

        activity?.let {
            val activity = it as AppCompatActivity

            Log.d(TAG, "onActivityCreated: Recycler ${position}")

            val linearLayoutManager = LinearLayoutManager(activity.applicationContext)
            linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            recycler_view.layoutManager = linearLayoutManager

            val adapter = CharactersAdapter(activity.applicationContext, activity as RootActivity, position)
            recycler_view.adapter = adapter


              viewModel.charactersListState.observe(this, Observer {
                  Log.d(TAG + "Search", "State ${it} position ${position}")

                  if (it.isLoadedFromNetwork) {
                      viewModel.loadCharactersByPosition(position)
                      return@Observer
                  }

                  if (!it.isSearchMode) {
                      adapter.addData(it.data)
                  } else {
                      adapter.addData(it.searchResults)
                  }

                  if (adapter.itemCount == 0) {
                      not_found_data_info.visibility = View.VISIBLE
                  } else {
                      not_found_data_info.visibility = View.GONE
                  }

                  loading_progress.visibility = View.GONE

              })

              viewModel.loadCharactersByPosition(position)

        }


    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = (searchItem.actionView as SearchView)

        viewModel.currentState?.let {
            if (it.isSearchMode) {
                searchItem.expandActionView()
                searchView.setQuery(it.searchQuery, false)
                searchView.requestFocus()
            } else {
                searchView.clearFocus()
            }
        }


        searchView.setOnSearchClickListener {
            Toast.makeText(activity, "Search click", Toast.LENGTH_SHORT).show()
            viewModel.handleSearchMode(true)
        }


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("Query", "onQueryTextSubmit: ${query}")
                if (viewModel.currentState?.isSearchMode!!) viewModel.handleSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("Query", "onQueryTextChange: ${newText}")
                newText?.let {
                    if (it.isEmpty()) {
                        viewModel.handleSearchMode(false)
                    } else {
                        viewModel.handleSearchMode(true)
                    }

                    if (viewModel.currentState?.isSearchMode!! && !it.equals(viewModel.currentState?.searchQuery)) {
                        viewModel.handleSearch(it)
                    }
                }

                return true
            }

        })
        

       // super.onCreateOptionsMenu(menu, inflater)
    }



}


