# n801-popular-movies
## Udacity Android Developer Nanodegree - Popular Movies Project
##### Popular Movies is an application project for the Udacity Android Developer Nanodegree
##### Author: Paul Mender
#### Overview
Although Stage 2 is a continuation of the Stage 1 application, there is considerable refactoring in Stage 2:
* Due to the additional requirements posed by Stage 2, the inheritance structure is expanded to maximize code reuse within the Popular Movies application. Moreover, the redesign provides a framework for future projects. See the Class Diagrams section.
* The restructured class hierarchy supports the logical and physical (project file structure) partitioning between the UI thread and worker/asynchronous threads.  As a result, performance and memory management is improved.
* Generic types were also employed to accomplish code reuse, and thus further support the object-oriented design of the framework.
* The User Interface includes significant enhancements through the application of material design concepts, such as color contrasts, readability, and touch feedback.
##### Table of Contents
* Stage 1 Requirements
* Stage 2 Requirements
* Stage 2 Suggestions
* General Requirements
* Other Implementation Guide Requirements
* Movie DB Requirements
* Naming Conventions
* Screenshots
* Class Diagrams
#### Stage 1 Requirements:
1. Completed: App is written solely in the Java Programming Language.
1. Completed: Movies are displayed in the mainrid of their corresponding movie poster thumbnails.
1. Completed: UI contains an element (i.e a spinner or settings menu) to toggle the sort order of the movies by: most popular, highest rated.
1. Completed: When a movie poster thumbnail is selected, the movie details screen is launched.
1. Completed: UI contains a screen for displaying the details for a selected movie.
1. Completed: Movie details layout contains title, release date, movie poster, vote average, and plot synopsis.
1. Completed: When a user changes the sort criteria (“most popular and highest rated”) the main view gets updated correctly.
1. Completed: In a background thread, app queries the /movie/popular or /movie/top_rated API for the sort criteria specified in the settings menu.
#### Stage 2 Requirements: (Addendum to Stage 1 requirements above.)
9. Completed: Movie Details layout contains a section for displaying trailer videos and user reviews.
1. Completed: When a user changes the sort criteria (most popular, highest rated, and favorites) the main view gets updated correctly.
1. Completed: When a trailer is selected, app uses an Intent to launch the trailer. (Either in the youtube app or a web browser).
1. Completed: In the movies detail screen, a user can tap a button(for example, a star) to mark it as a Favorite. This is for a local movies collection that you will maintain and does not require an API request.
1. Completed: App requests for related videos for a selected movie via the /movie/{id}/videos endpoint in a background thread and displays those details when the user selects a movie.
1. Completed: App requests for user reviews for a selected movie via the /movie/{id}/reviews endpoint in a background thread and displays those details when the user selects a movie.
1. Completed: The titles and ids of the user's favorite movies are stored in a ContentProvider backed by a SQLite database. This ContentProvider is updated whenever the user favorites or unfavorites a movie.  See Screenshot 1. Movie Detail with Reviews.
1. Completed: When the "favorites" setting option is selected, the main view displays the entire favorites collection based on movie ids stored in the ContentProvider. See Screenshot 2. Favorites.
#### Stage 2 Suggestions:
17. Completed: Extend the favorites ContentProvider to store the movie poster, synopsis, user rating, and release date, and display them even when offline.
1. Completed: Implement sharing functionality to allow the user to share the first trailer’s YouTube URL from the movie details screen.
*Note: A share icon button is included on each video in the list that allows sharing each video, not just the first trailer.*
#### General Requirements:
App conforms to common standards found in the Android Nanodegree General Project Guidelines
* Completed: Java      
* Completed: Git    Note: For Stage 1, did not include the type.
* Completed: Core   Note: The app may or may not adhere to Google Play Store App policies.
* Completed: Tablet Note: Ignored for this project per Carlos
#### Other Implementation Guide Requirements
1. Completed: IMPORTANT: PLEASE REMOVE YOUR API KEY WHEN SHARING CODE PUBLICLY
* Note: You will need to configure your own API key values in the gradle.properties file by adding the following lines: 
    ##### api_key_v3="*your Movie DB API Key here*"
    ##### youtube_api_key_v3="*your YouTube API Key here*"
2. Completed: You must make sure your app does not crash when there is no network connection!
#### Additional Features (added by Paul):
* Add About menu option (on the main screen action bar menu) and include the attribution requirements found at https://www.themoviedb.org/faq/api/. See Screenshot 3. About.
* Highlight the selected movie when returning from the Movie Details screen. See Screenshot 4. Selected Movie Highlight. 
* Add a share button for each video listed. See Screenshot 5. Videos List with Share Icon.
#### Naming Conventions
* Where applicable, Package/Directory names in the framework reflect the superclass package name in Android (or Java). For example: The AsyncTaskLoaderParcelables class extends AsyncTaskLoader. AsyncTaskLoader is located in the android.content package, therefore the AsyncTaskLoaderParcelables is contained in the com.paulmender.android.content package.
* During phase one of the Popular Movies implementation the m (member) and s (private static) prefixes for fields were used based on conventions observed in example code, and the Android Open Source Project (AOSP) http://s.android.com/source/code-style.html#follow-field-naming-conventions. Although there are still some examples in the Popular Movies code that follow the convention, the majority of the code now adheres to the Google style guide https://google.github.io/styleguide/javaguide.html#s5.1-identifier-names, which shuns the prefixes.
### Screenshots
The screenshots were generated with the Android Debug Bridge through the capture button on the Logcat, then resized in Windows Paint.
1. Detail with Reviews

![Popular Movies Detail w Reviews](/images/50/pkmiPopMoviesDetailReviews.png) 

2. Favorites

![Popular Movies Favorites](/images/50/pkmiPopMoviesFavorites.png) 

3. About

![Popular Movies About](/images/50/pkmiPopMoviesAbout.png)

4. Selected Movie Highlight

![Popular Movies Highlight](/images/50/pkmiPopMoviesHighlight.png) 

5. Videos List with Share Icon

![Popular Movies Share Videos](/images/50/pkmiPopMoviesDetailVideos.png) 

### Class Diagrams
The class diagrams were generated with the simpleUMLCE Version: 0.01 plugin, then edited with Windows Paint.
For more class documentation, browse the JavaDoc/index.html.

1. Activity Class Collaborations

![Activity Class](/images/uml/pkmiActivityClass.png) 

2. Activity Class Hierarchy

![Activity Class Hierarchy](/images/uml/pkmiActivityHierarchy.png) 

3. Activity/Fragment Dependency

![Fragment Class Dependency](/images/uml/pkmiFragmentDependency.png) 

4. Movie Favorite Provider Class

![Movie Favorite Provider Class](/images/uml/pkmiMovieFavoritesProviderClass.png) 
