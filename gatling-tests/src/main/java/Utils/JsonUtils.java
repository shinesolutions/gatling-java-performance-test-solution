package Utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class JsonUtils {
    private static final ObjectMapper mapper = (new ObjectMapper()).registerModule(new JavaTimeModule());

    public static JsonNode readJsonFromFilePath(String filePath) throws IOException {
        return mapper.readTree(new File(filePath));
    }

    public static JsonNode readJsonFromInputStream(InputStream inputStream) throws IOException {
        return mapper.readTree(inputStream);
    }
    public static <T> T nodeToObject(JsonNode node, Class<T> className){
        try {
            return mapper.readerFor(className).readValue(node);
        } catch (Exception e) {
            System.out.println("Error in converting Json " + e.getMessage());
            return null;
        }
    }
}
