package org.material.galleryexample;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.material.profileimv.ProfileImageView;

import java.util.List;

/**
 * Created by Pedro on 23/03/2016.
 */
public class SimpleListAdapter extends RecyclerView.Adapter<SimpleListAdapter.ProfileHolder> {

    private List<SimpleListModel> mList;
    private LayoutInflater mInflator;

    public SimpleListAdapter(Context context, List<SimpleListModel> list) {
        mInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mList = list;
    }


    @Override
    public ProfileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ProfileHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_model, parent, false));
    }

    @Override
    public void onBindViewHolder(ProfileHolder holder, int position) {
        SimpleListModel model = mList.get(position);
        holder.view.setImage(model.image);
        holder.view.setFeatureIcon(model.featureIcon);
        holder.view.setFeatureText(model.featureText + "");
        holder.view.setFrame(model.frame.clone());
        holder.view.setMode(model.mode);
    }

    @Override
    public int getItemCount() { return mList.size(); }

    /**
     * Holder
     */
    public class ProfileHolder extends RecyclerView.ViewHolder {

        ProfileImageView view;

        public ProfileHolder(View itemView) {
            super(itemView);
            view = (ProfileImageView) itemView.findViewById(R.id.profile_view);
        }
    }
}
