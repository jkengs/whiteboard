package io;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.awt.*;
import java.util.ArrayList;

public class JSONHandler {

    // Error Messages
    private final String ERROR_PARSE = "Error when parsing JSON string.";
    private final String ERROR_GET_STRING = "Error getting 'String' value of a key.";
    private final String ERROR_GET_INT= "Error getting 'int' value of a key.";
    private final String ERROR_GET_ARRAY = "Error getting 'ArrayList<String>' value of a key";

    /**
     * JSONHandler default constructor
     */
    public JSONHandler() {}

    /**
     * Returns the value (string) of the given key in the JSON string
     * @param string JSON string
     * @param key key
     * @return string value
     * @throws JSONHandlerException error when parsing JSON string
     */
    public String processString(String string, String key) throws JSONHandlerException {
        return getKeyValue(parseString(string), key);
    }

    /**
     * Returns the value (int) of the given key in the JSON string
     * @param string JSON string
     * @param key key
     * @return int value
     * @throws JSONHandlerException error when parsing JSON string
     */
    public int processInt(String string, String key) throws JSONHandlerException {
        return (getKeyValueInt(parseString(string), key));
    }

    /**
     * Returns the value (array list of string) of the given key in the JSON string
     * @param string JSON string
     * @param key key
     * @return string list
     * @throws JSONHandlerException error when parsing JSON string
     */
    public ArrayList<String> processStringArray(String string, String key) throws JSONHandlerException {
        return getKeyValueArray(parseString(string), key);
    }

    /**
     * Creates a JSON string that is used in message exchange
     * @param type type
     * @param key key
     * @param value value
     * @return JSON string
     * @param <T> type parameter
     */
    public <T> String createJSONString(String type, String key, T value) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(type, key);
        jsonObject.put(key, value);
        return jsonObject.toJSONString();
    }

    /**
     * Creates a JSON object that contains a shape information such as their coordinates
     * @return JSON object
     */
    public JSONObject createShape(String key, String key1, String key2, String key3, String key4, String key5,
                                  String val, int val1, int val2, int val3, int val4, Color val5) {
        JSONObject jsonShape = new JSONObject();
        jsonShape.put(key, val);
        jsonShape.put(key1, val1);
        jsonShape.put(key2, val2);
        jsonShape.put(key3, val3);
        jsonShape.put(key4, val4);
        jsonShape.put(key5, val5.getRGB());
        return jsonShape;
    }

    /**
     * Creates a JSON object that contains a text box information such as their coordinates
     * @return JSON object
     */
    public JSONObject createText(String key, String key1, String key2, String key3, String key4,
                                 String val, String val1, int val2, int val3, Color val4) {
        JSONObject jsonText = new JSONObject();
        jsonText.put(key, val);
        jsonText.put(key1, val1);
        jsonText.put(key2, val2);
        jsonText.put(key3, val3);
        jsonText.put(key4, val4.getRGB());
        return jsonText;
    }

    /**
     * Returns the JSON string containing the drawing from within another JSON string
     * @param string JSON string
     * @param key key
     * @return JSON string
     * @throws JSONHandlerException error when parsing JSON string
     */
    public String processDrawing(String string, String key) throws JSONHandlerException {
        JSONObject parsed = parseString(string);
        JSONObject drawing = (JSONObject) parsed.get(key);
        return drawing.toJSONString();
    }

    /**
     * Parse JSON string
     * @param string JSON string
     * @return JSON Object
     * @throws JSONHandlerException error when parsing JSON string
     */
    private JSONObject parseString(String string) throws JSONHandlerException {
        JSONParser parser = new JSONParser();
        try {
            return (JSONObject) parser.parse(string);
        } catch (Exception e) {
            throw new JSONHandlerException(ERROR_PARSE);
        }
    }

    /**
     * Returns the value (string) of a given key
     * @param jsonObject JSON object
     * @param key key
     * @return string value
     * @throws JSONHandlerException error when retrieving value from the key
     */
    private String getKeyValue(JSONObject jsonObject, String key) throws JSONHandlerException {
        try {
            return (String) jsonObject.get(key);
        } catch (Exception e) {
            throw new JSONHandlerException(ERROR_GET_STRING);
        }
    }

    /**
     * Returns the value (int) of a given key
     * @param jsonObject JSON object
     * @param key key
     * @return int value
     * @throws JSONHandlerException error when retrieving value from the key
     */
    private int getKeyValueInt(JSONObject jsonObject, String key) throws JSONHandlerException {
        try {
            return (int) (long) jsonObject.get(key);
        } catch (Exception e) {
            throw new JSONHandlerException(ERROR_GET_INT);
        }
    }

    /**
     * Returns the value (array list of string) of a given key
     * @param jsonObject JSON object
     * @param key key
     * @return string list
     * @throws JSONHandlerException
     */
    private ArrayList<String> getKeyValueArray(JSONObject jsonObject, String key) throws JSONHandlerException {
        try {
            return (ArrayList<String>) jsonObject.get(key);
        } catch (Exception e) {
            throw new JSONHandlerException(ERROR_GET_ARRAY);
        }
    }
}
