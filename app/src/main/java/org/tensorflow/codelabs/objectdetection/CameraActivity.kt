package org.tensorflow.codelabs.objectdetection

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.activity_spot_result.*
import org.tensorflow.codelabs.objectdetection.databinding.ActivityCameraBinding
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class CameraActivity : AppCompatActivity() {
    private lateinit var profileAdapter: PoseProfileAdapter
    private val datas = mutableListOf<PoseProfileData>()
    private lateinit var binding: ActivityCameraBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var yolo: ArrayList<String>
    private lateinit var res: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        res = intent.getStringExtra("res")!!
        yolo = intent.getStringArrayListExtra("yolo")!!

        // 카메라 시작
        startCamera()

        initRecycler()

        //displayRatio()

        profileAdapter.setOnItemClickListener(object : PoseProfileAdapter.OnItemClickListener{
            override fun onItemClick(v: View, data: PoseProfileData, pos: Int) {
                background.setImageResource(R.drawable.odd2)
            }
        })
    }

    private fun displayRatio() {
        val display = windowManager.defaultDisplay // in case of Activity
        val size = Point()
        display.getRealSize(size) // or getSize(size)
        val width = size.x

        viewFinder.layoutParams.height=width
    }

    // 저장소
    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let { mFile->
            File(mFile, resources.getString(R.string.app_name)).apply{
                mkdir()
            }
        }
        return if(mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    fun Context.mediaScan(uri: Uri) {
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        intent.data = uri
        this.sendBroadcast(intent)
    }
    // 촬영버튼클릭
    private fun takePhoto(){

        val imageCapture = imageCapture ?: return

        // 현재시간을 string으로 저장
        var time = SimpleDateFormat(Constants.FILE_NAME_FORMAT,
            Locale.getDefault())
            .format(System
                .currentTimeMillis())

        // 저장된 string과 저장위치가 담긴 파일
        val photoFile = File(
            outputDirectory,
            "$time.jpg"
        )

        // 파일을 옵션화
        val outputOption = ImageCapture
            .OutputFileOptions
            .Builder(photoFile)
            .build()

        imageCapture.takePicture(
            outputOption, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback{
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // 저장에 성공하면 저장위치의 uri 출력
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo Saved"
                    mediaScan(savedUri)
                    Toast.makeText(
                        this@CameraActivity,
                        "$msg $savedUri",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(Constants.TAG,
                        "onError: ${exception.message}",
                        exception)
                }
            }
        )
    }
    // 카메라
    private fun startCamera(){
        outputDirectory = getOutputDirectory()
        val cameraProviderFuture = ProcessCameraProvider
            .getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also { mPreview->

                    mPreview.setSurfaceProvider(
                        // activity_camera에 있는 viewfinder
                        binding.viewFinder.surfaceProvider
                    )
                }

            imageCapture = ImageCapture.Builder()
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try{
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,cameraSelector,
                    preview,imageCapture
                )

            }catch (e: Exception){
                Log.d(Constants.TAG,"startCamera Fail", e)
            }
        }, ContextCompat.getMainExecutor(this))
        val mediaPlayer = MediaPlayer.create(this,R.raw.shutter)
        // 촬영버튼클릭
        binding.btnTakePhoto.setOnClickListener{
            //갤러리에 파일폴더 생성 후 저장
            mediaPlayer.start()
            takePhoto()
        }
    }
    //recycle view 항목 추가
    @SuppressLint("NotifyDataSetChanged")
    private fun initRecycler() {
        profileAdapter = PoseProfileAdapter(this)
        camera_profile.adapter = profileAdapter

        datas.apply {
            add(PoseProfileData(img = R.drawable.odd2, obj = "zzafa"))
            add(PoseProfileData(img = R.drawable.odd2, obj = "zzaf"))
            add(PoseProfileData(img = R.drawable.odd2, obj = "zz"))
            add(PoseProfileData(img = R.drawable.odd2, obj = "zz"))
            add(PoseProfileData(img = R.drawable.odd2, obj = "zz"))
            add(PoseProfileData(img = R.drawable.odd2, obj = "zz"))
            add(PoseProfileData(img = R.drawable.odd2, obj = "zz"))
            add(PoseProfileData(img = R.drawable.odd2, obj = "zz"))
            add(PoseProfileData(img = R.drawable.odd2, obj = "zz"))

            profileAdapter.datas = datas
            profileAdapter.notifyDataSetChanged()
        }
    }
}