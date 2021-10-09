package org.tensorflow.codelabs.objectdetection

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_spot_result.*

class SpotResultActivity : AppCompatActivity() {
    private lateinit var profileAdapter: SpotProfileAdapter
    private lateinit var yolo: ArrayList<String>
    private lateinit var res: String
    private val datas = mutableListOf<SpotProfileData>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spot_result)
        overridePendingTransition(0, 0)

        res = intent.getStringExtra("res")!!
        yolo = intent.getStringArrayListExtra("yolo")!!

        initRecycler()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initRecycler() {
        profileAdapter = SpotProfileAdapter(this)
        spot_profile.adapter = profileAdapter

        datas.apply {

            for (i in 0 until mArrayListSpot.size){
                val img = mArrayListSpot[i].get("address")
                val name = mArrayListSpot[i].get("id")
                add(SpotProfileData(img = img!!, name = name!!, explain = "aaaa", address = "bbbb"))
            }

            //val img = mArrayList[0].get("address")
            add(SpotProfileData(img = "C:/avenue",name = maxYoloLabel[0], explain = "Asdasd", address = "asdasd"))
            profileAdapter.datas = datas
            profileAdapter.notifyDataSetChanged()
        }
    }
}