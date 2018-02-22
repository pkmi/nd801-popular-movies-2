# n801-popular-movies
## Udacity Android Developer Nanodegree - Popular Movies Project
##### Popular Movies is an application project for the Udacity Android Developer Nanodegree
##### Author: Paul Mender
#### Overview
Although Stage 2 is a continuation of the Stage 1 application, there is considerable refactoring in Stage 2:
* Due to the additional requirements posed by Stage 2, the inheritance structure is expanded to maximize code reuse within the Popular Movies application. Moreover, the redesign provides a framework for future projects.
* The restructured class hierarchy supports the logical and physical (project file structure) partitioning between the UI thread and worker/asynchronous threads.  As a result, performance and memory management is improved.
* Generic types were also employed to accomplish code reuse, and thus support the object-oriented design of the framework.
* The User Interface includes significant enhancements through the application of material design concepts, such as color contrasts, readability, and touch feedback.

##### Table of Contents
* Screen Shots
* Stage 1 Requirements
* Stage 2 Requirements
* Stage 2 Suggestions
* General Requirements
* Other Implementation Guide Requirements
* Movie DB Requirements
* Known Issues
* Class Diagrams
* Naming Conventions
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
1. Completed: The titles and ids of the user's favorite movies are stored in a ContentProvider backed by a SQLite database. This ContentProvider is updated whenever the user favorites or unfavorites a movie.  See ![Popular Movies Detail w Reviews](/images/50/pkmiPopMoviesDetailReviews.png).
1. Completed: When the "favorites" setting option is selected, the main view displays the entire favorites collection based on movie ids stored in the ContentProvider. See ![Popular Movies Favorites](/images/50/pkmiPopMoviesFavorites.png).
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
* Add About menu option (on the main screen action bar menu) and include the attribution requirements found at https://www.themoviedb.org/faq/api/. See ![Popular Movies About](/images/50/pkmiPopularMoviesAbout.png).
* Highlight the selected movie when returning from the Movie Details screen. See ![Popular Movies Highlight](/images/50/pkmiPopularMoviesHighlight.png).
* Add a share button for each video listed. See ![Popular Movies Share Videos](/images/50/pkmiPopMoviesDetailVideos.png).
#### Known Issues
After playing a video from the video list, this message error is message is logged in the Logcat:

    E/ActivityThread: Activity com.google.android.youtube.api.StandalonePlayerActivity has leaked 
    IntentReceiver aalm@7e6e2ae that was originally registered here. 
    Are you missing a call to unregisterReceiver()?
 
Tried targetSdkVersion 16 and 20 to correct the YouTubePlayer Error, but the project would not compile.
### Class Diagrams


