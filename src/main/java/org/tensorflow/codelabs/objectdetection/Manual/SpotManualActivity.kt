package org.tensorflow.codelabs.objectdetection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_spot_manual.*

class SpotManualActivity : AppCompatActivity() {
    private lateinit var horizon1: Animation
    private lateinit var horizon2: Animation
    private lateinit var horizon3: Animation
    private lateinit var horizon4: Animation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spot_manual)
        overridePendingTransition(0, 0)

        horizon1 = AnimationUtils.loadAnimation(this,R.anim.horizon_enter1)
        horizon2 = AnimationUtils.loadAnimation(this,R.anim.horizon_enter2)
        horizon3 = AnimationUtils.loadAnimation(this,R.anim.horizon_enter3)
        horizon4 = AnimationUtils.loadAnimation(this,R.anim.horizon_enter4)

        text.startAnimation(horizon1)
        text2.startAnimation(horizon1)
        imageView1.startAnimation(horizon2)
        imageView2.startAnimation(horizon2)
        imageView3.startAnimation(horizon2)
        text_ex1.startAnimation(horizon2)
        text_ex2.startAnimation(horizon2)
        text_ex3.startAnimation(horizon2)
        text3.startAnimation(horizon3)
        imageView4.startAnimation(horizon3)
        imageView5.startAnimation(horizon3)
        text_ex4.startAnimation(horizon3)
        text4.startAnimation(horizon4)
        text_ex5.startAnimation(horizon4)
    }
}