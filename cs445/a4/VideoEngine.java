package cs445.a4;

/**
 * This abstract data type is a predictive engine for video ratings in a streaming video system. It
 * stores a set of users, a set of videos, and a set of ratings that users have assigned to videos.
 * It cannot contain duplicates of videos, users, ratings(one user having multiple ratings for the same
 * video), or television series.
 */
public interface VideoEngine {

    /**
     * The abstract methods below are declared as void methods with no parameters. You need to
     * expand each declaration to specify a return type and parameters, as necessary. You also need
     * to include a detailed comment for each abstract method describing its effect, its return
     * value, any corner cases that the client may need to consider, any exceptions the method may
     * throw (including a description of the circumstances under which this will happen), and so on.
     * You should include enough details that a client could use this data structure without ever
     * being surprised or not knowing what will happen, even though they haven't read the
     * implementation.
     */

    /**
     * Adds a new video to the system. Any object that is passed in that implements Video (TvEpisode, Film)
	 * should work in the same way. If newVideo is null, this method will throw a NullPointerException. 
	 * If newVideo is already in the system, which cannot contain duplicates, then this method will 
	 * throw an IllegalArgumentException. Being added to the system allows for the other methods in
	 * this data structure to be used upon it. 
	 *
	 * @param newVideo a video that will be added to the system
	 * @throws NullPointerException if newVideo is null
	 * @throws IllegalArgumentException if newVideo is already in the system
     */
    void addVideo(Video newVideo);

    /**
     * Removes an existing video from the system. This method will search for the specified video,
	 * removedVideo, and remove it from the system. If removedVideo is null, a NullPointerException 
	 * will be thrown. If removedVideo is not on the system, than an IllegalArgumentException will be thrown.
	 * If no Videos exist on the system at all, then a SystemEmptyException is thrown. Being removed
	 * from the system makes the video unable to be processed by the other methods (excluding addVideo).
	 *
	 * @param removedVideo video to be sought and removed from the system
	 * @throws NullPointerException if removedVideo is null
	 * @throws IllegalArgumentException if removedVideo is not in the system
	 * @throws SystemEmptyException if the system is empty and a Video is attempted to be removed from it
     */
    void removeVideo(Video removedVideo);

    /**
     * Adds an existing television episode to an existing television series. If newTvEpisode or series
	 * is null, then a NullPointerException is thrown. A video can only exist in one series at a time,
	 * so if it already exists in another series or it is already in the series that it is trying to be added to 
	 * an IllegalArgumentException is thrown. The only options for which newTvEpisode and series can come from
	 * are from TvEpisodes and TvSeries that are already on the system, so there is not a case where either could
	 * not be on the system.
	 *
	 * @param newTvEpisode the episode to be added to the series
	 * @param series the series to which the episode will be added
	 * @throws NullPointerException if newTvEpisode or series is null
	 * @throws IllegalArgumentException if newTvEpisode is already in the series or is in a different series
     */
    void addToSeries(TvEpisode newTvEpisode, TvSeries series);

    /**
     * Removes a television episode from a television series. If removedTvEpisode or series is null then
	 * a NullPointerException is thrown.  If removedTvEpisode is not in the series to start with, an IllegalArgumentException 
	 * is thrown. If the series is empty then a SeriesEmptyException is thrown. This method assumes the user already has
	 * access to removedTvEpisode, so it is not returned. The only options for which removedTvEpisode and series can come from
	 * are from TvEpisodes and TvSeries that are already on the system, so there is not a case where either could
	 * not be on the system.
	 *
	 * @param removedTvEpisode the episode to be removed from the series
	 * @param series the series to which the episode will be removed
	 * @throws NullPointerException if removedTvEpisode or series is null
	 * @throws IllegalArgumentException if removedTvEpisode was never in series
	 * @throws SeriesEmptyException if the series is empty and a TvEpisode is attempted to be removed from it
     */
    void removeFromSeries(TvEpisode removedTvEpisode, TvSeries series);

    /**
     * Sets a user's rating for a video, as a number of stars from 1 to 5. A rating of 5 stars is considered to
	 * be the best rating possible to give, while a rating of 1 star is considered the worst. If theUser already rated
	 * theVideo, then the rating will be cleared and reset to what the new rating is. If theVideo or theUser is null, then a 
	 * NullPointerException will be thrown. If rating is not between 1 and 5, than an IllegalArgumentException
	 * will be thrown.
	 *
	 * @param theUser the user in which the rating will be attributed to
	 * @param theVideo the video which the user will be rating
	 * @param rating the rating that the video will have as an int from 1 to 5
	 * @throws NullPointerException if theUser or theVideo is null
	 * @throws IllegalArgumentException if rating is not between 1 and 5
     */
    void rateVideo(User theUser, Video theVideo, int rating);

    /**
     * Clears a user's rating on a video. If this user has rated this video and the rating has not
     * already been cleared, then the rating is cleared and the state will appear as if the rating
     * was never made. If this user has not rated this video, or if the rating has already been
     * cleared, then this method will throw an IllegalArgumentException.
     *
     * @param theUser user whose rating should be cleared
     * @param theVideo video from which the user's rating should be cleared
     * @throws IllegalArgumentException if the user does not currently have a rating on record for
     * the video
     * @throws NullPointerException if either the user or the video is null
     */
    public void clearRating(User theUser, Video theVideo);

    /**
     * Predicts the rating a user will assign to a video that they have not yet rated, as a number
     * of stars from 1 to 5. Based upon theUser and theVideo, an int between 1 and 5 is returned,
	 * with 1 being the lowest rated score and 5 being the best possible score to give.
	 * The rating is based off of what other users have rated the video and the other videos that theUser has 
	 * rated. If the video has similar ratings to other videos theUser has already rated favorably and other users
	 * have rated this Video in a similar way, then a rating will be predicted accordingly.
	 * If noone else has rated this video, then other users will not be factored in and only videos that the 
	 * user has rated will be accounted for for their similarity in quality to the given video. 
	 * If the user has not rated any videos, then only what other users
	 * have said about the video will be accounted for. If the user has not rated any videos and no other users
	 * have rated the video, giving the prediction no basis, then an IllegalArgumentException is thrown.
	 * If theUser or theVideo are null, then a NullPointerException is thrown.
	 *
	 * @param theUser the user for which the rating will be predicted
	 * @param theVideo the video for which the rating will be predicted
	 * @return an int rating between 1 and 5 for the given video
	 * @throws NullPointerException if theUser or theVideo is null
	 * @throws IllegalArgumentException if theUser already rated theVideo, or if theUser has rated 0 Videos and the video has no ratings
     */
    int predictRating(User theUser, Video theVideo);

    /**
     * Suggests a video for a user (e.g.,based on their predicted ratings). This method will search the system until a
	 * video is found that can be suggested to the user. The predicted rating must be 3 stars or
	 * higher to be eligible to be suggested to the user.  Videos of higher rating will take priority, so the first one 
	 * with 5 stars found will be suggested. If one with 5 stars is not found, then one with 4 stars will be suggested.
	 * The same goes with 3 star videos. Videos whose predicted ratings are 2 stars or fewer are not eligible at all. 	 
	 * If a predicted rating cannot be given for a video for any reason, then that video is not eligible to be suggested.
	 * If there are no videos that satisfy that criteria, then a SuggestionNotFoundException is thrown.
	 * If theUser is null, then a NullPointerException is thrown. 
	 * If theUser has rated videos but a suggestion cannot be found (theUser has rated all the videos on the system),
	 * then a SuggestionNotFoundException is thrown. If there are no videos on the system, and therefore no ratings
	 * or suggestions could be found, then a SystemEmptyException is thrown. If none of the videos on the system have been
	 * rated and theUser has rated nothing, then a SuggestionNotFoundException is thrown.
	 *
	 * @param theUser the user for whom the video will be suggested for
	 * @return the video which will be suggested
	 * @throws NullPointerException if theUser is null
	 * @throws SuggestionNotFoundException if the user has rated everything on the system, a Video with at least 3 stars predicted rating is not found,
	 * or if nothing on the system has been rated and theUser has rated nothing (no predicted ratings can be found to base a suggestion)
	 * @throws SystemEmptyException if the system is empty
     */
    Video suggestVideo(User theUser);


}

