package info.futureme.abs.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;

/**
 * Created by hippo on 11/13/15.
 */
public class JsonParse {
    public static void main(String[] args){
        Gson gson = new GsonBuilder().create();
        String aa = "kkk";
        TypeAdapter<String> graphAdapter = gson.getAdapter(String.class);
        String json = graphAdapter.toJson(aa);
        System.out.println(json);
    }
}
