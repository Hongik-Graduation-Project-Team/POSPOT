package org.tensorflow.codelabs.objectdetection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.spot_detail.view.*

class SpotDetailAdaptor(SpotList: ArrayList<Int>) : RecyclerView.Adapter<SpotDetailAdaptor.PagerViewHolder>() {
    var item = SpotList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PagerViewHolder((parent))

    override fun getItemCount(): Int = item.size

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.Spot.setImageResource(item[position])
    }

    inner class PagerViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder
        (LayoutInflater.from(parent.context).inflate(R.layout.spot_detail, parent, false)){

        val Spot = itemView.spot_detail_view!!
    }
}