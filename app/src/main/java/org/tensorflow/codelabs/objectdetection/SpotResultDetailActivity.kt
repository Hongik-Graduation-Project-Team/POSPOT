package org.tensorflow.codelabs.objectdetection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_spot_result_detail.*

class SpotResultDetailActivity : AppCompatActivity() {
    lateinit var datas : SpotProfileData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spot_result_detail)
        overridePendingTransition(0, 0)

        datas = intent.getParcelableExtra("data")!!
        viewpager.adapter = SpotDetailAdaptor(getSpotList())
        name.setText(datas.name)
        address.setText(datas.address)
    }
    private fun getSpotList(): ArrayList<Int> {
        return arrayListOf<Int>(datas.img1, datas.img2, datas.img3, datas.img4, datas.img5)
    }
}