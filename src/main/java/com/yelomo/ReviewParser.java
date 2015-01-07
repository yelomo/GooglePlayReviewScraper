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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Wilko Oley on 20.12.14.
 */
public class ReviewParser {

    /**
     * Replaces all Unicode Sequences like \u003c with its corresponding unicode character.
     * After the conversion all backslashes will be removed from the input string.
     *
     * @param string the String which should be unescaped
     * @return returns a unicode String
     */
    public String unescapeUnicode(String string) {
        //Replace all Unicode sequences with its Character
        Matcher matcher = Pattern.compile("\\\\u((?i)[0-9a-f]{4})").matcher(string);
        while (matcher.find()) {
            int code = Integer.valueOf(matcher.group(1), 16);
            string = string.replaceAll("\\" + matcher.group(0), String.valueOf((char) code));
        }
        //Remove the escape characters
        string = string.replace("\\", "");
        return string;
    }

    /**
     * Parses the reviews from the given HTML String using Jsoup.
     *
     * @param unescapedString HTML String returned by the Google Play Store with unescaped unicode characters.
     * @return a HasMap with review id as string and the review holder class as value
     */
    public Map<String, Review> parseReviews(String unescapedString) {
        HashMap<String, Review> resultData = new HashMap<>();
        Document doc = Jsoup.parse(unescapedString);
        Elements singleReviews = doc.select("div.single-review");

        for (Element element : singleReviews) {
            String id = element.select("div.review-header").attr("data-reviewid");

            Element reviewInfo = element.select("div.review-info").first();
            String name = reviewInfo.select("span.author-name").text();
            String date = reviewInfo.select("span.review-date").text();
            String rating = reviewInfo.select("div.review-info-star-rating > div.star-rating-non-editable-container").attr("aria-label");
            rating = rating.replaceAll("\\D+", "");

            Element reviewBody = element.select("div.review-body").first();
            String title = element.select("span.review-title").text();
            String description = reviewBody.text();
            //Remove double white spaces with one white space
            description = description.trim().replaceAll(" +", " ");
            //we just need the description text (this should be adjusted to fit your language and needs)
            description = description.replace("Vollständige Bewertung", "");
            description = description.replace("Full Review", "");

            Review review = new Review();
            review.authorName = name;
            review.date = date;
            review.rating = Integer.parseInt(rating);
            review.title = title;
            review.description = description;

            resultData.put(id, review);
        }
        return resultData;
    }

    /**
     * A Holder for the parsed Reviews
     */
    public class Review {
        String authorName;
        String date;
        int rating;
        String title;
        String description;

        @Override
        public String toString() {
            return date + ";" + authorName + ";" + rating + ";" + title + ";" + description;
        }
    }

}
