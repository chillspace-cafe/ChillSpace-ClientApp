package chillspace.chillspace.views


import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import chillspace.chillspace.R
import chillspace.chillspace.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        var user: User? = User("", "", "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //for toolbar
        setSupportActionBar(toolbar)

        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        val firebaseAuth = FirebaseAuth.getInstance()

        //changing fragments when firebase auth changed
        firebaseAuth.addAuthStateListener {
            if (firebaseAuth.currentUser == null) {
                when {
                    navController.currentDestination?.id == R.id.dest_home -> navController.navigate(R.id.action_dest_home_to_dest_signin)
                    navController.currentDestination?.id == R.id.dest_email_verification -> navController.navigate(R.id.action_dest_email_verification_to_dest_signin)
                    navController.currentDestination?.id == R.id.dest_profile -> navController.navigate(R.id.action_dest_profile_to_dest_signin)
                }
            } else {
                //checking if current user has his email verified
                if (firebaseAuth.currentUser?.isEmailVerified!!) {
                    navController.navigate(R.id.dest_home)

                    //getting current user data and saving as companion object
                    FirebaseDatabase.getInstance().reference.child("User").child(firebaseAuth.currentUser?.uid.toString())
                            .addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {
                                    Toast.makeText(this@MainActivity, "Cant find user data.", Toast.LENGTH_SHORT).show()
                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    user = p0.getValue(User::class.java)!!
                                }

                            })
                } else {
                    navController.navigate(R.id.dest_email_verification)
                }
            }
        }

        //setting title according to fragment
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            toolbar.title = navController.currentDestination?.label
        }
    }

    //assists navigation
    override fun onBackPressed() {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        if (navController.currentDestination?.id == R.id.dest_email_verification
                || navController.currentDestination?.id == R.id.dest_home
                || navController.currentDestination?.id == R.id.dest_signin) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

}