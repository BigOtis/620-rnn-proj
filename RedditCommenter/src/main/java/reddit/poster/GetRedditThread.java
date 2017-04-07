package reddit.poster;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Random;

import ga.dryco.redditjerk.api.Reddit;
import ga.dryco.redditjerk.api.enums.Sorting;
import ga.dryco.redditjerk.implementation.RedditApi;
import ga.dryco.redditjerk.wrappers.Comment;
import ga.dryco.redditjerk.wrappers.Link;
import ga.dryco.redditjerk.wrappers.RedditThread;
import ga.dryco.redditjerk.wrappers.Subreddit;
import reddit.model.ThreadLink;
import reddit.mongo.MongoFacade;

/**
 * Loads a random new reddit thread and loads it into a file
 * @author pgl57
 *
 */
public class GetRedditThread {

	private static MongoFacade mongo = MongoFacade.getInstance();
	
	// Subreddits we care about
	public static String[] subredStrings = new String[]{
			"westworld", 
			"roastme", "funny",
			"lifeofnorman",
			 "politicaldiscussion", 
			 "tellmeafact"
	};	
	
	public reddit.model.ThreadLink getRandomThread(){
		
		Random r = new Random();
		String subredditName = subredStrings[r.nextInt(subredStrings.length)];
		Reddit red = RedditApi.getRedditInstance("Otis Test");
		Subreddit subreddit = red.getSubreddit(subredditName);
		Link topThread = subreddit.getTop(5).get(r.nextInt(5));
		
		RedditThread thread;
		try {
			thread = red.getRedditThread("https://www.reddit.com/" + topThread.getPermalink(), Sorting.TOP);
		} catch (MalformedURLException e) { 
			System.err.println("Unable to reach reddit thread: " + "https://www.reddit.com/" + topThread.getPermalink());
			return null;
		}
		
		mongo.addThread(topThread, subredditName);
		List<Comment> commentList = thread.getFlatComments();
		for(Comment comment : commentList){
			mongo.addComment(comment, topThread.getId());
		}
		
		return new ThreadLink(topThread, mongo.getThreadDoc(topThread.getId()));
	}
	
	
}
