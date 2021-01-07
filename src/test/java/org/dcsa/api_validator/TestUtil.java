package org.dcsa.api_validator;

import com.fasterxml.jackson.databind.json.JsonMapper;

import java.io.*;
import java.net.URL;
import java.util.Map;

public class TestUtil {

    private static final JsonMapper JSON_MAPPER = new JsonMapper();

    private static InputStream openStream(String resource) throws IOException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
        if (url == null) {
            throw new IllegalStateException("Cannot find json file " + resource);
        }
        return url.openStream();
    }

    public static Map<String, Object> loadJSONObjectFromFile(String resource) {
        return parseResourceWithStream(resource, inputStream -> JSON_MAPPER.reader().readValue(inputStream));
    }

    public static String loadFileAsString(String resource) {
        return parseResourceWithStream(resource, inputStream -> {
            Reader dataInputStream = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            char[] buffer = new char[4096];
            while (dataInputStream.read(buffer) >= 1) {
                stringBuilder.append(buffer);
            }
            return stringBuilder.toString();
        });
    }


    private static void closeStream(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException ignored) {
            }
        }
    }

    private static <T> T parseResourceWithStream(String classpath, ParserFunction<InputStream, T> reader) {
        InputStream inputStream = null;
        try {
            inputStream = openStream(classpath);
            return reader.apply(inputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeStream(inputStream);
        }
    }

    private interface ParserFunction<T, R> {
        R apply(T t) throws Exception;
    }
}
