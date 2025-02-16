package readWrite;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import collections.Flat;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Vector;

public class Reader {
    public static Vector<Flat> readJson(String filename) {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(filename), "UTF-8")) {
            Gson gson = new Gson();
            Type type = new TypeToken<Vector<Flat>>() {}.getType();
            return gson.fromJson(reader, type);
        } catch (JsonSyntaxException e) {
            System.out.println("Invalid JSON: " + filename);
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filename);
        } catch (UnsupportedEncodingException e) {
            System.out.println("Unsupported encoding: " + filename);
        } catch (IOException e) {
            System.out.println("Error reading file: " + filename);
        }
        return null;
    }

    public static <T> T parseJsonFromString(String json, Class<T> classType) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, classType);
        } catch (JsonSyntaxException e) {
            System.out.println("Invalid JSON format!");
        }
        return null;
    }
}
