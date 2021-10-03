package org.tensorflow.codelabs.objectdetection

import android.content.Context
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SpotProfileData(var name: String, var address: String, var explain: String,
                           val img: String, ):Parcelable {}

data class PoseProfileData(
    val obj: String,
    val img : String
)

