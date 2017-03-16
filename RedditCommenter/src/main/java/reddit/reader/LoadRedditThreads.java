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

/**
 * A simple class to populate a local MongoDB
 * with Reddit threads and associated comments
 * 
 * @author Phillip Lopez - pgl5711@rit.edu
 *
 */
public class LoadRedditThreads {
	
	// Subreddits we want to load in
	static String[] subredStrings = new String[]{"westworld", "roastme", "funny","lifeofnorman",
												 "politicaldiscussion", "tellmeafact"};	
	
	// Setup mongodb connection
	static MongoClient mongo;
	static MongoDatabase db;
	static MongoCollection<Document> threads;
	static MongoCollection<Document> comments;

	/**
	 * Loops through the subreddits specified and
	 * adds them to the local MongoDB
	 */
	public static void main(String[] args) throws IOException{
		
        System.getProperties().load(new FileInputStream("mongo.properties"));
		mongo = new MongoClient(System.getProperty("mongo.address"), 
				Integer.valueOf(System.getProperty("mongo.port")));		
		db = mongo.getDatabase("RedditDB");
		threads = db.getCollection("threads");
		comments = db.getCollection("comments");
		
		// Reddit API
		Reddit red = RedditApi.getRedditInstance("Otis Test");
				
		// Go through all of the subreddits
		for(String subredname : subredStrings){
			
			System.out.println("*************************************************************");
			System.out.println("Gathering top 100 posts for subreddit: " + subredname + "...");
			System.out.println("*************************************************************");

			Subreddit subred = red.getSubreddit(subredname);
			List<Link> links = subred.getTop(100, FromPast.YEAR);
			
			// Go through the top 100 threads from the past year
			for(Link link : links){
				
				// Some info needed for comments
				String threadId = link.getId();
				Integer numComments = link.getNumComments();
				
				// Add the thread to the mongodb
				addThread(link, subredname);
				
				System.out.println("\t\tAdding " + numComments + " comments for this thread...");
				RedditThread thread = red.getRedditThread("https://www.reddit.com/" + link.getPermalink(), Sorting.TOP);
				List<Comment> commentList = thread.getFlatComments();
				
				// Get all the comments
				for(Comment comment : commentList){
					addComment(comment, threadId);
				}
			}
		}
		
		mongo.close();
	}
	
	/**
	 * Adds the given comment to our MongoDB
	 * @param comment
	 * @param threadId
	 */
	public static void addComment(Comment comment, String threadId){
		
		String id = comment.getId();
		String author = comment.getAuthor();
		String text = comment.getBody();
		Integer score = comment.getScore();
		Long createdDate = comment.getCreated();
		
		Document commentDoc = new Document()
				.append("id", id)
				.append("author", author)
				.append("text", text)
				.append("createdDate", createdDate)
				.append("score", score)
				.append("threadId", threadId);
		
		
		comments.insertOne(commentDoc);
	}
	
	/**
	 * Adds the given thread to our MongoDB
	 * @param link
	 */
	public static void addThread(Link link, String subreddit){
		
		String threadID = link.getId();
		String threadTitle = link.getTitle();
		String threadText = link.getSelftext();
		String threadLinkURL = link.getUrl();
		String threadAuthor = link.getAuthor();
		long threadCreated = link.getCreated();
		Integer threadScore = link.getScore();
		Integer numComments = link.getNumComments();
		
		System.out.println("\tMongo Insert Thread: " + threadTitle);

		Document threadDoc = new Document()
				.append("id", threadID)
				.append("title", threadTitle)
				.append("subreddit", subreddit)
				.append("text", threadText)
				.append("url", threadLinkURL)
				.append("author", threadAuthor)
				.append("createDate", threadCreated)
				.append("score", threadScore)
				.append("numComments", numComments);	
		
		threads.insertOne(threadDoc);
	}
}
