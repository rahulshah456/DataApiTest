package Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.dataapitest.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.api.services.youtube.model.Playlist;

import java.util.ArrayList;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {


    private static final String TAG = PlaylistAdapter.class.getSimpleName();
    public OnItemClickListener onItemClickListener;
    private ArrayList<Playlist> mPlayList = new ArrayList<>();
    Context mContext;

    public PlaylistAdapter(Context context,ArrayList<Playlist> mPlayList) {
        mContext = context;
        this.mPlayList = mPlayList;
    }

    public interface OnItemClickListener {
        void OnItemClick(int position);
        void OnItemLongClick(int position);
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(mContext).inflate(R.layout.video_list_item, parent, false);
        return new ViewHolder(itemView);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ImageView thumbnail;
        public TextView playlistTitle;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            thumbnail = (ImageView) itemView.findViewById(R.id.video_thumbnailID);
            playlistTitle = (TextView) itemView.findViewById(R.id.titleTextID);
        }

        @Override
        public void onClick(View v) {

            onItemClickListener.OnItemClick(this.getLayoutPosition());

        }

        @Override
        public boolean onLongClick(View v) {

            onItemClickListener.OnItemLongClick(this.getLayoutPosition());
            return true;
        }
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Playlist playlistData = mPlayList.get(position);

        Glide.with(mContext)
                .load(playlistData.getSnippet().getThumbnails().getMedium().getUrl())
                .apply(new RequestOptions()
                        .centerCrop())
                .into(holder.thumbnail);

        holder.playlistTitle.setText(playlistData.getSnippet().getTitle());

    }


    @Override
    public int getItemCount() {
        return mPlayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void addVideos(List<Playlist> list){
        mPlayList.addAll(list);
        notifyDataSetChanged();
    }

    public void clearList(){
        mPlayList.clear();
    }

    public List<Playlist> getItemList(){
        return mPlayList;
    }
}
