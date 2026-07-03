package com.example.edugame_project

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var profileImage: ImageView
    private lateinit var editName: EditText
    private lateinit var editAge: EditText
    private lateinit var genderGroup: RadioGroup
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        profileImage = findViewById(R.id.profile_image)
        editName = findViewById(R.id.edit_name)
        editAge = findViewById(R.id.edit_age)
        genderGroup = findViewById(R.id.gender_group)
        val saveButton = findViewById<Button>(R.id.save_profile_button)
        val backButton = findViewById<ImageView>(R.id.back_button_profile)

        loadProfile()

        val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedImageUri = result.data?.data
                selectedImageUri?.let {
                    contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    profileImage.setImageURI(it)
                }
            }
        }

        profileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }

        saveButton.setOnClickListener {
            saveProfile()
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun saveProfile() {
        val prefs = getSharedPreferences("User_Profile", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("name", editName.text.toString())
        editor.putString("age", editAge.text.toString())
        
        val selectedGenderId = genderGroup.checkedRadioButtonId
        if (selectedGenderId != -1) {
            val radioButton = findViewById<RadioButton>(selectedGenderId)
            editor.putString("gender", radioButton.text.toString())
        }

        selectedImageUri?.let { editor.putString("image_uri", it.toString()) }
        editor.apply()
        Toast.makeText(this, "Profile Saved!", Toast.LENGTH_SHORT).show()
    }

    private fun loadProfile() {
        val prefs = getSharedPreferences("User_Profile", Context.MODE_PRIVATE)
        editName.setText(prefs.getString("name", ""))
        editAge.setText(prefs.getString("age", ""))
        
        val gender = prefs.getString("gender", "")
        if (gender == "Boy") findViewById<RadioButton>(R.id.radio_male).isChecked = true
        else if (gender == "Girl") findViewById<RadioButton>(R.id.radio_female).isChecked = true

        val uriString = prefs.getString("image_uri", null)
        if (uriString != null) {
            selectedImageUri = Uri.parse(uriString)
            profileImage.setImageURI(selectedImageUri)
        }
    }
}