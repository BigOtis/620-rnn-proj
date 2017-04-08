package reddit.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.bson.Document;
import org.bson.json.JsonWriterSettings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * Uses the input subreddit and searches the MongoDB
 * for all threads that belong to this sub. 
 * 
 * The threads and all comments are placed into
 * a single text file
 * 
 * @author Phillip Lopez - pgl5711@rit.edu
 *
 */
public class DumpSubRedditToText {

	
	// Setup mongodb connection
	static 	MongoClient mongo;
	static MongoDatabase db;
	static MongoCollection<Document> threads;
	static MongoCollection<Document> comments;
	
	static String subreddit = "all";

	/**
	 * Loops through the subreddits specified and
	 * adds them to the local MongoDB
	 */
	public static void main(String[] args) throws IOException{
		
		// Setup our mongodb
        System.getProperties().load(new FileInputStream("mongo.properties"));
		mongo = new MongoClient(System.getProperty("mongo.address"), 
				Integer.valueOf(System.getProperty("mongo.port")));
		db = mongo.getDatabase("RedditDB");
		threads = db.getCollection("threads");
		comments = db.getCollection("comments");
		
		// setup the file to write to
		PrintWriter writer = new PrintWriter(new File(subreddit + "_training.txt"));
		
		// Retrieve all matching thread docs from sub
		//FindIterable<Document> docs = threads.find(new Document().append("subreddit", subreddit));
		
		// Retrieve all threads
		FindIterable<Document> docs = threads.find();
		
		// Loop through all threads in sub
		writer.println("{");
		for(Document doc : docs){
			String title = doc.getString("title");
			String author = doc.getString("author");
			String text = doc.getString("text");
			String url = doc.getString("url");
			Long createDate = doc.getLong("createDate");
			Integer score = doc.getInteger("score");
			Integer numComments = doc.getInteger("numComments");
					
			if(text != null && text.length() > 10){
				Document subDoc = new Document();
				subDoc.append("subreddit", doc.get("subreddit"));
				subDoc.append("title", title);
				subDoc.append("text", text);
				writer.println(subDoc.toJson());
			}
		}
		writer.print("}");	

		writer.flush();
		writer.close();
	}
	
	
	/**
	 * Outputs a comment as pure text in a uniform format
	 */
	public static void printComments(Document thread, PrintWriter writer){
		
		String threadId = (String) thread.getString("id");
		FindIterable<Document> commentDocs = comments.find(new Document().append("threadId", threadId));
		int num = 1;
		for(Document comment : commentDocs){
			
			String number = num+"";
			String author = comment.getString("author");
			String text = comment.getString("text");
			Integer score = comment.getInteger("score");
			
			writer.println("<comment " + "number=[" + number + "]" +
										 "author=[" + author + "]" +
										 "text=[" + text + "]" +
										 "score=[" + score + "] </comment>");
			num++;
		}
	}
}
