package com.example.civiladvocacy;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class OfficialViewHolder extends RecyclerView.ViewHolder {
    ImageView officialImage;
    TextView officialOffice;
    TextView officialNameParty;

    OfficialViewHolder(View view) {
        super(view);
        officialImage = view.findViewById(R.id.mainActImage);
        officialOffice = view.findViewById(R.id.mainActOffice);
        officialNameParty = view.findViewById(R.id.mainActNameParty);
    }

}
