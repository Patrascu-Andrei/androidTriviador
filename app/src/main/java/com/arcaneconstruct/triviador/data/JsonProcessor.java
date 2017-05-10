package com.arcaneconstruct.triviador.data;

import android.content.Context;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by Archangel on 4/14/2016.
 */
public class JsonProcessor {
    public static ConfigData readConfig(Context context, String levels) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            ConfigData config =
                    mapper.readValue(LoaderHelper.parseFileToString(context, levels + ".json"),ConfigData.class);
            return config;
        } catch (JsonParseException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
