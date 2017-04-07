package reddit.poster;

import java.util.Random;

import ga.dryco.redditjerk.api.Reddit;
import ga.dryco.redditjerk.implementation.RedditApi;
import ga.dryco.redditjerk.wrappers.Link;
import ga.dryco.redditjerk.wrappers.Subreddit;

/**
 * Loads a random new reddit thread and loads it into a file
 * @author pgl57
 *
 */
public class GetRedditThread {

	// Subreddits we care about
	public static String[] subredStrings = new String[]{
			"westworld", 
			"roastme", "funny",
			"lifeofnorman",
			 "politicaldiscussion", 
			 "tellmeafact"
	};	
	
	public Thread getRandomThread(){
		
		Random r = new Random();
		String subredditName = subredStrings[r.nextInt(subredStrings.length)];
		Reddit red = RedditApi.getRedditInstance("Otis Test");
		Subreddit subreddit = red.getSubreddit(subredditName);
		Link topThread = subreddit.getTop(5).get(r.nextInt(5));
		
		return null;
	}
	
	
}
