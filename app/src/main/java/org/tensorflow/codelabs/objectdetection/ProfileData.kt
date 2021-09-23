package org.tensorflow.codelabs.objectdetection

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SpotProfileData(var name: String, var address: String, val img: Int):Parcelable {
}

data class PoseProfileData(
    val obj: String,
    val img : Int
)

