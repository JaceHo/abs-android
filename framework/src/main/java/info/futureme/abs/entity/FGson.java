package info.futureme.abs.entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import info.futureme.abs.conf.FConstants;

/**
 * fgson used for gson to encoding or decoding json string
 * @author Jeffrey
 * @version 1.0
 * @updated 26-一月-2016 15:16:08
 */
public class FGson {
    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, new GsonUTCdateAdapter())
            .create();

    public static Gson gson() {
        return gson;
    }

    public static class GsonUTCdateAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

        private final DateFormat dateFormat;

        public GsonUTCdateAdapter() {
            //dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);      //This is the format I need
            dateFormat = new SimpleDateFormat(FConstants.DATE_FORMAT, Locale.CHINA);      //This is the format I need
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));                               //This is the key line which converts the date to UTC which cannot be accessed with the default serializer
        }

        @Override
        public synchronized JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
            //return new JsonPrimitive(dateFormat.format(date));
            return new JsonPrimitive(date.getTime());
        }

        @Override
        public synchronized Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            return new Date(jsonElement.getAsLong());
            /*try {
                return dateFormat.parse(jsonElement.getAsString());
            } catch (ParseException e) {
               throw new JsonParseException(e);
            }
            */
        }
    }
}
