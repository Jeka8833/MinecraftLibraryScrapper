package com.Jeka8833.LibraryScrapper;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class VersionManifest {

    @SerializedName("versions")
    public VersionConfig[] versions = new VersionConfig[0];

    public static class VersionConfig {
        @SerializedName("id")
        public String id;
        @SerializedName("url")
        public String url;

        public transient final List<String> urls = new CopyOnWriteArrayList<>();
    }
}
