package org.tensorflow.codelabs.objectdetection

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_spot_result.*
import kotlinx.android.synthetic.main.activity_spot_result_detail.*

class SpotResultActivity : AppCompatActivity() {
    private lateinit var profileAdapter: SpotProfileAdapter
    private val datas = mutableListOf<SpotProfileData>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spot_result)
        overridePendingTransition(0, 0)

        initRecycler()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initRecycler() {
        profileAdapter = SpotProfileAdapter(this)
        spot_profile.adapter = profileAdapter

        datas.apply {
            for (i in 0 until mArrayListSpot.size){
                val img = mArrayListSpot[i].get("address")!!
                val name = mArrayListSpot[i].get("name")!!
                val address = mArrayListSpot[i].get("realaddress")!!
                val link = mArrayListSpot[i].get("link")!!
                Log.i("dddddd",name)
                add(SpotProfileData(img = img, name = name, link = link, address = address))
            }
            mArrayListSpot.clear()
            profileAdapter.datas = datas
            profileAdapter.notifyDataSetChanged()
        }
    }
}