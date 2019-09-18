package android.gr.katastima;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.api.services.people.v1.PeopleScopes;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    public static Context context;
    public static Activity activity;

    public static GoogleSignInAccount account;
    public static GoogleApiClient mGoogleApiClient;

    public static int CUR_POINTS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        activity = this;

        //Permission to read external storage
        if(ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    20);
        }
        else {
            ServerCommunication.init();
        }

        Functions.LOCATION_FINDER = new LocationFinder(context, this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(getString(R.string.server_client_id))
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .requestScopes(new Scope(Scopes.PROFILE))
                .requestScopes(new Scope(PeopleScopes.USER_BIRTHDAY_READ))
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 20) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ServerCommunication.init();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

        // Check if already signed in
        account = GoogleSignIn.getLastSignedInAccount(context);

        if(account != null) {
            ServerCommunication.PointGetter task = new ServerCommunication.PointGetter(false);
            task.execute(account.getEmail(), getString(R.string.appId), getString(R.string.userId));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        GoogleApiAvailability gaa = GoogleApiAvailability.getInstance();
        Dialog dialog = gaa.getErrorDialog(this, connectionResult.getErrorCode(), 101);
        dialog.show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {}

    @Override
    public void onConnectionSuspended(int i) {}

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        public Fragment setItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Home";
                case 1:
                    return "Catalog";
                case 2:
                    return "Offers";
                case 3:
                    return "Map";
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = null;
            int section = getArguments().getInt(ARG_SECTION_NUMBER);

            if (section == 1) { // Home
                try {
                    rootView = inflater.inflate(R.layout.fragment_main, container, false);
                    Resources res = context.getResources();
                    ImageView img = (ImageView) rootView.findViewById(R.id.shopImage);
                    DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
                    int reqW = dm.widthPixels;
                    int reqH = Functions.convertDpToPx(200);
                    img.setImageBitmap(Functions.getScaledBitmap(res,
                            res.getIdentifier(context.getString(R.string.shop_image), "drawable", context.getPackageName()),
                            reqW, reqH));
                }
                catch(InflateException e) {}
            } else if (section == 2) { // Τιμοκατάλογος
                rootView = inflater.inflate(R.layout.fragment_timokatalogos, container, false);
                ListView listView = (ListView) rootView.findViewById(R.id.list_of_products);
                ProductListAdapter listAdapter = new ProductListAdapter(getActivity(),
                        Functions.calcProductlist(getActivity()));
                listView.setAdapter(listAdapter);
            } else if (section == 3) { // Προσφορές
                    if(MainActivity.account != null) { // If user has logged in
                        rootView = inflater.inflate(R.layout.fragment_gifts, container, false);
                        final TextView textView = (TextView) rootView.findViewById(R.id.pointsTxt);
                        ListView listView = (ListView) rootView.findViewById(R.id.list_of_gifts);

                        final ArrayList<Gift> gifts = Functions.calcGiftlist(getActivity());

                        GiftListAdapter listAdapter = new GiftListAdapter(getActivity(),
                                gifts);
                        listView.setAdapter(listAdapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if(MainActivity.CUR_POINTS >= Integer.parseInt(gifts.get(position).points)) {
                                    ServerCommunication.CodeCreation task = new ServerCommunication.CodeCreation();
                                    task.execute(
                                            account.getEmail(),
                                            getString(R.string.appId),
                                            getString(R.string.userId),
                                            gifts.get(position).points);
                                }
                                else {
                                    Toast.makeText(context,"Not enough points..", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        final Button b = (Button) rootView.findViewById(R.id.checkInBtn);


                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                    boolean gpsOn = Functions.LOCATION_FINDER.checkIfGpsIsEnabled();

                                    if(gpsOn) {
                                        Location l = Functions.LOCATION_FINDER.getLocation();
                                        if(l != null) {
                                            String[] details = Functions.getMapDetails(context);
                                            float distance = Functions.getDistance(Double.parseDouble(details[1]), Double.parseDouble(details[2]), l.getLatitude(), l.getLongitude());
                                            if(distance <= Integer.parseInt(getString(R.string.distance_from_shop))) {
                                                ServerCommunication.PointSetter task = new ServerCommunication.PointSetter();
                                                task.execute(MainActivity.account.getEmail(),getString(R.string.appId), getString(R.string.userId), "5");
                                            }
                                            else Toast.makeText(context,  "Too far. Distance: " + distance, Toast.LENGTH_SHORT).show();
                                        }
                                        else Toast.makeText(context,  "Try again..", Toast.LENGTH_SHORT).show();
                                    }
                                }


                        });

                        String points = "POINTS(" + MainActivity.CUR_POINTS + ")";
                        textView.setText(points);
                    }
                    else { // Show sign in button
                        rootView = inflater.inflate(R.layout.fragment_google_signin, container, false);
                        SignInButton signInButton = (SignInButton) rootView.findViewById(R.id.sign_in_button);
                        signInButton.setSize(SignInButton.SIZE_WIDE);
                        signInButton.setColorScheme(SignInButton.COLOR_DARK);

                        signInButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                signIn();
                            }
                        });
                    }

            }
            else if(section == 4) { // Map
                rootView = inflater.inflate(R.layout.fragment_maps, container, false);
                FragmentManager fm = getChildFragmentManager();
                SupportMapFragment mapFragment = SupportMapFragment.newInstance();

                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        String[] details = Functions.getMapDetails(context);
                        LatLng place = new LatLng(Float.parseFloat(details[1]), Float.parseFloat(details[2]));
                        googleMap.addMarker(new MarkerOptions().position(place).title(details[0]));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 16f));
                    }
                });
                fm.beginTransaction().replace(R.id.rl_map, mapFragment).commit();

            }

            return rootView;
        }

        private void signIn() {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            MainActivity.activity.startActivityForResult(signInIntent, 100);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100) {
            GoogleSignInResult result =
                    Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()) {
                account = result.getSignInAccount();

                ServerCommunication.PeopleTask task = new ServerCommunication.PeopleTask(this, account.getIdToken(), account.getEmail());
                task.execute(account.getServerAuthCode());
            }
        }
    }

    public void updateUi(String response) {
        if(response != null) {

            if(response.equals("Illegal")) {
                account = null;
            }
            else if(response.equals("legal")) {
                ServerCommunication.PointGetter task = new ServerCommunication.PointGetter(true);
                task.execute(account.getEmail(), getString(R.string.appId), getString(R.string.userId));

                // Update ui
                mSectionsPagerAdapter.setItem(2);
                mViewPager.setAdapter(mSectionsPagerAdapter);
                mViewPager.setCurrentItem(2);
            }
        }
        else account = null;
    }

    public static void updatePointTxtView() {
        try {
            TextView textView = (TextView) activity.findViewById(R.id.pointsTxt);
            Log.e("POINTS", MainActivity.CUR_POINTS + "");
            textView.setText("POINTS(" +MainActivity.CUR_POINTS + ")");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
