package info.futureme.abs.example.util;

import android.annotation.SuppressLint;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class DateDeserializerUtils
  implements JsonDeserializer<Date>
{
  public Date deserialize(JsonElement json, Type type, JsonDeserializationContext context)
    throws JsonParseException
  {
	  try {
		  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		  return sdf.parse(json.getAsJsonPrimitive().getAsString());
	} catch (Exception e) {
		
	}
	  return null;
  }
}
