package org.tensorflow.codelabs.objectdetection

import android.content.Context
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.HashMap

@Parcelize
data class SpotProfileData(var name: String, var address: String, var link: String,
                           val img: String):Parcelable {}

data class PoseProfileData(
    val obj: String,
    val img : String
)

object LabelData {
    var resnet: String = ""
    var yolo: MutableList<String> = mutableListOf()
}

object ArrayListData{
    var mArrayListPose = java.util.ArrayList<HashMap<String, String>>()
    var mArrayListSpot = java.util.ArrayList<HashMap<String, String>>()
}

