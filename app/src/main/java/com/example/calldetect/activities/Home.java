package com.example.calldetect.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.calldetect.R;
import com.example.calldetect.adapters.ItemDrawerAdapter;
import com.example.calldetect.adapters.ViewPagerAdapter;
import com.example.calldetect.database.DataBaseManager;
import com.example.calldetect.firebase_data_base_manager.ImageStorageHelper;
import com.example.calldetect.firebase_data_base_manager.SaveContact;
import com.example.calldetect.firebase_data_base_manager.SignalNumberHelper;
import com.example.calldetect.firebase_data_base_manager.UserHelper;
import com.example.calldetect.fragment.FragmentCalls;
import com.example.calldetect.fragment.FragmentContact;
import com.example.calldetect.messageriePrincipale.ActivityMessaferiePrincipale;
import com.example.calldetect.messageriePrincipale.contact.ContactPhone;
import com.example.calldetect.messageriePrincipale.infoUser.InfoUser;
import com.example.calldetect.models.ItemDrawer;
import com.example.calldetect.models.SignalNumber;
import com.example.calldetect.models.User;
import com.example.calldetect.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Home extends AppCompatActivity {
    public static final String SEARCH_EXTRA = "Search";
    private TabLayout tabLayout;
    private ListView drawer_list;
    private ViewPager viewPager;
    private DrawerLayout drawerLayout;
    private FirebaseUser userF;
    private User user;
    private ShapeableImageView imageUser;
    private ImageButton edit_profile;
    private TextView user_address;
    private TextView user_name;
    private FragmentCalls fragmentCall = new FragmentCalls();
    private FragmentContact fragmentContact = new FragmentContact();
    private FloatingActionButton composer, addContact;
    private ItemDrawerAdapter adapter;
    private boolean is_certify = false;
    private  List<ItemDrawer> itemDrawerList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initView();

        // Set the information about the user.
        setNavigationList();

        setListeners();
        initSupportActionBar();

        initTabLayout();
        setUserInfoListener();

        insertContactSignale();

    }

    private void setListeners() {
        addContact.setVisibility(View.GONE);
        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                startActivity(intent);
            }
        });

        composer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, ComposerContact.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Function to initialize the views.
     */
    private void initView() {
        drawer_list = findViewById(R.id.drawer_list);
        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);
        drawerLayout = findViewById(R.id.drawer_layout);
        composer = findViewById(R.id.composeur);
        addContact = findViewById(R.id.addcontact);

        @SuppressLint("InflateParams") View header_drawer = LayoutInflater.from(this)
                .inflate(R.layout.nav_header_home, null, false);
        imageUser = header_drawer.findViewById(R.id.imageView);
        imageUser.setShapeAppearanceModel(
                ShapeAppearanceModel.builder().setAllCornerSizes(ShapeAppearanceModel.PILL)
                        .build()
        );

        user_name = header_drawer.findViewById(R.id.user_name);
        user_address = header_drawer.findViewById(R.id.user_phone_number);
        edit_profile = header_drawer.findViewById(R.id.edit_profile_btn);

        drawer_list.addHeaderView(header_drawer);

        if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Home.this,new String[]{Manifest.permission.READ_PHONE_STATE}, PackageManager.PERMISSION_GRANTED);
        }


        // extrait des conatcts dans le telephone et sauvegarde dans firebase et on ajoute le code du pays devant ceux qui non pas

        List<ContactPhone> listContatFirebase  = getContactFirebase();

        for (int i = 0; i <listContatFirebase.size() ; i++) {
            if (listContatFirebase.get(i).getNumero().contains("+")){

                listContatFirebase.get(i).setNumero(listContatFirebase.get(i).getNumero().replace(" ",""));
                SaveContact.addContact(listContatFirebase.get(i));
            }else {

                DataBaseManager manager = new DataBaseManager(this);
                List<InfoUser> infoUsers = manager.lectureInfoUser();
                manager.close();
                listContatFirebase.get(i).setNumero(("+"+infoUsers.get(0).getCodepays()+listContatFirebase.get(i).getNumero()).replace(" ",""));
                SaveContact.addContact(listContatFirebase.get(i));
            }



        }

        // finn


    }

    @Override
    protected void onResume() {
        super.onResume();
        userF = FirebaseAuth.getInstance().getCurrentUser();
        if (userF == null) {
            Utils.setToastMessage(this, "User is not connect .");
            Intent intentInscription = new Intent(this, Login.class);
            startActivity(intentInscription);
            finish();
        }
        setUserInformation();
    }

    private void setUserInformation() {
        UserHelper.getUserById(userF.getUid())
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            is_certify = user.getCertification();
                            String user_nameStr = user.getName() + " " + user.getSurname();
                            user_address.setText(user.getEmail());
                            user_name.setText(user_nameStr);
                            if (!user.getProfile().equals("")) {
                                ImageStorageHelper.getStorageReference().child(user.getProfile())
                                        .getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (!task.isSuccessful()){
                                            if (task.getException() instanceof FirebaseNetworkException)
                                                Utils.setDialog(Home.this, getString(R.string.error_not_connect));
                                            return;
                                        }
                                        Glide.with(Home.this)
                                                .load(task.getResult())
                                                .placeholder(R.mipmap.image_profile_foreground)
                                                .error(R.mipmap.image_profile_foreground)
                                                .into(imageUser);
                                    }
                                });
                            }
                        } else {
                            is_certify = false;
                            user_name.setText(getResources().getString(R.string.app_name));
                            user_address.setText(getResources().getString(R.string.app_mail));
                        }
                        displayCertificateOption();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        is_certify = false;
                        user_name.setText(getResources().getString(R.string.app_name));
                        user_address.setText(getResources().getString(R.string.app_mail));
                        displayCertificateOption();
                    }
                });
    }

    /**
     * Function that display the certificate options
     */
    private void displayCertificateOption() {
        if (is_certify) {
            if (!itemDrawerList.get(0).getOption().equals("pub_day"))
                itemDrawerList.add(0, new ItemDrawer("pub_day", ResourcesCompat.getDrawable(getResources(),
                        R.drawable.ic_today_pub, null), getString(R.string.pub_jour)));
            if (!itemDrawerList.get(itemDrawerList.size() - 4).getOption().equals("survey"))
                itemDrawerList.add(itemDrawerList.size() - 4, new ItemDrawer("survey",
                        ResourcesCompat.getDrawable(getResources(), R.drawable.ic_sondage, null),
                        getString(R.string.sondage)));
            if (!itemDrawerList.get(itemDrawerList.size() - 5).getOption().equals("consult"))
                itemDrawerList.add(itemDrawerList.size() - 5, new ItemDrawer("consult",
                        ResourcesCompat.getDrawable(getResources(), R.drawable.ic_eye, null),
                        getString(R.string.consult)));
            if (itemDrawerList.get(itemDrawerList.size() - 2).getOption().equals("certify"))
                itemDrawerList.remove(itemDrawerList.size() - 2);
        }

        adapter.notifyDataSetChanged();
    }

    /**
     * Function to set the action bar.
     */
    private void initSupportActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar
        ,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchIntent = new Intent(Home.this, ComposerContact.class);
                searchIntent.putExtra(SEARCH_EXTRA, "search");
                startActivity(searchIntent);
            }
        });
    }

    /**
     * Function to initialize the tabLayout.
     */
    private void initTabLayout() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(fragmentCall, "Call");
        adapter.addFragment(fragmentContact, "Contacts");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    composer.setVisibility(View.VISIBLE);
                    addContact.setVisibility(View.GONE);
                }else {
                    composer.setVisibility(View.GONE);
                    addContact.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1)
                    fragmentContact.setBooleans();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    /**
     * Function set the navigation listener.
     */
    private void setNavigationList() {

        itemDrawerList.add(new ItemDrawer("message", ResourcesCompat.getDrawable(getResources(),
                R.drawable.ic_message_black_24dp, null), getString(R.string.message)));
        itemDrawerList.add(new ItemDrawer("notification", ResourcesCompat.getDrawable(getResources(),
                R.drawable.ic_notifications, null), getString(R.string.notification)));
        itemDrawerList.add(new ItemDrawer("recording", ResourcesCompat.getDrawable(getResources(),
                R.drawable.ic_enregistre, null), getString(R.string.enregistrement)));
        itemDrawerList.add(new ItemDrawer("faq", ResourcesCompat.getDrawable(getResources(),
                R.drawable.ic_faq, null), getString(R.string.FAQ)));
        itemDrawerList.add(new ItemDrawer("invite", ResourcesCompat.getDrawable(getResources(),
                R.drawable.ic_invitaion, null), getString(R.string.invite)));
        itemDrawerList.add(new ItemDrawer("certify", ResourcesCompat.getDrawable(getResources(),
                R.drawable.ic_invitaion, null), getString(R.string.certify_user)));
        itemDrawerList.add(new ItemDrawer("setting", ResourcesCompat.getDrawable(getResources(),
                R.drawable.ic_settings, null), getString(R.string.setting)));
        adapter = new ItemDrawerAdapter(itemDrawerList);
        drawer_list.setAdapter(adapter);
        adapter.setOnItemClickListener(new ItemDrawerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent();
                ItemDrawer item = itemDrawerList.get(position);
                switch (item.getOption()) {
                    case "pub_day":
                        Utils.setToastMessage(Home.this, "Publication du jour.");
                        // TODO Set the pub jour page.
                        break;
                    case "message":
                        // TODO Parti qui permet de renvoyer à la messagerie.
                        if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(Home.this,new String[]{Manifest.permission.SEND_SMS}, PackageManager.PERMISSION_GRANTED);
                        }else {
                            intent.setClass(Home.this, ActivityMessaferiePrincipale.class);
                            startActivity(intent);
                        }
                        break;
                    case "notification":
                        Utils.setToastMessage(Home.this, "Notification.");
                        // TODO Set the notification page.
                        break;
                    case "recording":
                        Utils.setToastMessage(Home.this, "Enregistrement.");
                        // TODO Set the enregistrement page.
                        break;
                    case "faq":
                        Utils.setToastMessage(Home.this, "FAQ");
                        // TODO Set the faq page.
                        break;
                    case "consult":
                        Utils.setToastMessage(Home.this, "Consult.");
                        // TODO Set the consult page.
                        break;
                    case "survey":
                        Utils.setToastMessage(Home.this, "Sondage");
                        // TODO Set the sondage page.
                        break;
                    case "invite":
                        Utils.invite(Home.this);
                        break;
                    case "certify":
                        if (!is_certify) {
                            intent.putExtra(Utils.USER_EXTRA, user);
                            intent.setClass(Home.this, CertificateUser.class);
                            startActivity(intent);
                        }
                        break;
                    case "setting":
                        intent.setClass(Home.this, Settings.class);
                        startActivity(intent);
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
    }
    /**
     * Set to the user profile when click here.
     */
    private void setUserInfoListener() {
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Home.this, Profile.class);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings)
            startActivity(new Intent(this, Settings.class));
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            finish();
        }
    }


    /**
     * cette methode permet de d'inserer dans la bd firebase les contact signaler
     */
    public void insertContactSignale(){

        DataBaseManager manager = new DataBaseManager(this);
        List<String> list = manager.recuperationContactDangereux();
        String nuberUser = manager.lectureInfoUser().get(0).getNumero();
        String codePays = manager.lectureInfoUser().get(0).getCodepays();
        manager.close();

        Date currentDate = Calendar.getInstance().getTime();
        for (int i = 0; i <list.size() ; i++) {
            if (list.get(i).contains("+")){
                SignalNumberHelper.addSignalNumber( new SignalNumber(list.get(i).replace(" ",""),nuberUser,currentDate));
            }else {
                SignalNumberHelper.addSignalNumber( new SignalNumber(("+"+codePays+list.get(i)).replace(" ",""),nuberUser,currentDate));
            }

        }

    }



    /**
     * cette methode retourne la liste des contacts
     * @return
     */
    private List<ContactPhone> getContactFirebase(){

        List<ContactPhone> list = new ArrayList<>();
        Cursor cursor = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            list.add(new ContactPhone(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)), cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))));
        }
        return list;

    }

}

