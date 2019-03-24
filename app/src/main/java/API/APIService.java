package API;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static API.DataAPI.BASE_URL;

public class APIService {

    private static DataAPI service;

    public static DataAPI getInstance() {
        if (service == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_URL)
                    .build();
            service = retrofit.create(DataAPI.class);
            return service;
        } else {
            return service;
        }
    }
}
