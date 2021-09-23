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

        //datas = intent.getParcelableExtra("data")!!

        //Glide.with(this).load(datas.img).into(img)
        //tv_name.text = datas.name
    }
}