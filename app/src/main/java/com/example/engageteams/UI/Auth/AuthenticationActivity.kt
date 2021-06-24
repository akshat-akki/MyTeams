package com.example.engageteams.UI.Auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.engageteams.DAO.UserDao
import com.example.engageteams.Models.User
import com.example.engageteams.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import sdk.chat.core.session.ChatSDK
import sdk.chat.core.types.AccountDetails
import sdk.chat.firebase.adapter.FirebaseCoreHandler.database

class AuthenticationActivity : AppCompatActivity() {

   //Declaration of global variables
    private lateinit var auth: FirebaseAuth
    private val TAG="Sign In"
    private val RC_SIGN_IN: Int = 123
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var signInButton: Button
    private lateinit var progressBar:ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        auth= Firebase.auth
        signInButton = findViewById(R.id.google_sign_in_button)// initialising the google sign in Button
        progressBar=findViewById(R.id.signInProgressBar)// initialising the Progress bar

        //Calling signIn() function when the SignIn Button is pressed!!
        signInButton.setOnClickListener {
            signIn();
        }

    }

    //onStart function to check if the user is already signed in!!
    override fun onStart() {
        super.onStart()
        val currentUser=auth.currentUser
        updateUI(currentUser)
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)   //passing the sign in Intent to open the choose google Accounts window
    }

    //Method ActivityResult after sign In from choose google Accounts window
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            signInResult(task)
        }
    }

    //Method that gives the result of signing in of user in a task!!
    private fun signInResult(task: Task<GoogleSignInAccount>?) {
        try {
            // Google Sign In was successful, authenticate with Firebase
            val account = task?.getResult(ApiException::class.java)!!
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
            Log.w(TAG, "Google sign in failed", e)
        }
    }

    // [START auth_with_google]
    private fun firebaseAuthWithGoogle(idToken: String) {
        signInButton.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        //Creating a Kotlin CoroutineScope for halding the Background processes!!
        GlobalScope.launch(Dispatchers.IO) {
           val auth=auth.signInWithCredential(credential).await();
            val firebaseUser=auth.user
            withContext(Dispatchers.Main){
                val details:AccountDetails = AccountDetails.signUp(firebaseUser?.email.toString(), "")

                updateUI(firebaseUser)
            }
        }
    }// [END auth_with_google]

    //Updates the Activity UI for necessary changes
    private fun updateUI(firebaseUser: FirebaseUser?) {
        if(firebaseUser != null) {

            val user = User(
                firebaseUser.email.toString(),
                firebaseUser.uid,
                firebaseUser.displayName,
                firebaseUser.photoUrl.toString()
            )
            val usersDao = UserDao()
            usersDao.addUser(user)
            val details:AccountDetails = AccountDetails.username(user.email,"")
            ChatSDK.auth().authenticate(details).subscribe()
            ChatSDK.ui().startMainActivity(this)
//            val mainActivityIntent = Intent(this, MainActivity::class.java)// Calling the Main Activity
//            startActivity(mainActivityIntent)
//            finish()
        } else {
            signInButton.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
    }

}







