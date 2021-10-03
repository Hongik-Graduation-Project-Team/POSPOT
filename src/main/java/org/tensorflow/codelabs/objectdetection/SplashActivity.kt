package org.tensorflow.codelabs.objectdetection

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_splash.*


class SplashActivity : AppCompatActivity() {
    lateinit var fadeIn: Animation
    lateinit var fadeOut: Animation
    lateinit var none: Animation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        fadeIn = AnimationUtils.loadAnimation(this,R.anim.fade_in)
        fadeOut = AnimationUtils.loadAnimation(this,R.anim.fade_out)
        none = AnimationUtils.loadAnimation(this,R.anim.none)

        textView.startAnimation(none)
        Handler().postDelayed({textView.startAnimation(fadeIn)},1000)
        Handler().postDelayed({textView.startAnimation(fadeOut)},2000)
        Handler().postDelayed({textView.startAnimation(none)},3000)
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            overridePendingTransition(0, 0)
        },3750) //3초뒤에 메인엑티비티로 넘어감
    }
}