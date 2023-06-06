package com.ashiq.userprofile.ui.activity

import android.app.Dialog
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import com.ashiq.userprofile.R
import com.ashiq.userprofile.databinding.ActivityMainBinding
import com.ashiq.userprofile.modelclass.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private var auth: FirebaseAuth? = null
    private var databaseReference: DatabaseReference? = null
    private var storageReference: StorageReference? = null
    private var profileImageUri: Uri? = null
    private var dialog: Dialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        auth = FirebaseAuth.getInstance()
        val uid = auth?.currentUser?.uid
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        binding?.btnSave?.setOnClickListener {

            showProgressBar()
            val firstName = binding?.etFirstName?.text.toString()
            val secondName = binding?.etSecondName?.text.toString()
            val bio = binding?.etBio?.text.toString()

            val userID=System.currentTimeMillis().toString()

            val user = User(userID,firstName, secondName, bio)

        /*    if (uid != null) {*/
                databaseReference?.child(userID)?.setValue(user)?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        uploadProfilePic(userID)
                        binding?.etFirstName?.setText("")
                        binding?.etSecondName?.setText("")
                        binding?.etBio?.setText("")
                    } else {
                        hideProgressBar()
                        Toast.makeText(this, "Failed to update profile", Toast.LENGTH_LONG).show()
                    }
                }?.addOnFailureListener {
                    println("failed-->$it")
                }
           /* }else{
                hideProgressBar()
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_LONG).show()
            }*/

        }
    }

    private fun uploadProfilePic(userID: String) {
        profileImageUri = Uri.parse("android.resource://$packageName/${R.drawable.ic_profile}")
        storageReference =
            FirebaseStorage.getInstance().getReference("Users/$userID.jpeg")
        storageReference?.putFile(profileImageUri!!)?.addOnCompleteListener {
            if (it.isSuccessful) {
                hideProgressBar()
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_LONG).show()
            } else {
                hideProgressBar()
                Toast.makeText(this, "Failed to load profile", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showProgressBar() {
        dialog = Dialog(this)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.dialog_wait)
        dialog?.setCancelable(false)
        dialog?.show()
    }

    private fun hideProgressBar() {
        dialog?.dismiss()
    }
}