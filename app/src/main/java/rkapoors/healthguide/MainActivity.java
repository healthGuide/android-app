package rkapoors.healthguide;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements OnTabChangeListener, OnPageChangeListener {

    MyPageAdapter pageAdapter;
    private ViewPager mViewPager;
    private TabHost mTabHost;

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    boolean doubleBackToExitPressedOnce = false;
    private TextView maildesc;
    TextView naam;

    android.support.v7.widget.Toolbar myToolbar;

    private DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;

    FirebaseDatabase database;
    DatabaseReference dbref;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.mytoolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitleTextColor(android.graphics.Color.WHITE);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);

        database=FirebaseDatabase.getInstance();
        dbref=database.getReference();

        final ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,myToolbar,R.string.drawer_open,R.string.drawer_close){
                public void onDrawerClosed(View view){
                    supportInvalidateOptionsMenu();
                }
                public void onDrawerOpened(View drawerView){
                    supportInvalidateOptionsMenu();
                }
            };
            actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
            drawerLayout.setDrawerListener(actionBarDrawerToggle);
            actionBarDrawerToggle.syncState();
        }

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(!isConnected)
        {
            Snackbar snackbar=Snackbar.make(drawerLayout, "Check Internet Connection", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }

        auth = FirebaseAuth.getInstance();

        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, signup.class));
                    finish();
                }
            }
        };

        String mailaddr="",useruid="";
        if(user!=null) {mailaddr=user.getEmail();useruid=user.getUid();}
        maildesc = (TextView)findViewById(R.id.useremail);
        maildesc.setText(mailaddr);

        naam = (TextView)findViewById(R.id.userName);
        dbref.child("users").child(useruid).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.getValue(String.class);
                naam.setText(username);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        // Tab Initialization
        initialiseTabHost();

        // Fragments and ViewPager Initialization
        List<Fragment> fragments = getFragments();
        pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(pageAdapter);
        mViewPager.setOnPageChangeListener(MainActivity.this);

        String[] data={"New record","","","Change password","Log out"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);

        final ListView navList = (ListView) findViewById(R.id.navList);
        navList.setAdapter(adapter);
        navList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int pos,long id){

                switch(pos){
                    case 0:
                        Intent nra=new Intent(MainActivity.this,newrecord.class);
                        startActivity(nra);
                        break;
                    case 3:
                        Intent changepass=new Intent(MainActivity.this,changepassword.class);
                        startActivity(changepass);
                        break;
                    case 4:
                        signOut();
                        break;
                }
            }
        });
    }
    // Method to add a TabHost
    private static void AddTab(MainActivity activity, TabHost tabHost, TabHost.TabSpec tabSpec) {
        tabSpec.setContent(new MyTabFactory(activity));
        tabHost.addTab(tabSpec);
    }

    // Manages the Tab changes, synchronizing it with Pages
    public void onTabChanged(String tag) {
        int pos = this.mTabHost.getCurrentTab();
        this.mViewPager.setCurrentItem(pos);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    // Manages the Page changes, synchronizing it with Tabs
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        int pos = this.mViewPager.getCurrentItem();
        this.mTabHost.setCurrentTab(pos);

        View tabView = mTabHost.getTabWidget().getChildAt(position);
        View mHorizontalScroll = (HorizontalScrollView) findViewById(R.id.mHorizontalScroll);
        if (tabView != null)
        {
            int width = mHorizontalScroll.getWidth();
            int scrollPos = tabView.getLeft() - (width - tabView.getWidth()) / 2;
            mHorizontalScroll.scrollTo(scrollPos, 0);
        } else {
            mHorizontalScroll.scrollBy(positionOffsetPixels, 0);
        }
    }
    @Override
    public void onPageSelected(int arg0) {
        mTabHost.setCurrentTab(arg0);
    }

    private List<Fragment> getFragments(){
        List<Fragment> fList = new ArrayList<Fragment>();

        // TODO Put here your Fragments
        MySampleFragment f1 = new MySampleFragment();
        MySampleFragment1 f2 = new MySampleFragment1();
        MySampleFragment2 f3 = new MySampleFragment2();
        MySampleFragment3 f4 = new MySampleFragment3();

        fList.add(f1);
        fList.add(f2);
        fList.add(f3);
        fList.add(f4);

        return fList;
    }
    // Tabs Creation
    private void initialiseTabHost() {
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();

        // TODO Put here your Tabs
        MainActivity.AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab1").setIndicator("Schedule"));
        MainActivity.AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab2").setIndicator("Notifications"));
        MainActivity.AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab3").setIndicator("Records"));
        MainActivity.AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab4").setIndicator("Emergency"));

        //Text color of tabs
        int kk=mTabHost.getTabWidget().getChildCount();
        for (int i = 0; i<kk; i++) {
            TextView tv = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(Color.WHITE);
        }

        mTabHost.setOnTabChangedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;

        Snackbar snackbar=Snackbar.make(drawerLayout, "Tap back again to exit.", Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    //sign out method
    public void signOut() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Signing out");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.show();
        pd.setCancelable(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(1000);
                }catch(Exception e){
                    e.printStackTrace();
                }
                auth.signOut();
                pd.dismiss();
            }
        }).start();
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }
}