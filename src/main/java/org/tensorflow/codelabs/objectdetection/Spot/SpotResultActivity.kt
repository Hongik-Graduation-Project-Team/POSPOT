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
    private lateinit var img: String
    private lateinit var name: String
    private lateinit var address: String
    private lateinit var link: String
    private val datas = mutableListOf<SpotProfileData>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spot_result)
        overridePendingTransition(0, 0)

        initRecycler()

        btn_link.setOnClickListener(View.OnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            startActivity(intent)
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initRecycler() {
        profileAdapter = SpotProfileAdapter(this)
        spot_profile.adapter = profileAdapter

        datas.apply {

            for (i in 0 until mArrayListSpot.size){
                img = mArrayListSpot[i].get("address")!!
                name = mArrayListSpot[i].get("name")!!
                address = mArrayListSpot[i].get("realaddress")!!
                link = mArrayListSpot[i].get("link")!!
                add(SpotProfileData(img = img, name = name!!, link = link!!, address = address!!))
            }

            //add(SpotProfileData(img = "images/beach_spot.png", name = mArrayListSpot.size.toString(), link = maxResLabel, address = "bbbb"))
            profileAdapter.datas = datas
            profileAdapter.notifyDataSetChanged()
        }
    }
}