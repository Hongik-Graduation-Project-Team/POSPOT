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
        name.setText(datas.name)
        address.setText(datas.address)
        explain.setText(datas.link)
        val DBaddress = "http://3.35.171.19/" + datas.img
        Glide.with(this).load(DBaddress).into(imageview)
    }
}