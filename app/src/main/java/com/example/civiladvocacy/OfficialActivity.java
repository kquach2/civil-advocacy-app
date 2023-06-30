package com.example.civiladvocacy;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.Objects;

public class OfficialActivity extends AppCompatActivity {

    String partyWebsite = "";
    private ConstraintLayout constraintLayout;
    private TextView currentLoc;
    private Official official;
    private TextView name;
    private TextView office;
    private TextView party;
    private ImageView officialImage;
    private ImageView facebook;
    private ImageView youtube;
    private ImageView twitter;
    private ImageView partyIcon;
    private TextView addressLabel;
    private TextView address;
    private TextView phoneLabel;
    private TextView phone;
    private TextView email;
    private TextView emailLabel;
    private TextView websiteLabel;
    private TextView website;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        constraintLayout = findViewById(R.id.officialActConstraintLayout);
        currentLoc = findViewById(R.id.officialActLoc);
        name = findViewById(R.id.officialName);
        office = findViewById(R.id.officialOffice);
        party = findViewById(R.id.officialParty);
        officialImage = findViewById(R.id.officialImage);
        facebook = findViewById(R.id.facebookLogo);
        youtube = findViewById(R.id.youtubeLogo);
        twitter = findViewById(R.id.twitterLogo);
        partyIcon = findViewById(R.id.partyLogo);
        addressLabel = findViewById(R.id.addressLabel);
        address = findViewById(R.id.address);
        phoneLabel = findViewById(R.id.phoneLabel);
        phone = findViewById(R.id.phone);
        emailLabel = findViewById(R.id.emailLabel);
        email = findViewById(R.id.email);
        websiteLabel = findViewById(R.id.websiteLabel);
        website = findViewById(R.id.website);

        String location = extras.getString("LOCATION");
        official = (Official) extras.getSerializable("OFFICIAL");

        currentLoc.setText(location);

        name.setText(official.getName());
        if (!official.getOffice().equals("")) office.setText(official.getOffice());
        else {
            office.setVisibility(View.GONE);
            office.setVisibility(View.GONE);
        }
        party.setText(String.format("(%s)", official.getParty()));
        if (Objects.equals(official.getParty(), "Democratic Party")) {
            constraintLayout.setBackgroundColor(Color.parseColor("#0000fe"));
            partyIcon.setImageResource(R.drawable.dem_logo);
            partyWebsite = "https://democrats.org";
        } else if (Objects.equals(official.getParty(), "Republican Party")) {
            constraintLayout.setBackgroundColor(Color.parseColor("#fe0000"));
            partyIcon.setImageResource(R.drawable.rep_logo);
            partyWebsite = "https://www.gop.com";
        } else constraintLayout.setBackgroundColor(Color.parseColor("#000000"));

        if (!Objects.equals(official.getFacebookID(), "")) {

        } else facebook.setVisibility(View.INVISIBLE);

        if (!Objects.equals(official.getYoutubeID(), "")) {

        } else youtube.setVisibility(View.INVISIBLE);

        if (!Objects.equals(official.getTwitterID(), "")) {

        } else twitter.setVisibility(View.INVISIBLE);

        if (!Objects.equals(official.getPhotoURL(), "")) {  // url has to be https
            Glide.with(this)
                    .load(official.getPhotoURL())
                    .error(R.drawable.brokenimage)
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            //holder.officialImage.setImageResource(R.drawable.brokenimage);
                            officialImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(officialImage);
        }
        else {
            //Glide.with(mainAct).clear(holder.officialImage);
            officialImage.setImageResource(R.drawable.missing);
            officialImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
        if (!Objects.equals(official.getAddress(), "")) {
            address.setText(underlineString(official.getAddress()));
        }
        else {
            addressLabel.setVisibility(View.GONE);
            address.setVisibility(View.GONE);
        }

        if (!Objects.equals(official.getPhoneNumber(), "")) phone.setText(underlineString(official.getPhoneNumber()));
        else {
            phoneLabel.setVisibility(View.GONE);
            phone.setVisibility(View.GONE);
        }

        if (!Objects.equals(official.getEmail(), "")) email.setText(underlineString(official.getEmail()));
        else {
            emailLabel.setVisibility(View.GONE);
            email.setVisibility(View.GONE);
        }

        if (!Objects.equals(official.getWebsite(), "")) website.setText(underlineString(official.getWebsite()));
        else {
            websiteLabel.setVisibility(View.GONE);
            website.setVisibility(View.GONE);
        }
    }

    public SpannableString underlineString (String s) {
        SpannableString result = new SpannableString(s);
        result.setSpan(new UnderlineSpan(), 0, s.length(), 0);
        return result;
    }

    public void clickFacebook(View v) {
        // You need the FB user's id for the url
        String FACEBOOK_URL = "https://www.facebook.com/"+official.getFacebookID();

        Intent intent;

        // Check if FB is installed, if not we'll use the browser
        if (isPackageInstalled("com.facebook.katana")) {
            String urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToUse));
        } else {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_URL));
        }

        // Check if there is an app that can handle fb or https intents
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            makeErrorAlert("No Application found that handles ACTION_VIEW (fb/https) intents");
        }
    }

    public void clickYouTube(View v) {
        String name = official.getYoutubeID();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + name)));
        }
    }

    public void clickTwitter(View v) {
        Intent intent;
        String name = official.getTwitterID();
        try {
            // get the Twitter app if possible
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
        }
        startActivity(intent);
    }

    public void clickMap(View v) {
        String address = official.getAddress();

        Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));

        Intent intent = new Intent(Intent.ACTION_VIEW, mapUri);

        // Check if there is an app that can handle geo intents
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            makeErrorAlert("No Application found that handles ACTION_VIEW (geo) intents");
        }
    }

    public void clickCall(View v) {
        String number = official.getPhoneNumber();

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            makeErrorAlert("No Application found that handles ACTION_DIAL (tel) intents");
        }
    }

    public void clickEmail(View v) {
        String[] addresses = new String[]{official.getEmail()};

        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));

        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, "This comes from EXTRA_SUBJECT");
        intent.putExtra(Intent.EXTRA_TEXT, "Email text body from EXTRA_TEXT...");

        // Check if there is an app that can handle mailto intents
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            makeErrorAlert("No Application found that handles SENDTO (mailto) intents");
        }
    }

    public void clickWebsite(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(official.getWebsite()));

        // Check if there is an app that can handle https intents
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            makeErrorAlert("No Application found that handles ACTION_VIEW (https) intents");
        }
    }

    public void clickLogo(View v) {
        if (Objects.equals(partyWebsite, "")) return;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(partyWebsite));

        // Check if there is an app that can handle https intents
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            makeErrorAlert("No Application found that handles ACTION_VIEW (https) intents");
        }
    }

    public void clickPhoto(View v) {
        if (Objects.equals(official.getPhotoURL(), "")) return;
        Intent intent = new Intent(this, PhotoActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable("OFFICIAL", official);
        extras.putString("LOCATION", currentLoc.getText().toString());
        intent.putExtras(extras);
        startActivity(intent);
    }

    public boolean isPackageInstalled(String packageName) {
        try {
            return getPackageManager().getApplicationInfo(packageName, 0).enabled;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    private void makeErrorAlert(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(msg);
        builder.setTitle("No App Found");

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
