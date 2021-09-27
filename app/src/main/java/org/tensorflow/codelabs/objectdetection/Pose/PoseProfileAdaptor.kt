package org.tensorflow.codelabs.objectdetection

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PoseProfileAdapter(private val context: Context) : RecyclerView.Adapter<PoseProfileAdapter.ViewHolder>() {

    var datas = mutableListOf<PoseProfileData>()

    interface OnItemClickListener{
        fun onItemClick(v:View, data: PoseProfileData, pos : Int)
    }
    private var listener : OnItemClickListener? = null
    fun setOnItemClickListener(listener : OnItemClickListener) {
        this.listener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.pose_recycler,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = datas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val txtobj: TextView = itemView.findViewById(R.id.obj)
        private val imgProfile: ImageView = itemView.findViewById(R.id.pose_photo)

        fun bind(item: PoseProfileData) {
            txtobj.text = item.obj
            Glide.with(itemView).load(item.img).into(imgProfile)

            val pos = adapterPosition
            if(pos!= RecyclerView.NO_POSITION)
            {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView,item,pos)
                }
            }
        }
    }
}