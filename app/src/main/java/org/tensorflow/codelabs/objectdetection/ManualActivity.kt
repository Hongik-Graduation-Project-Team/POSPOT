package org.tensorflow.codelabs.objectdetection

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.activity_camera.camera_profile
import kotlinx.android.synthetic.main.activity_spot_result.*

class ManualActivity : AppCompatActivity() {
    private lateinit var profileAdapter: PoseProfileAdapter
    private val datas = mutableListOf<PoseProfileData>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual)
        overridePendingTransition(0, 0)
    }
}