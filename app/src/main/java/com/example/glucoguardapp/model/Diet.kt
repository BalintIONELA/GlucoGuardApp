package com.example.glucoguardapp.model


import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.example.glucoguardapp.helper.FirebaseHelper

@Parcelize
data class Diet(
    var id: String = "",
    var meal: String = "",
    var food: String = ""
) : Parcelable {
    init {
        // Initialize the id property with a unique key from Firebase, or an empty string if the key is null
        this.id = FirebaseHelper.getDatabase().push().key ?: ""
    }
}
