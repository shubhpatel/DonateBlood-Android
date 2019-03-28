package com.centennial.donateblood.activities


import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import com.centennial.donateblood.R
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.messaging.FirebaseMessaging

open class BaseActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(applicationContext)
    }

    @VisibleForTesting
    val progressDialog by lazy {
        ProgressDialog(this)
    }

    fun showProgressDialog() {
        progressDialog.setMessage(getString(R.string.loading))
        progressDialog.isIndeterminate = true
        progressDialog.show()
    }

    fun hideProgressDialog() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    public override fun onStop() {
        super.onStop()
        hideProgressDialog()
    }

    fun redirectTo(user: FirebaseUser?, dbRef: CollectionReference, activity: AppCompatActivity) {

        if (user != null) {
            Log.i(activity::class.java.simpleName, "Login User:" + user.displayName)
            dbRef.document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document.data != null) {
                        Log.d(activity::class.java.simpleName, "DocumentSnapshot data: " + document.data)
                        if(activity::class.java.simpleName != MainActivity::class.java.simpleName) {
                            startActivity(Intent(activity, MainActivity::class.java))
                            finish()
                        }

                    } else {
                        hideProgressDialog()
                        Log.d(activity::class.java.simpleName, "No such document")
                        if(activity::class.java.simpleName != RegistrationActivity::class.java.simpleName) {
                            startActivity(Intent(activity, RegistrationActivity::class.java))
                            finish()
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(activity::class.java.simpleName, "Data get failed with ", exception)
                    if(activity::class.java.simpleName != LoginActivity::class.java.simpleName) {
                        startActivity(Intent(activity, LoginActivity::class.java))
                        finish()
                    }
                }

        } else {
            Log.e(activity::class.java.simpleName, "User not logged in")
            if(activity::class.java.simpleName != LoginActivity::class.java.simpleName) {
                startActivity(Intent(activity, LoginActivity::class.java))
                finish()
            }

        }

    }

    fun subscribeFCM(channel: String){

        FirebaseMessaging.getInstance().subscribeToTopic(channel)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e(TAG, "Failed to Subscribe: $channel")
                }
                Log.d(TAG, "Subscribed: $channel")
            }

    }

    companion object {
        private val TAG = this::class.java.simpleName
    }
}