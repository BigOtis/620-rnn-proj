package reddit.poster;

import org.bson.Document;

import ga.dryco.redditjerk.api.Reddit;
import ga.dryco.redditjerk.implementation.RedditApi;
import ga.dryco.redditjerk.wrappers.Link;
import ga.dryco.redditjerk.wrappers.User;
import reddit.mongo.MongoFacade;

/**
 * Simple class used to post specific threads quickly 
 * based on their titles. Not meant for production use.
 * @author Phillip Lopez - pgl5711@rit.edu
 *
 */
public class TorchPost {

	public static void main(String args[]){
		
		// API is broken for authentication ...
		Reddit red = RedditApi.getRedditInstance("Otis Test");
	    User user = red.login("torchbot2017", "gibatad", "5WRAIQAXlszYGA", "r0UlUU295NTuZxQ6j3UTSnpj4u8");
	    Document doc = MongoFacade.getInstance().torch_threads.find(new Document("title", "[PI] In a radiated clock with St. Question 11, I actually take BISTUCLLY SENDERS, BHEA, OK. When it's simply my asshole when they probably work if its as really okay about today.")).first();
	    System.out.println(doc.getString("title"));
	    System.out.println(doc.getString("text"));
	    Link link = red.Submit(doc.getString("subreddit"), doc.getString("title"), doc.getString("text"), "self");
	    System.out.println(link.getUrl());
	    System.out.println("Thread posted and record updated!");
	}
	
}
