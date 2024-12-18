package com.second.jtrace.core.protocol;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public class GsonSerializer{

    public static  <T> T deserialize(Class<T> clazz, byte[] bytes) {
        try {
            Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodec()).create();
            String json = new String(bytes, StandardCharsets.UTF_8);
            return gson.fromJson(json, clazz);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static  <T> byte[] serialize(T object) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodec()).create();
        String json = gson.toJson(object);
        return json.getBytes(StandardCharsets.UTF_8);
    }

    public static String toJson(Object object) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodec()).create();
        return gson.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodec()).create();
        return gson.fromJson(json, clazz);
    }

    static class ClassCodec implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {

        @Override
        public Class<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                String str = json.getAsString();
                return Class.forName(str);
            } catch (ClassNotFoundException e) {
                throw new JsonParseException(e);
            }
        }

        @Override             //   String.class
        public JsonElement serialize(Class<?> src, Type typeOfSrc, JsonSerializationContext context) {
            // class -> json
            return new JsonPrimitive(src.getName());
        }
    }
}
