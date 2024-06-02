package com.example.glucoguardapp.model

import android.os.Parcelable
import com.example.glucoguardapp.helper.FirebaseHelper
import kotlinx.parcelize.Parcelize

@Parcelize
data class Medication(
    var id: String = "",
    var name: String = "",
    var dosage: String = ""
) : Parcelable {
    init {
        // Initialize the id property with a unique key from Firebase, or an empty string if the key is null
        this.id = FirebaseHelper.getDatabase().push().key ?: ""
    }
}
