package com.example.civiladvocacy;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;
import java.util.Objects;

public class OfficialAdapter extends RecyclerView.Adapter<OfficialViewHolder> {
    private static final String TAG = "OfficialAdapter";
    private final List<Official> officialList;
    private final MainActivity mainAct;

    public OfficialAdapter(List<Official> officialList, MainActivity mainAct) {
        this.officialList = officialList;
        this.mainAct = mainAct;
    }

    @NonNull
    @Override
    public OfficialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.official_entry, parent, false);

        itemView.setOnClickListener(mainAct);
        return new OfficialViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OfficialViewHolder holder, int position) {
        Official official = officialList.get(position);
        holder.officialNameParty.setText(String.format("%s (%s)", official.getName(), official.getParty()));
        holder.officialOffice.setText(official.getOffice());
        String photoURL = official.getPhotoURL();
        if (!Objects.equals(photoURL, "")) {  // url has to be https
            Glide.with(mainAct)
                    .load(photoURL)
                    .error(R.drawable.brokenimage)
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            //holder.officialImage.setImageResource(R.drawable.brokenimage);
                            holder.officialImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(holder.officialImage);
        }
        else {
            Glide.with(mainAct).clear(holder.officialImage);
            holder.officialImage.setImageResource(R.drawable.missing);
        }
    }

    @Override
    public int getItemCount() {
        return officialList.size();
    }
}
