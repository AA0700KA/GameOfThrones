package ru.skillbranch.gameofthrones.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.transition.Visibility
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.character_screen.*
import kotlinx.android.synthetic.main.character_screen.view.*
import ru.skillbranch.gameofthrones.AppConfig
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.data.local.entities.RelativeCharacter
import ru.skillbranch.gameofthrones.data.local.entities.toRelativeCharacter
import ru.skillbranch.gameofthrones.data.remote.res.CharacterRes
import ru.skillbranch.gameofthrones.viewmodel.CharacterState
import ru.skillbranch.gameofthrones.viewmodel.CharacterViewModel

class CharacterScreenFragment : Fragment() {

    private var housePosition = 0
    private lateinit var characterId : String
    private lateinit var viewModel : CharacterViewModel
    private lateinit var charName : String

    companion object {
        fun newInstance(character : RelativeCharacter, housePosition : Int) : CharacterScreenFragment {
            val fragment = CharacterScreenFragment()
            val bundle = Bundle()
            bundle.putString("id", character.id)
            bundle.putInt("housePosition", housePosition)
            bundle.putString("name", character.name)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            val bundle = it

            characterId = bundle["id"] as String
            housePosition = bundle["housePosition"] as Int
            charName = bundle["name"] as String
        }

        viewModel = ViewModelProviders.of(this).get(CharacterViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.character_screen, container, false)
        view.toolbar.title = charName
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        activity?.let {
            val activity = it as AppCompatActivity
            viewModel.stateData.observe(this, Observer {

                toolbar.setTitleTextColor(activity.resources.getColor(R.color.white))
                toolbar.setBackgroundColor(activity.resources.getColor(AppConfig.COLORS[it.housePosition]))
                character_logo.setImageDrawable(activity.resources.getDrawable(AppConfig.SPLASHES[it.housePosition]))
                father.setBackgroundColor(activity.resources.getColor(AppConfig.COLORS[it.housePosition]))
                mather.setBackgroundColor(activity.resources.getColor(AppConfig.COLORS[it.housePosition]))

                renderUi(it)
            })

            activity.setSupportActionBar(toolbar)
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            viewModel.updateState(characterId, housePosition)
        }

    }

    private fun renderUi(characterState: CharacterState) {

        with(characterState) {
            Log.d("Character", "renderUi: ${characterFull.name}")
            aliases_content.text =characterFull.aliases.toString()
                    .replace("[", "")
                    .replace("]", "")
                    .replace(",", " ")

            titles_content.text = characterFull.titles.toString()
                    .replace("[", "")
                    .replace("]", "")
                    .replace(",", " ")

            born_content.text = characterFull.born
            words_content.text = characterFull.words

            renderButton(characterFull.father, father)

            renderButton(characterFull.mother, mather)

            if (!characterFull.died.isEmpty()) {
                Snackbar.make(character_coordinator, characterFull.died, Snackbar.LENGTH_INDEFINITE).show()
            }

        }
    }

    private fun renderButton(relativeCharacter: RelativeCharacter?, button : Button) {
        if (relativeCharacter == null) {
            button.visibility = View.GONE
        } else {
            button.text = relativeCharacter.name
            button.setOnClickListener {

                activity?.let {
                    val activity = it as AppCompatActivity
                    activity.supportFragmentManager
                            .beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.root_container, newInstance(relativeCharacter, housePosition))
                            .commit()
                }

            }
        }
    }



}