package readWrite;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.*;

public class Reader {
    public static <T> T readJson(String filename, Class<T> classType) {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(filename), "UTF-8")) {
            Gson gson = new Gson();
            return gson.fromJson(reader, classType);
        } catch (JsonSyntaxException e) {
            System.out.println("Invalid JSON: " + filename);
        } catch(FileNotFoundException e) {
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
