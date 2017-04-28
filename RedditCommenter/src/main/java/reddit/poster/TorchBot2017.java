package reddit.poster;

import java.util.Random;

import org.bson.Document;

import ga.dryco.redditjerk.api.Reddit;
import ga.dryco.redditjerk.implementation.RedditApi;
import ga.dryco.redditjerk.wrappers.User;
import reddit.mongo.MongoFacade;

public class TorchBot2017 {

	public static void main(String args[]){
		
		MongoFacade mongo = MongoFacade.getInstance();
		int size =  (int) mongo.torch_threads.count();
		Random r = new Random();
		
		// get a random thread
		Document thread = mongo.torch_threads.find().limit(-1).skip(r.nextInt(size)).first();
		
		// API is broken for authentication ...
		Reddit red = RedditApi.getRedditInstance("Otis Test");
	    User user = red.login("torchbot2017", "gibatad", "5WRAIQAXlszYGA", "r0UlUU295NTuZxQ6j3UTSnpj4u8");
	    
	    System.out.println("Torchbot posting new thread:\n"
	    		+ "\tSubreddit: " + thread.getString("subreddit") + "\n"
	    		+ "\tTitle: " + thread.getString("title")
	    );
	    
	    
	}
	
}
