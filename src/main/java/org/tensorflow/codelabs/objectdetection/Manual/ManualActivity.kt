package org.tensorflow.codelabs.objectdetection

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.activity_camera.camera_profile
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.text
import kotlinx.android.synthetic.main.activity_spot.*
import kotlinx.android.synthetic.main.activity_spot_result.*

class ManualActivity : AppCompatActivity() {
    private lateinit var horizon1: Animation
    private lateinit var horizon2: Animation
    private lateinit var horizon3: Animation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual)
        overridePendingTransition(0, 0)

        horizon1 = AnimationUtils.loadAnimation(this,R.anim.horizon_enter1)
        horizon2 = AnimationUtils.loadAnimation(this,R.anim.horizon_enter2)
        horizon3 = AnimationUtils.loadAnimation(this,R.anim.horizon_enter3)

        text.startAnimation(horizon1)
        btn_pose.startAnimation(horizon2)
        btn_spot.startAnimation(horizon3)

        btn_pose.setOnClickListener(View.OnClickListener {
            val poseIntent = Intent(this, PoseManualActivity::class.java)
            startActivity(poseIntent)
            overridePendingTransition(0, 0)
        })
        // SpotActivity
        btn_spot.setOnClickListener(View.OnClickListener {
            val spotIntent = Intent(this, SpotManualActivity::class.java)
            startActivity(spotIntent)
            overridePendingTransition(0, 0)
        })
    }
}