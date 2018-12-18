package chillspace.chillspace


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

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
                when{
                    navController.currentDestination?.id == R.id.dest_home -> navController.navigate(R.id.action_dest_home_to_dest_signin)
                    navController.currentDestination?.id == R.id.dest_email_verification -> navController.navigate(R.id.action_dest_email_verification_to_dest_signin)
                }
            } else {
                //checking if current user has his email verified
                if (firebaseAuth.currentUser?.isEmailVerified!!) {
                    navController.navigate(R.id.dest_home)
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

}

