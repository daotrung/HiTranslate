package com.daotrung.hitranslate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.daotrung.hitranslate.databinding.ItemViewLanguageBinding
import com.daotrung.hitranslate.model.ListLanguage
import com.daotrung.hitranslate.utils.AppUtils
import com.daotrung.hitranslate.utils.interfaces.ItemListener

class ViewLanguageAdapter(private val itemCall: ItemListener) :
    RecyclerView.Adapter<ViewLanguageAdapter.ViewHolder>(), Filterable {

    private var listAll = mutableListOf<ListLanguage>()
    private var listTemp = mutableListOf<ListLanguage>()

    fun setData(listLanguage: List<ListLanguage>) {
        listAll.clear()
        listAll.addAll(listLanguage)
        listTemp = listAll
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: ItemViewLanguageBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        private val binding = itemView
        fun onBind(item: ListLanguage) {
            val width = (AppUtils.screenWidth()) / 5 - AppUtils.dpToPx(10)
            binding.imvFlag.layoutParams.width = width
            binding.imvFlag.layoutParams.height = width
            binding.imvFlag.setImageResource(item.imgFlag)
            binding.tvName.text = item.txtNameLanguage

            itemView.setOnClickListener {
                itemCall.itemListener(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemViewLanguageBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listAll[position]
        holder.onBind(item)
    }

    override fun getItemCount(): Int {
        return listAll.size
    }

    override fun getFilter(): Filter {
        return searchFilter
    }

    private val searchFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val charString = constraint.toString()
            listAll = if (charString.isEmpty()) {
                listTemp
            } else {
                val filteredList: MutableList<ListLanguage> = ArrayList()
                for (item in listTemp) {
                    if (item.txtNameLanguage.toLowerCase()
                            .contains(charString.toLowerCase().trim())
                    ) {
                        filteredList.add(item)
                    }
                }
                filteredList
            }
            val results = FilterResults()
            results.values = listAll
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            listAll = results.values as MutableList<ListLanguage>
            notifyDataSetChanged()
        }
    }
}