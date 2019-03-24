package API;

import Modals.Example;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DataAPI {


    String BASE_URL = "https://www.googleapis.com/youtube/v3/";
    String API_KEY = "AIzaSyAuNTRdydXrc8_dDAlsduyaKjwkX1CfboA";
    String CHANNEL_ID = "UCCDR5El26qpFJW3ke9ySfOA";

    @GET("search")
    Call<Example> listVideos(@Query("channelId") String channelId,
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
