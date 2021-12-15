package nl.bd.vincent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class FileHandler {
    static File file;
    static String filePath;

    public FileHandler(String filePath) {
        this.filePath = filePath;
        file = new File(filePath);

    }

    public  void saveToFile(String base64String) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write(base64String);
        writer.close();
    }

    public String loadFile() throws IOException, ClassNotFoundException {
            String content = "";
            byte[] bytes = null;

            try
            {
                content = new String ( Files.readAllBytes( Paths.get(filePath) ) );
                Base64.Decoder decoder = Base64.getDecoder();
                bytes = decoder.decode(content);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return new String(bytes);
    }




}
