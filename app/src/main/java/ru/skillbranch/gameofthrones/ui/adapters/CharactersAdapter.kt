package ru.skillbranch.gameofthrones.ui.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.characters_item.view.*
import ru.skillbranch.gameofthrones.AppConfig
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.data.remote.res.CharacterRes
import ru.skillbranch.gameofthrones.viewmodel.CharactersViewModel

class CharactersAdapter(val context: Context, val showInfo: IShowInfo, val housePosition : Int) : RecyclerView.Adapter<CharactersAdapter.CharactersHolder>() {

   private var items : MutableList<CharacterItem> = mutableListOf()

    val TAG = "CHARCATER"

    class CharactersHolder(view : View) : RecyclerView.ViewHolder(view)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharactersHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.characters_item, parent, false)
        return CharactersHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CharactersHolder, position: Int) {
        holder.itemView.name.text = items[position].name
        Log.d(TAG, "Titles ${items[position].titles} and size = ${items[position].titles.size} ")
        holder.itemView.gender.text = items[position].simpleTitles

        holder.itemView.icon.setImageDrawable(context.resources.getDrawable(AppConfig.ICONS[housePosition]))

        holder.itemView.card_view.setOnClickListener {
            showInfo.showInfo(items[position], housePosition)
        }
    }

    fun addData(characters : List<CharacterItem>) {
        Log.d(TAG, "addData: new data ${characters}")

        items.clear()


        items.addAll(characters)
        notifyDataSetChanged()
    }

    interface IShowInfo {

        fun showInfo(character: CharacterItem, housePosition: Int)

    }

}