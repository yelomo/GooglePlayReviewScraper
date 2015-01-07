/**
 * ﻿Copyright (C) 2015 Wilko Oley woley@tzi.de
 *
 * This file is part of GooglePlayReviewScraper
 *
 * GooglePlayReviewScraper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GooglePlayReviewScraper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GooglePlayReviewScraper.  If not, see <http://www.gnu.org/licenses/>.
 **/
package com.yelomo;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Created by Wilko Oley on 20.12.14.
 */
public class ReviewScraper {

    private static String ANDROID_ID = "com.google.android.apps.maps";
    private static String FILE_NAME = ANDROID_ID + "_reviews";
    private static int START_PAGE = 0;
    private static int END_PAGE = 5;
    private static String LANGUAGE = "de";

    public static void main(String[] args) {
        initialize(args);

        for (int i = START_PAGE; i <= END_PAGE; i++) {
            String url = "https://play.google.com/store/getreviews?reviewType=0&pageNum=" + i + "&id=" + ANDROID_ID + "&reviewSortOrder=0&xhr=1&hl=" + LANGUAGE;
            String result = executePost(url);

            //remove unnecessary characters
            result = result.substring(15, result.length() - 5);

            ReviewParser parser = new ReviewParser();
            result = parser.unescapeUnicode(result);
            Map<String, ReviewParser.Review> reviews = parser.parseReviews(result);

            PrintWriter writer = null;
            try {
                writer = new PrintWriter(new FileOutputStream(new File(FILE_NAME + ".csv"), true));
                for (ReviewParser.Review values : reviews.values()) {
                    writer.println(values.toString());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (writer != null)
                    writer.close();
            }
        }
    }

    /**
     * Executes a HTTP POST Request against the targetURL
     *
     * @param targetURL URL which should be called with HTTP POST
     * @return returns the content of the response as a plain string
     */

    private static String executePost(String targetURL) {
        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Language", "de-DE");

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.flush();
            wr.close();

            String line;
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();
            return builder.toString();

        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Initializes the values from the command line input
     *
     * @param args command line arguments
     */

    private static void initialize(String args[]) {
        System.out.println("Google Play Review Scraper starting ...");

        if (args.length == 0) {
            System.out.println("using default Values");
            System.out.println("Example: java -jar google_play_review_scraper.jar [Android_ID] [Start Page] [End Page] [Output File Name] [Language]");
        }
        if (args.length > 1) {
            ANDROID_ID = args[0];
        }
        if (args.length > 2) {
            START_PAGE = Integer.parseInt(args[1]);
            END_PAGE = Integer.parseInt(args[2]);
        }
        if (args.length > 3) {
            FILE_NAME = args[3];
        }
        if (args.length > 4) {
            LANGUAGE = args[4];
        }

        String path = new File(FILE_NAME + ".csv").getAbsoluteFile().getPath();
        System.out.println(" Android ID: " + ANDROID_ID +
                "\n Start Page: " + START_PAGE +
                "\n End Page: " + END_PAGE +
                "\n Output: " + path + FILE_NAME + ".csv" +
                "\n Language: " + LANGUAGE);
    }

}