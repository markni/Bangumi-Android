package io.github.scarletsky.bangumi.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Timer;

import io.github.scarletsky.bangumi.R;
import io.github.scarletsky.bangumi.api.models.Subject;
import io.github.scarletsky.bangumi.api.models.UserCollection;
import io.github.scarletsky.bangumi.events.GetSubjectEvent;
import io.github.scarletsky.bangumi.utils.BusProvider;

public class CardRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_NORMAL = 1;
    public static final int VIEW_TYPE_WITH_PROGRESS = 2;

    private int lastPosition = -1;

    private static final String TAG = CardRecyclerAdapter.class.getSimpleName();

    private Context ctx;
    private List<?> data;
    private int viewType = 1;

    public CardRecyclerAdapter(Context ctx, List<?> data) {
        this.ctx = ctx;
        this.data = data;
    }

    public void resetLastPosition(){
        lastPosition = -1;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.adapter_card, parent, false);
        return viewType == VIEW_TYPE_WITH_PROGRESS ? new ViewHolderWithProgress(v) : new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder h = (ViewHolder) holder;
        final Subject mSubject;

        setAnimation(h.mCard, position);

        if (data.get(position) instanceof Subject) {

            mSubject = (Subject) data.get(position);

        } else if (data.get(position) instanceof UserCollection) {

            ViewHolderWithProgress hp = (ViewHolderWithProgress) holder;
            UserCollection mUserCollection = (UserCollection) data.get(position);
            mSubject = mUserCollection.getSubject();

            int currentProgress = mUserCollection.getEpStatus();
            String maxProgress = mSubject.getEps() == 0 ? "??" : String.valueOf(mSubject.getEps());

            hp.mProgressLabel.setText(currentProgress + "/" + maxProgress);
            hp.mProgressBar.setMax(mSubject.getEps());
            hp.mProgressBar.setProgress(mUserCollection.getEpStatus());
            hp.mProgressBar.getProgressDrawable().setColorFilter(ctx.getResources().getColor(R.color.accent), PorterDuff.Mode.SRC_IN);


        } else {

            return;

        }

        h.mCardTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BusProvider.getInstance().post(new GetSubjectEvent(mSubject));
                    }
                }, 100);//delayed to show the full ripple animation

            }
        });



        // set card title
        if (!mSubject.getNameCn().equals("")) {
            h.mCardTitle.setText(mSubject.getNameCn());
        } else {
            h.mCardTitle.setText(mSubject.getName());
        }

        h.mCardImage.setTag(mSubject.getName());


        // set card image
        if (mSubject.getImages() != null) {
            Picasso
                    .with(ctx)
                    .load(mSubject.getImages().getLarge())
                    .into(h.mCardImage);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        public CardView mCard;
        public ImageView mCardImage;
        public TextView mCardTitle;

        public ViewHolder(View v) {
            super(v);
            mCard = (CardView) v.findViewById(R.id.card);
            mCardImage = (ImageView) v.findViewById(R.id.card_image);
            mCardTitle = (TextView) v.findViewById(R.id.card_title);
        }
    }

    private static class ViewHolderWithProgress extends ViewHolder {

        public ProgressBar mProgressBar;
        public TextView mProgressLabel;

        public ViewHolderWithProgress(View v) {
            super(v);
            mProgressBar = (ProgressBar) v.findViewById(R.id.card_progress);
            mProgressLabel = (TextView) v.findViewById(R.id.card_progress_label);
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressLabel.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(ctx, R.anim.slide_in_down);
            if (position < 4){
                animation.setStartOffset(position * 100);
            }
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
