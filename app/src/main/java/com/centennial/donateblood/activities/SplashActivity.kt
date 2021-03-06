package com.centennial.donateblood.activities

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.animation.AnimationUtils
import com.centennial.donateblood.R
import com.centennial.donateblood.utils.Constants
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_splash.*


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class SplashActivity : BaseActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.centennial.donateblood.R.layout.activity_splash)

        ivSplash.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pulse))

        auth = FirebaseAuth.getInstance()
        firestore= FirebaseFirestore.getInstance()
        userDBRef = firestore.collection(Constants.USER_DATA_REF)

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }
                val token = task.result!!.token
                Log.d(TAG, "Token for FCM: $token")

            })


        subscribeFCM(Constants.FCM_DEFAULT)


        Handler().postDelayed({
            redirectTo(auth.currentUser, userDBRef, this)
        }, SPLASH_TIME_OUT)
    }



    companion object {
        private val TAG = this::class.java.simpleName
        private const val SPLASH_TIME_OUT: Long = 3000
    }

}
