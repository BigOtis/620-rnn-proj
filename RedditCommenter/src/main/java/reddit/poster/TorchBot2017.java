package reddit.poster;

import java.util.Random;

import org.bson.Document;

import ga.dryco.redditjerk.api.Reddit;
import ga.dryco.redditjerk.implementation.RedditApi;
import ga.dryco.redditjerk.wrappers.Link;
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
	    //User user = red.login("torchbot2017", "gibatad", "5WRAIQAXlszYGA", "r0UlUU295NTuZxQ6j3UTSnpj4u8");
	    User user = red.login("barrybonds10", "barrybonds10", "paM3qk8nzuPu7g", "cdZbFFM_tpnyeC3eL36KPmVcLMU");

	    
	    String title = thread.getString("title");
	    
	    if(title.length() >= 300){
	    	title = title.substring(0, 250);
	    }
	    String text = thread.getString("text");
	    if(text == null){
	    	return;
	    }
	    if(text.length() > 15000){
	    	text = text.substring(0, 14950);
	    }
	    String subreddit = thread.getString("subreddit");
	    if(subreddit.equals("tifu")){
	    	text += "TL:DR - and that is how it is what it is.";
	    	if(text.length() < 750){
	    		return;
	    	}
	    }
	    if(subreddit.equals("lifeofnorman")){
	    	System.out.println("Skipping life of norman for now...");
	    	return;
	    }
	    if(subreddit.equals("Poetry")){
	    	title += "[General]";
	    }

	    System.out.println("Torchbot posting new thread:\n"
	    		+ "\tSubreddit: " + subreddit + "\n"
	    		+ "\tTitle: " + title
	    );
	    
	    try{
	    	Link link = red.Submit(thread.getString("subreddit"), title, text, "self");
		    String url = link.getUrl();
		    thread.append("url", url);
	    }
	    catch(Exception e){
	    	System.err.println("Error posting thread...");
	    }
	    
	    thread.append("posted", true);
	    mongo.torch_threads.replaceOne(new Document("_id", thread.get("_id")), thread);
	    System.out.println("Thread posted and record updated!");
	}
	
}
