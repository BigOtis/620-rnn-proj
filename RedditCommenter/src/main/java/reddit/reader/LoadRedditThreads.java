package reddit.reader;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import ga.dryco.redditjerk.api.Reddit;
import ga.dryco.redditjerk.api.enums.FromPast;
import ga.dryco.redditjerk.api.enums.Sorting;
import ga.dryco.redditjerk.implementation.RedditApi;
import ga.dryco.redditjerk.wrappers.Comment;
import ga.dryco.redditjerk.wrappers.Link;
import ga.dryco.redditjerk.wrappers.RedditThread;
import ga.dryco.redditjerk.wrappers.Subreddit;
import reddit.mongo.MongoFacade;

/**
 * A simple class to populate a local MongoDB
 * with Reddit threads and associated comments
 * 
 * @author Phillip Lopez - pgl5711@rit.edu
 *
 */
public class LoadRedditThreads {
	
	// Subreddits we want to load in
	static String[] subredStrings = new String[]{//"westworld", "roastme", "funny",
												"lifeofnorman",
												 //"politicaldiscussion", "tellmeafact"
												 };	
	
	// Setup mongodb connection
	private static MongoFacade mongo = MongoFacade.getInstance();

	/**
	 * Loops through the subreddits specified and
	 * adds them to the local MongoDB
	 */
	public static void main(String[] args) throws IOException{
		
		
		// Reddit API
		Reddit red = RedditApi.getRedditInstance("Otis Test");
				
		// Go through all of the subreddits
		for(String subredname : subredStrings){
			
			System.out.println("*************************************************************");
			System.out.println("Gathering top 1000 posts for subreddit: " + subredname + "...");
			System.out.println("*************************************************************");

			Subreddit subred = red.getSubreddit(subredname);
			List<Link> links = subred.getTop(1000, FromPast.YEAR);
			
			// Go through the top threads from the past year
			for(Link link : links){
				
				// Some info needed for comments
				String threadId = link.getId();
				Integer numComments = link.getNumComments();
				
				// Add the thread to the mongodb
				mongo.addThread(link, subredname);
				
				System.out.println("\t\tAdding " + numComments + " comments for this thread...");
				RedditThread thread = red.getRedditThread("https://www.reddit.com/" + link.getPermalink(), Sorting.TOP);
				List<Comment> commentList = thread.getFlatComments();
				
				// Get all the comments
				for(Comment comment : commentList){
					mongo.addComment(comment, threadId);
				}
			}
		}
	}
}
