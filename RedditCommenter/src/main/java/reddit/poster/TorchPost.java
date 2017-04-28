package reddit.poster;

import org.bson.Document;

import ga.dryco.redditjerk.api.Reddit;
import ga.dryco.redditjerk.implementation.RedditApi;
import ga.dryco.redditjerk.wrappers.Link;
import ga.dryco.redditjerk.wrappers.User;
import reddit.mongo.MongoFacade;

public class TorchPost {

	public static void main(String args[]){
		
		// API is broken for authentication ...
		Reddit red = RedditApi.getRedditInstance("Otis Test");
	    User user = red.login("torchbot2017", "gibatad", "5WRAIQAXlszYGA", "r0UlUU295NTuZxQ6j3UTSnpj4u8");
	    Document doc = MongoFacade.getInstance().torch_threads.find(new Document("title", "Norman Works Norman")).first();
	    System.out.println(doc.getString("text"));
	    Link link = red.Submit("lifeofnorman", "Norman Works Norman", doc.getString("text"), "self");
	    System.out.println(link.getUrl());
	    System.out.println("Thread posted and record updated!");
	}
	
}
