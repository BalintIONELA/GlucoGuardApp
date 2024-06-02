package com.example.glucoguardapp.ui.theme.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.glucoguardapp.R
import com.example.glucoguardapp.databinding.ItemAdapterBinding
import com.example.glucoguardapp.model.Glycemia

class GlycemiaAdapter(
    private val context: Context,
    private val glycemiaList: List<Glycemia>,
    val glycemiaSelected: (Glycemia, Int) -> Unit
) : RecyclerView.Adapter<GlycemiaAdapter.MyViewHolder>() {

    private var glycemiaListM = glycemiaList.toMutableList()

    companion object {
        val SELECT_BACK: Int = 1
        val SELECT_REMOVE: Int = 2
        val SELECT_EDIT: Int = 3
        val SELECT_DETAILS: Int = 4
        val SELECT_NEXT: Int = 5
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemAdapterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val glycemia = glycemiaListM[position]
        val colorUnregulatedGlucose = ContextCompat.getColor(context, R.color.color_1)
        val colorRegulatedGlucose = ContextCompat.getColor(context, R.color.color_2)

        holder.binding.textDescription.text =
            "Date: " + glycemia.day + "/" + glycemia.month + "/" + glycemia.year +
                    "\nBlood Glucose Level: " + glycemia.glucoseLevel + "mg/dL" +
                    "\nDescription: " + glycemia.description

        if (glycemia.glucoseLevel.toDouble() >= 200 || glycemia.glucoseLevel.toDouble() <= 75) {
            holder.binding.cardview.setCardBackgroundColor(colorUnregulatedGlucose)
        } else {
            holder.binding.cardview.setCardBackgroundColor(colorRegulatedGlucose)
        }

        holder.binding.btnDelete.setOnClickListener { glycemiaSelected(glycemia, SELECT_REMOVE) }
        holder.binding.btnEdit.setOnClickListener { glycemiaSelected(glycemia, SELECT_EDIT) }
        holder.binding.btnDetails.setOnClickListener { glycemiaSelected(glycemia, SELECT_DETAILS) }
    }

    override fun getItemCount() = glycemiaListM.size

    fun searchGlycemia(query: String): Boolean {
        glycemiaListM.clear()

        glycemiaListM.addAll(glycemiaList.filter { it.glucoseLevel.contains(query, true) })

        notifyDataSetChanged()

        return glycemiaListM.isEmpty()
    }

    fun clearSearchGlycemia() {
        glycemiaListM = glycemiaList.toMutableList()
        notifyDataSetChanged()
    }

    inner class MyViewHolder(val binding: ItemAdapterBinding) :
        RecyclerView.ViewHolder(binding.root)
}
