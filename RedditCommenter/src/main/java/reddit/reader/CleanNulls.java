package reddit.reader;

import java.io.FileInputStream;
import java.io.IOException;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * Simple script that removes empty 
 * and null comments from the MongoDB
 * 
 * @author Phillip Lopez - pgl5711@rit.edu
 *
 */
public class CleanNulls {

	// Setup mongodb connection
	static 	MongoClient mongo;
	static MongoDatabase db;
	static MongoCollection<Document> comments;

	/**
	 * Removes any null comments from the mongo database
	 */
	public static void main(String[] args) throws IOException{
		
        System.getProperties().load(new FileInputStream("mongo.properties"));
		mongo = new MongoClient(System.getProperty("mongo.address"), 
				Integer.valueOf(System.getProperty("mongo.port")));		
		db = mongo.getDatabase("RedditDB");
		comments = db.getCollection("comments");
		comments.deleteMany(new Document().append("text", null));
	}
}
