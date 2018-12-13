package chillspace.chillspace


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

        lateinit var firebaseAuth: FirebaseAuth

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
            //for toolbar
            setSupportActionBar(toolbar)

            val navController = Navigation.findNavController(this, R.id.nav_host_fragment)

            //changing fragments when firebase auth changed
            firebaseAuth = FirebaseAuth.getInstance()
            firebaseAuth.addAuthStateListener {
                if (firebaseAuth.currentUser == null) {
                    navController.navigate(R.id.action_dest_home_to_dest_signin)
                }
                else{
                    navController.navigate(R.id.dest_home)
                }
            }

            //setting title according to fragment
            navController.addOnDestinationChangedListener { controller, destination, arguments ->
                toolbar.title = navController.currentDestination?.label
            }
        }
}

