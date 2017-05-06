package reddit.mongo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import ga.dryco.redditjerk.wrappers.Comment;
import ga.dryco.redditjerk.wrappers.Link;

/**
 * Singleton class used to wrap the MongoDatabase object
 * for use specific to the RedditDB
 * 
 * @author Phillip Lopez - pgl5711@rit.edu
 */
public class MongoFacade {

	
	/**
	 * Singleton
	 */
	private static MongoFacade instance = new MongoFacade();
	
	/**
	 * MongoClient API
	 */
	public MongoClient mongo;
	
	/**
	 * The opened database
	 */
	public MongoDatabase db;
	
	public MongoCollection<Document> threads;
	public MongoCollection<Document> comments;
	public MongoCollection<Document> torch_threads;
	
	public MongoFacade(){
        try {
			System.getProperties().load(new FileInputStream("mongo.properties"));
		} catch (IOException e) {
			String myCurrentDir = System.getProperty("user.dir") + File.separator + System.getProperty("sun.java.command") .substring(0, System.getProperty("sun.java.command").lastIndexOf(".")) .replace(".", File.separator); System.out.println(myCurrentDir);
			System.err.println("MISSING MONGO.PROPERTIES FILE. DB WILL NOT LOAD CORRECTLY.");
		}
		mongo = new MongoClient(System.getProperty("mongo.address"), 
				Integer.valueOf(System.getProperty("mongo.port")));		
		db = mongo.getDatabase("RedditDB");
		threads = db.getCollection("threads");
		comments = db.getCollection("comments");
		torch_threads = db.getCollection("torch_threads");
	}
	
	public static MongoFacade getInstance(){
		return instance;
	}
	
	public Document getThreadDoc(String id){
		return threads.find(new Document("id",id)).first();
	}
	
	/**
	 * Adds the given comment to our MongoDB
	 * @param comment
	 * @param threadId
	 */
	public void addComment(Comment comment, String threadId){
		
		String id = comment.getId();
		if(commentExists(id)){
			return;
		}
		
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
	public boolean addThread(Link link, String subreddit){
		
		String threadID = link.getId();
		if(threadExists(threadID)){
			return false;
		}
		
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
		return true;
	}
	
	public boolean threadExists(String id){
		
		Document query = new Document("id", id);
		return threads.find(query).first() != null;
	}
	
	public boolean commentExists(String id){
		
		Document query = new Document("id", id);
		return comments.find(query).first() != null;
	}
}
