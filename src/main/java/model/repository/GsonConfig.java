package model.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class GsonConfig {

    public static final Gson INSTANCE = new GsonBuilder().setPrettyPrinting().create();

    private GsonConfig() {}
}
