package API;

import Modals.Example;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DataAPI {


    String BASE_URL = "https://www.googleapis.com/youtube/v3/";
    String API_KEY = "AIzaSyAXzGr9QZRqhTMQ3AbdaSkvcPkWMVHpmIs";
    String CHANNEL_ID = "UC_x5XG1OV2P6uZZ5FSM9Ttw";
    String PLAYLIST_ID = "PLWlUJU11tp4eh1sLw2VTuc5CuZDPNnsBr";

    @GET("search")
    Call<Example> listVideos(@Query("channelId") String channelId,
                             @Query("key") String key,
                             @Query("part") String part,
                             @Query("order") String order,
                             @Query("regionCode") String regionCode,
                             @Query("maxResults") String maxResults,
                             @Query("pageToken") String pageToken);

    @GET("search")
    Call<Example> listPlaylist(@Query("playlistId") String playlistId,
                             @Query("key") String key,
                             @Query("part") String part,
                             @Query("order") String order,
                             @Query("regionCode") String regionCode,
                             @Query("maxResults") String maxResults,
                             @Query("pageToken") String pageToken);


    @GET("videos")
    Call<Example> getVideo(@Query("part") String part,
                            @Query("id") String id,
                            @Query("key") String key);

}
