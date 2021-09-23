package org.tensorflow.codelabs.objectdetection

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class SpotProfileAdapter(private val context: Context) : RecyclerView.Adapter<SpotProfileAdapter.ViewHolder>() {

    var datas = mutableListOf<SpotProfileData>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.spot_recycler,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = datas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val txtName: TextView = itemView.findViewById(R.id.name)
        private val txtAge: TextView = itemView.findViewById(R.id.address)
        private val imgProfile: ImageView = itemView.findViewById(R.id.photo)

        fun bind(item: SpotProfileData) {
            txtName.text = item.name
            txtAge.text = item.address
            Glide.with(itemView).load(item.img).apply(RequestOptions.bitmapTransform(RoundedCorners(50))).into(imgProfile)

            itemView.setOnClickListener {
                Intent(context, SpotResultDetailActivity::class.java).apply {
                    putExtra("data", item)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.run { context.startActivity(this) }
            }
        }
    }
}