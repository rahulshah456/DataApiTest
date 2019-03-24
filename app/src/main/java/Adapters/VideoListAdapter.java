package Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.dataapitest.MainActivity;
import com.app.dataapitest.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import Modals.Item;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoHolder> {


    private static final String TAG = VideoListAdapter.class.getSimpleName();
    private static RecyclerViewClickListener itemListener;
    private String videoURL;
    Context mContext;


    public VideoListAdapter(Context context) {
        mContext = context;
    }

    public interface RecyclerViewClickListener
    {
        public void recyclerViewListClicked(View v, int position);
    }


    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(mContext).inflate(R.layout.video_list_item, parent, false);
        return new VideoHolder(itemView);
    }


    public class VideoHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView thumbnail;
        public TextView videoTitle;

        public VideoHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            thumbnail = (ImageView) itemView.findViewById(R.id.video_thumbnailID);
            videoTitle = (TextView) itemView.findViewById(R.id.titleTextID);
        }

        @Override
        public void onClick(View v) {

            //itemListener.recyclerViewListClicked(v, this.getLayoutPosition());

        }
    }


    @Override
    public void onBindViewHolder(final VideoHolder holder, int position) {

        Item videoData = MainActivity.videoList.get(position);

        videoURL = videoData.getSnippet().getThumbnails().getMedium().getUrl();

        Glide.with(mContext)
                .load(videoURL)
                .apply(new RequestOptions()
                    .centerCrop())
                .into(holder.thumbnail);
        holder.videoTitle.setText(videoData.getSnippet().getTitle());

    }


    @Override
    public int getItemCount() {
        return MainActivity.videoList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void addVideos(List<Item> list){
        MainActivity.videoList.addAll(list);
        notifyDataSetChanged();
    }

    public void clearList(){
        MainActivity.videoList.clear();
    }

    public List<Item> getItemList(){
        return MainActivity.videoList;
    }


}
