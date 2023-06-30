package com.example.civiladvocacy;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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

public class PhotoActivity extends AppCompatActivity {
    String partyWebsite = "";
    private TextView currentLoc;
    private Official official;
    private TextView name;
    private TextView office;
    private ImageView partyIcon;
    private ImageView officialPhoto;
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        constraintLayout = findViewById(R.id.photoActConstraintLayout);
        currentLoc = findViewById(R.id.photoActLoc);
        name = findViewById(R.id.photoActOfficialName);
        office = findViewById(R.id.photoActOfficialOffice);
        partyIcon = findViewById(R.id.bigLogo);
        officialPhoto = findViewById(R.id.bigPic);

        String location = extras.getString("LOCATION");
        official = (Official) extras.getSerializable("OFFICIAL");

        currentLoc.setText(location);
        name.setText(official.getName());
        office.setText(official.getOffice());
        if (Objects.equals(official.getParty(), "Democratic Party")) {
            constraintLayout.setBackgroundColor(Color.parseColor("#0000fe"));
            partyIcon.setImageResource(R.drawable.dem_logo);
            partyWebsite = "https://democrats.org";
        } else if (Objects.equals(official.getParty(), "Republican Party")) {
            constraintLayout.setBackgroundColor(Color.parseColor("#fe0000"));
            partyIcon.setImageResource(R.drawable.rep_logo);
            partyWebsite = "https://www.gop.com";
        } else constraintLayout.setBackgroundColor(Color.parseColor("#000000"));

        Glide.with(this)
                .load(official.getPhotoURL())
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        //holder.officialImage.setImageResource(R.drawable.brokenimage);
                        officialPhoto.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .error(R.drawable.brokenimage)
                .into(officialPhoto);
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

    private void makeErrorAlert(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(msg);
        builder.setTitle("No App Found");

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}


