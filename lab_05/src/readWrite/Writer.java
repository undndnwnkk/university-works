package readWrite;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;

public class Writer {
    public static <T> void writeJson(String filename, T data) {
        try(FileWriter fileWriter = new FileWriter(filename)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            fileWriter.write(gson.toJson(data));
            System.out.println("Success!");
        } catch(IOException e) {
            System.out.println("Error! I can't write to file!");
        }
    }
}
