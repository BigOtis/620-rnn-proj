package reddit.reader;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class CleanNulls {

	// Setup mongodb connection
	static 	MongoClient mongo;
	static MongoDatabase db;
	static MongoCollection<Document> comments;

	/**
	 * Removes any null comments from the mongo database
	 */
	public static void main(String[] args){
		
		mongo = new MongoClient("localhost", 27017);
		db = mongo.getDatabase("RedditDB");
		comments = db.getCollection("comments");
		comments.deleteMany(new Document().append("text", null));
	}
}
