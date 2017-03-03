package reddit.reader;

import java.net.MalformedURLException;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class ThreadToTextFile {

	
	// Setup mongodb connection
	static 	MongoClient mongo;
	static MongoDatabase db;
	static MongoCollection<Document> threads;
	static MongoCollection<Document> comments;

	/**
	 * Loops through the subreddits specified and
	 * adds them to the local MongoDB
	 */
	public static void main(String[] args) throws MalformedURLException{
		
		mongo = new MongoClient(System.getProperty("mongo.address"), 
				Integer.valueOf(System.getProperty("mongo.port")));
		db = mongo.getDatabase("RedditDB");
		threads = db.getCollection("threads");
		comments = db.getCollection("comments");
		
	}
	
}
