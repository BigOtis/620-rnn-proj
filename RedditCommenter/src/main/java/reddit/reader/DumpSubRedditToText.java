package reddit.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

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
	
	static String subreddit = "ShittyPoetry";

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
		FindIterable<Document> docs = threads.find(new Document().append("subreddit", subreddit));
		
		// Retrieve all threads
		//FindIterable<Document> docs = threads.find();
		
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
				printComments(doc, subDoc);
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
	public static void printComments(Document thread, Document subDoc){
		
		String threadId = (String) thread.getString("id");
		FindIterable<Document> commentDocs = comments.find(new Document().append("threadId", threadId));
		List<Document> commentList = new ArrayList<>();
		int num = 1;
		for(Document comment : commentDocs){
			if(num >= 10){
				continue;
			}
			String text = comment.getString("text");
			Document doc = new Document();
			doc.put("comment", text);
			commentList.add(doc);
			num++;
		}
		subDoc.append("comments", commentList);
	}
}
