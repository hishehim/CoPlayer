package statics;

import org.json.JSONException;
import org.json.JSONObject;
import play.Logger;

import javax.annotation.Nonnull;

/**
 * Created by Mike on 4/26/2016.
 */
public class JSONObjectWrapper {

    public static JSONObject error(@Nonnull String message) {
        JSONObject errObj = new JSONObject();
        try {
            errObj.put("error", message);
        } catch (JSONException exp) {
            Logger.error("Unexpected error");
        }
        return errObj;
    }
}