package com.example.expensestracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


  private  DrawerLayout drawerLayout;
  private  NavigationView navigationView;
  private  Toolbar toolbar;

   private BottomNavigationView bottomnavigation;
   private FrameLayout framelayout;

   private dashboardFragment dashboardframe;

   private incomeFragment incomeframe;

   private expenseFragment expenseframe;


   //back button

    private static final int BACK_BUTTON_INTERVAL = 2000; // Define the time interval in milliseconds
    private long backButtonPressedTime = 0;







    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.naview);
        toolbar = findViewById(R.id.mytoolbar);
        bottomnavigation = findViewById(R.id.bottombar);
        framelayout = findViewById(R.id.frame_layout);

        mAuth = FirebaseAuth.getInstance();


        //step 1

        toolbar.setTitle("Expenses tracker");

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        toogle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        dashboardframe = new dashboardFragment();
        incomeframe = new incomeFragment();
        expenseframe = new expenseFragment();



        //defalut{
        setFragment(dashboardframe);
        //}

        bottomnavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.dashboard) {
                    setFragment(dashboardframe);
                } else if (itemId == R.id.income) {
                    setFragment(incomeframe);
                } else if (itemId == R.id.expense) {
                    setFragment(expenseframe);
                }
                return false;
            }

        });

    }


    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mAuth.signOut();
                Intent loginActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(loginActivity);
                Toast.makeText(HomeActivity.this, " You have been logged out ", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            // Handle logout here
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }








    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            // Check if the back button is pressed within the specified interval
            if (backButtonPressedTime + BACK_BUTTON_INTERVAL > System.currentTimeMillis()) {
                // If pressed twice within the interval, exit the app
                finishAffinity();
            } else {
                Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
                backButtonPressedTime = System.currentTimeMillis();
            }
        }
    }




    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        displaySelectedListener(item.getItemId());
        return true;
    }


     public void displaySelectedListener(int itemId){


        Fragment fragment = null;

         if (itemId == R.id.dashboard) {
             fragment = new dashboardFragment();

         } else if (itemId == R.id.income) {
             fragment = new incomeFragment();

         } else if (itemId == R.id.expense) {
             fragment = new expenseFragment();
         }

         if (fragment != null) {

             FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
             ft.replace(R.id.frame_layout,fragment);
             ft.commit();
         }

         drawerLayout.closeDrawer(GravityCompat.START);

     }


}