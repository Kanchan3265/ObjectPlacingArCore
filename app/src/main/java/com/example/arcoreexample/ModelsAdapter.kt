package com.example.arcoreexample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView

class ModelsAdapter(
    private val list: List<Model3D>,
    private val liveData: MutableLiveData<Model3D>
) : RecyclerView.Adapter<ModelsAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textView = view.findViewById<TextView>(R.id.modelNameTv)

        init {
            view.setOnClickListener {
                liveData.value = list[adapterPosition]
            }
        }

        fun bind(model3D: Model3D) {
            textView.text = model3D.modelName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder =
        MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.model_item, parent, false)
        )

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}