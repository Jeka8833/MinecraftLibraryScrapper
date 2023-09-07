package com.Jeka8833.LibraryScrapper;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class Main {

    private static final Path PATH =
            Paths.get("C:\\Users\\Jeka8833\\AppData\\Roaming\\.minecraft\\versions\\version_manifest_v2.json");

    private static final Gson GSON = new Gson();
    private static final OkHttpClient client = new OkHttpClient();
    private static final ExecutorService executors = Executors.newWorkStealingPool();

    public static void main(String[] args) throws IOException, InterruptedException {
        VersionManifest versionManifest = GSON.fromJson(Files.readString(PATH), VersionManifest.class);
        for (final VersionManifest.VersionConfig version : versionManifest.versions) {
            executors.execute(() -> {
                System.out.println("Requesting: " + version.id + " - " + version.url);
                Request request = new Request.Builder().url(version.url).build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    executors.execute(new UrlScrapper(response.body().string(), version.urls));
                    System.out.println("Done: " + version.id + " - " + version.url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        executors.shutdown();
        executors.awaitTermination(60, TimeUnit.SECONDS);

        while (true) {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter regex: ");
            String input = scanner.nextLine();
            if (input.equals("exit")) break;
            try {
                Pattern pattern = Pattern.compile(input);

                List<SearchResult> searchResults = new ArrayList<>();

                for (final VersionManifest.VersionConfig version : versionManifest.versions) {
                    for (final String url : version.urls) {
                        if (pattern.matcher(url).find())
                            searchResults.add(new SearchResult(version.id, url));
                    }
                }

                List<Map.Entry<String, List<SearchResult>>> group = searchResults.stream()
                        .collect(Collectors.groupingBy(searchResult -> searchResult.url))
                        .entrySet().stream()
                        .sorted((o1, o2) -> o2.getKey().compareTo(o1.getKey()))
                        .toList();

                group.forEach(stringListEntry -> {
                    System.out.print("Url: " + stringListEntry.getKey() + " ");
                    stringListEntry.getValue().forEach(searchResult -> System.out.print(searchResult.id + " "));
                    System.out.println();
                });
            } catch (PatternSyntaxException e) {
                e.printStackTrace();
            }
        }
    }
}
