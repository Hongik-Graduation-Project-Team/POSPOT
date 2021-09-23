package org.tensorflow.codelabs.objectdetection

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        val yolo2: String

        if(yolo.size == 1){
            yolo2 = yolo[0]
        }
        else{
            yolo2 = yolo[1]
        }

        datas.apply {
            add(SpotProfileData(img = R.drawable.back, name = "resnet 라벨값", address = res))

            profileAdapter.datas = datas
            profileAdapter.notifyDataSetChanged()
        }
        datas.apply {
            add(SpotProfileData(img = R.drawable.odd, name = "resnet 라벨값", address = res))
            add(SpotProfileData(img = R.drawable.odd, name = "yolo 라벨값", address = yolo2))
            add(SpotProfileData(img = R.drawable.odd, name = "3", address = "3"))
            add(SpotProfileData(img = R.drawable.odd, name = "4", address = "4"))
            add(SpotProfileData(img = R.drawable.odd, name = "5", address = "5"))
            add(SpotProfileData(img = R.drawable.odd, name = "6", address = "6"))
            add(SpotProfileData(img = R.drawable.odd, name = "7", address = "7"))
            add(SpotProfileData(img = R.drawable.odd, name = "8", address = "8"))

            profileAdapter.datas = datas
            profileAdapter.notifyDataSetChanged()

        }
    }
}