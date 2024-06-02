package com.example.glucoguardapp.ui.theme.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.glucoguardapp.R
import com.example.glucoguardapp.databinding.ItemAdapterBinding
import com.example.glucoguardapp.model.Medication

class MedicationAdapter(
    private val context: Context,
    private val medicationList: List<Medication>,
    val medicationSelected: (Medication, Int) -> Unit
) : RecyclerView.Adapter<MedicationAdapter.MyViewHolder>() {

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
        val medication = medicationList[position]

        val colorCard = ContextCompat.getColor(context, R.color.color_3)

        holder.binding.textDescription.text = "Name: " + medication.name + "\nDosage: " + medication.dosage

        holder.binding.btnDelete.setOnClickListener { medicationSelected(medication, SELECT_REMOVE) }
        holder.binding.btnEdit.setOnClickListener { medicationSelected(medication, SELECT_EDIT) }
        holder.binding.btnDetails.setOnClickListener { medicationSelected(medication, SELECT_DETAILS) }

        holder.binding.cardview.setCardBackgroundColor(colorCard)
    }

    override fun getItemCount() = medicationList.size

    inner class MyViewHolder(val binding: ItemAdapterBinding) :
        RecyclerView.ViewHolder(binding.root)

}
