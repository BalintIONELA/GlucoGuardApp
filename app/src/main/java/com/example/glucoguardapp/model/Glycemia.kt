package com.example.glucoguardapp.model

import android.os.Parcelable
import com.example.glucoguardapp.helper.FirebaseHelper
import kotlinx.parcelize.Parcelize

@Parcelize
data class Glycemia(
    var id: String = "",
    var glucoseLevel: String = "",
    var description: String = "",
    var day: Int = 0,
    var month: Int = 0,
    var year: Int = 0
) : Parcelable {
    init {
        // Initialize the id property with a unique key from Firebase, or an empty string if the key is null
        this.id = FirebaseHelper.getDatabase().push().key ?: ""
    }
}
