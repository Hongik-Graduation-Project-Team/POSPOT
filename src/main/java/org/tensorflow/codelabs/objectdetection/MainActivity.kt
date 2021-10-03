/**
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tensorflow.codelabs.objectdetection

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.tensorflow.codelabs.objectdetection.ml.Resnet
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max
import kotlin.math.min

class MainActivity : AppCompatActivity(){
    companion object {
        const val TAG = "TFLite - ODT"
        const val REQUEST_IMAGE_CAPTURE: Int = 1
        const val REQ_PERMISSION_CAMERA: Int = 1001
    }
    private lateinit var currentPhotoPath: String
    private lateinit var maxResLabel: String
    private lateinit var maxYoloLabel: ArrayList<String>
    private lateinit var horizon1: Animation
    private lateinit var horizon2: Animation
    private lateinit var horizon3: Animation
    private lateinit var horizon4: Animation
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        overridePendingTransition(0, 0)

        maxYoloLabel = arrayListOf("zero")

        horizon1 = AnimationUtils.loadAnimation(this,R.anim.horizon_enter1)
        horizon2 = AnimationUtils.loadAnimation(this,R.anim.horizon_enter2)
        horizon3 = AnimationUtils.loadAnimation(this,R.anim.horizon_enter3)
        horizon4 = AnimationUtils.loadAnimation(this,R.anim.horizon_enter4)

        text.startAnimation(horizon1)
        btn_pose.startAnimation(horizon2)
        btn_spot.startAnimation(horizon3)
        btn_manual.startAnimation(horizon4)

        // 카메라
        btn_pose.setOnClickListener(View.OnClickListener {
            try {
                if(cameraPermissionGranted()) {
                    dispatchTakePictureIntent()
                }
            } catch (e: ActivityNotFoundException) {
                Log.e(TAG, e.message.toString())
            }
        })
        // SpotActivity
        btn_spot.setOnClickListener(View.OnClickListener {
            val spotIntent = Intent(this, SpotActivity::class.java)
            startActivity(spotIntent)
            overridePendingTransition(0, 0)
        })
        // manualActivity (How to use)
        btn_manual.setOnClickListener(View.OnClickListener{
            val manualIntent = Intent(this, ManualActivity::class.java)
            startActivity(manualIntent)
            overridePendingTransition(0, 0)
        })

    }
    // 사진찍고 확인완료 시 딥러닝 모델 실행
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //카메라
        if (resultCode == Activity.RESULT_OK &&
            requestCode == REQUEST_IMAGE_CAPTURE) {
            val cameraIntent = Intent(this, CameraActivity::class.java)
            showLoadingDialogAndMakeLabel(getCapturedImage(),cameraIntent)
        }
    }
    //다이얼로그 생성, 딥러닝 실행, 라벨 전송
    private fun showLoadingDialogAndMakeLabel(bitmap: Bitmap,resultIntent: Intent) {
        val dialog = LoadingDialog(this@MainActivity)
        CoroutineScope(Dispatchers.Main).launch{
            dialog.show()
            delay(100)
            setViewAndDetectYolo(bitmap)
            setViewAndDetectResnet(bitmap)
            delay(1)
            dialog.dismiss()
            resultIntent.putExtra("res", maxResLabel)
            resultIntent.putExtra("yolo", maxYoloLabel)
            startActivity(resultIntent)
            overridePendingTransition(0, 0)
            maxYoloLabel.clear()
            maxYoloLabel.add("zero")
        }
    }
    //-------------------------------------------------------------------------------------
    //                                         딥러닝
    //-------------------------------------------------------------------------------------
    // Yolo 실행
    private fun setViewAndDetectYolo(bitmap: Bitmap) {
        val image = TensorImage.fromBitmap(bitmap)
        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setMaxResults(5)
            .setScoreThreshold(0.5f)
            .build()

        val detector = ObjectDetector.createFromFileAndOptions(
            this,
            "model.tflite", // Yolo 모델
            options
        )

        // 모델 실행 후 결과를 얻음
        val results = detector.detect(image)
        // 결과도출
        val resultToDisplay = results.map {
            // 최상위(first)라벨값을 가져오고 확률표시
            val category = it.categories.first()
            val text = category.label
            // boundingBox를 생성
            DetectionResult(it.boundingBox, text)
        }
        resultToDisplay.forEach {
            maxYoloLabel.add(it.text)
        }
    }
    // resnet 실행
    private fun setViewAndDetectResnet(bitmap: Bitmap){
        val model = Resnet.newInstance(this)
        // input 생성
        val image = TensorImage.fromBitmap(bitmap)
        // 모델 실행 후 결과를 얻음
        val outputs = model.process(image)
        val probability = outputs.probabilityAsCategoryList
        // 가장 높은 score와 그 label
        maxResLabel = probability.maxByOrNull { it!!.score }?.label!!
        // 모델 종료
        model.close()
    }
    //-------------------------------------------------------------------------------------
    //                                         카메라
    //-------------------------------------------------------------------------------------
    // capture 버튼 누를 시 카메라를 실행함
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // 카메라 실행 중 행동(촬영) 확인
            takePictureIntent.resolveActivity(packageManager)?.also {
                // 사진 파일 생성
                val photoFile: File? = try {
                    createImageFile()
                } catch (e: IOException) {
                    Log.e(TAG, e.message.toString())
                    null
                }
                //파일 생성 완료 후
                photoFile?.also {
                    // path에 있는 파일 공유
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "org.tensorflow.codelabs.objectdetection.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    //onActivityResult로 이동(카메라를 마치고 Main화면으로 돌아옴)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }
    // 이미지 파일 내부저장소에 저장 -> 나중에 저장 안해도 가능하게 구현
    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // 이미지파일이름 생성
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // 파일저장 : ACTION_VIEW intents 에서 사용 할 경로
            currentPhotoPath = absolutePath
        }
    }
    // 정확한 이미지를 위한 고화질 사진 캡처
    private fun getCapturedImage(): Bitmap {
        // 디바이스 크기
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val targetW: Int = size.x
        val targetH: Int = size.y

        val bmOptions = BitmapFactory.Options().apply {
            // 비트맵의 치수 가져옴
            inJustDecodeBounds = true

            BitmapFactory.decodeFile(currentPhotoPath, this)

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // 이미지 축소 크기 결정
            val scaleFactor: Int = max(1, min(photoW / targetW, photoH / targetH))

            // 이미지뷰를 채우도롱 이미지를 비트맵크기로 디코딩
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inMutable = true
        }
        val exifInterface = ExifInterface(currentPhotoPath)
        val orientation = exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )

        bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                rotateImage(bitmap, 90f)
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                rotateImage(bitmap, 180f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                rotateImage(bitmap, 270f)
            }
            else -> {
                bitmap
            }
        }
    }
    // 사진이 돌아가 있을 시 알맞게 회전
    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }
    //-------------------------------------------------------------------------------------
    //                                         권한
    //-------------------------------------------------------------------------------------
    // 권한 확인 결과
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_PERMISSION_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
            } else {
                Toast.makeText(this,"권한이 없어 해당 기능을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // 권한 허용
    private fun cameraPermissionGranted(): Boolean {
        val preference = getPreferences(Context.MODE_PRIVATE)
        val isFirstCheck = preference.getBoolean("isFirstPermissionCheck", true)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                // 거부할 경우 왜 필요한지 설명
                val snackBar = Snackbar.make(main_layout, "권한이 필요합니다", Snackbar.LENGTH_INDEFINITE)
                snackBar.setAction("권한승인") {
                    ActivityCompat.requestPermissions(this,
                        arrayOf(
                            Manifest.permission.CAMERA
                        ), REQ_PERMISSION_CAMERA)
                }
                snackBar.show()
            } else {
                if (isFirstCheck) {
                    // 처음 물었는지 여부를 저장
                    preference.edit().putBoolean("isFirstPermissionCheck", false).apply()
                    // 권한요청
                    ActivityCompat.requestPermissions(this,
                        arrayOf(
                            Manifest.permission.CAMERA
                        ), REQ_PERMISSION_CAMERA)
                } else {
                    // 사용자가 권한을 거부하면서 다시 묻지않음 옵션을 선택한 경우
                    // requestPermission을 요청해도 창이 나타나지 않기 때문에 설정창으로 이동
                    val snackBar = Snackbar.make(main_layout, "위치권한이 필요합니다 확인을 누르시면 이동합니다", Snackbar.LENGTH_INDEFINITE)
                    snackBar.setAction("확인") {
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                    snackBar.show()
                }
            }
            return false
        } else {
            return true
        }
    }
}

// 시각화 정보를 저장하는 class
data class DetectionResult(val boundingBox: RectF, val text: String)
