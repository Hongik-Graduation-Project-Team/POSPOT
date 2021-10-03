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
            for (i in 0 until mArrayList.size){

                val address = mArrayList[i].get("address")
                val id = mArrayList[i].get("id")
                add(SpotProfileData(img = address!!, name = id!!, address = "aaaa", explain = "bbbb"))
            }
            profileAdapter.datas = datas
            profileAdapter.notifyDataSetChanged()
        }
        datas.apply {
            /*
            add(SpotProfileData(img1 = R.drawable.building, img2 = R.drawable.empty,img3 = R.drawable.empty,
                img4 = R.drawable.empty,img5 = R.drawable.empty, name = "resnet 라벨값", address = res))
            add(SpotProfileData(img1 = R.drawable.waterfall, img2 = R.drawable.empty,img3 = R.drawable.empty,
                img4 = R.drawable.empty,img5 = R.drawable.empty, name = "yolo 라벨값", address = yolo2))
            add(SpotProfileData(img1 = R.drawable.catholic_church, img2 = R.drawable.empty,img3 = R.drawable.empty,
                img4 = R.drawable.empty,img5 = R.drawable.empty, name = "3", address = "3"))
            add(SpotProfileData(img1 = R.drawable.odd, img2 = R.drawable.empty,img3 = R.drawable.empty,
                img4 = R.drawable.empty,img5 = R.drawable.empty, name = "4", address = "4"))
            add(SpotProfileData(img1 = R.drawable.odd, img2 = R.drawable.empty,img3 = R.drawable.empty,
                img4 = R.drawable.empty,img5 = R.drawable.empty, name = "5", address = "5"))
            add(SpotProfileData(img1 = R.drawable.odd, img2 = R.drawable.empty,img3 = R.drawable.empty,
                img4 = R.drawable.empty,img5 = R.drawable.empty, name = "6", address = "6"))
            add(SpotProfileData(img1 = R.drawable.odd, img2 = R.drawable.empty,img3 = R.drawable.empty,
                img4 = R.drawable.empty,img5 = R.drawable.empty, name = "7", address = "7"))
            add(SpotProfileData(img1 = R.drawable.odd, img2 = R.drawable.empty,img3 = R.drawable.empty,
                img4 = R.drawable.empty,img5 = R.drawable.empty, name = "8", address = "8"))
            */
            profileAdapter.datas = datas
            profileAdapter.notifyDataSetChanged()

        }
    }
}