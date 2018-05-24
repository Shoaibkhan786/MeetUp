package com.example.muhammadkhan.appointmentproject.activities;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.example.muhammadkhan.appointmentproject.Notification.NotificationFragment;
import com.example.muhammadkhan.appointmentproject.DataModel.Users;
import com.example.muhammadkhan.appointmentproject.R;
import com.example.muhammadkhan.appointmentproject.PendingAppointments.BookedFragment;
import com.example.muhammadkhan.appointmentproject.userShowFragment.AdapterUsers;
import com.example.muhammadkhan.appointmentproject.userShowFragment.UserShowFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private String TAG = "TAG";
    private Toolbar topToolBar;
    AHBottomNavigation bottomNavigation;
    FragmentTransaction ft;
    AdapterUsers adapterUsers;
    List<Users> data;
    UserShowFragment recyclerViewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        topToolBar = (Toolbar) findViewById(R.id.top_toolbar);
        setSupportActionBar(topToolBar);
        topToolBar.setTitleTextColor(Color.parseColor("#f9fcf9"));
        //method callqqqqq
        createBottomNavigation();
        ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frame, new BookedFragment());
        ft.commit();
        //method call
        new SaveUsers().execute();
    }
//-----------------------------------Method for creating bottom navigation bar-----------------------------------//
    public void createBottomNavigation() {
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Booked", R.drawable.ic_home_black_24dp);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Notification", R.drawable.ic_notifications_black_24dp);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("People", R.drawable.ic_supervisor_account_black_24dp);
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        // Set background color
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));

        bottomNavigation.setBehaviorTranslationEnabled(false);
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                Log.i("Positon is", "" + position);
                if (position == 2) {
                    ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.frame, new UserShowFragment());
                    ft.commit();
                }
                if (position == 0) {
                    //this is not working
                    ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.frame, new BookedFragment());
                    ft.commit();
                }
                if (position == 1) {
                    ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.frame, new NotificationFragment());
                    ft.commit();
                }
                return true;
            }
        });
    }

    //-------------------call back method inflating the search bar-------------------------------------//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top, menu);
        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        //method call for search implementation on recyclerview
        UserShowFragment.search(searchView);
        return true;
    }

    //-------------------------if menu items are clicked---------------------------------------------//
    public void logOut(MenuItem item) {
        if (item.getTitle().equals("signOut")) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, Login_in.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
            startActivity(intent);
            finish();
        }
    }

    public class SaveUsers extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                OkHttpClient client = new OkHttpClient();
                RequestBody body = new FormBody.Builder()
                        .add("userContact",FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                        .add("token",FirebaseInstanceId.getInstance().getToken())
                        .build();
                Request request = new okhttp3.Request.Builder()
                        .url("http://192.168.1.195/register.php")
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                Log.i(TAG, "doInBackground: "+response.message());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}