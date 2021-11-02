package org.tensorflow.codelabs.objectdetection

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_spot_result.*

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
            for (i in 0 until ArrayListData.mArrayListSpot.size){
                val img = ArrayListData.mArrayListSpot[i].get("address")!!
                val name = ArrayListData.mArrayListSpot[i].get("name")!!
                val address = ArrayListData.mArrayListSpot[i].get("realaddress")!!
                val link = ArrayListData.mArrayListSpot[i].get("link")!!
                add(SpotProfileData(img = img, name = name, link = link, address = address))
            }
            profileAdapter.datas = datas
            profileAdapter.notifyDataSetChanged()
        }
    }
}