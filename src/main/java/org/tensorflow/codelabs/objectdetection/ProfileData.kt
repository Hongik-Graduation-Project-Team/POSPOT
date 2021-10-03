package org.tensorflow.codelabs.objectdetection

import android.content.Context
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SpotProfileData(var name: String, var address: String,
                           val img1: Int, val img2: Int, val img3: Int, val img4: Int, val img5: Int):Parcelable {}

data class PoseProfileData(
    val obj: String,
    val img : Int
)

