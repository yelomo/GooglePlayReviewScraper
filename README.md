GooglePlayReviewScraper
==============

The GooglePlayReviewScraper is a java based command line tool to extract the public reviews 
of a app published on the Google Play Store. The results are written to a csv file specified in [filename].

To fetch the reviews a public but **undocumented** API of the play store is used. 
There is no guarantee this api stays the same or even will be available in the future.

Usage
==============

If you do not care about building this project download /dist and run

```java -jar google_play_review_scraper.jar [android-id] [start] [end] [filename] [language] ```

default values
--------------
All arguments are optional and default values are used if an argument is no set on the cli.
```
[android-id] = "com.google.android.apps.maps"
[start] = 0
[end] = 5
[filename] = [android-id]_reviews
[language] = "de"
```
Building the project
==============
The project is released with a gradle wrapper and can be built with the gradlew or gradlew.bat depending on your OS.
The gradle task build will compile the code, package a jar containing all dependencies and copy it to the /dist folder.

````./gradlew build ```` (Linux) or ````gradlew.bat build ```` (Windows)
