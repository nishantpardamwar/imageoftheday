package contrailtech.com.imageoftheday;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by serious on 29/1/16.
 */
public class Parser {
    private static List<ItemClass> itemList = null;
    private static ItemClass item;
    private static JSONArray itemListArray;

    public static List<ItemClass> parseData(JSONObject itemListObject) {
        try {
            itemList = new ArrayList<>();
            itemListArray = itemListObject.getJSONArray("imageList");
            if (itemListArray.length() == 0)
                return null;
            for (int i = 0; i < itemListArray.length(); i++) {
                item = new ItemClass();
                item.setName(itemListArray.getJSONObject(i).getString("title"));
                item.setDate(itemListArray.getJSONObject(i).getLong("date"));
                item.setDescription(itemListArray.getJSONObject(i).getString("description"));
                item.setImageUrl(itemListArray.getJSONObject(i).getString("imageUrl"));
                itemList.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return itemList;
    }
}
