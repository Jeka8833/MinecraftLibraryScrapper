package com.Jeka8833.LibraryScrapper;

import java.util.Collection;
import java.util.regex.Pattern;

public class UrlScrapper implements Runnable {
    private static final Pattern URL_PATTERN = Pattern.compile("(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

    private final String text;
    private final Collection<String> urls;

    public UrlScrapper(String text, Collection<String> urls) {
        this.text = text;
        this.urls = urls;
    }

    @Override
    public void run() {
        URL_PATTERN.matcher(text).results().forEach(matchResult -> urls.add(matchResult.group()));
    }
}
