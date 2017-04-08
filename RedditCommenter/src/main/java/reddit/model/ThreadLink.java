package reddit.model;

import org.bson.Document;

import ga.dryco.redditjerk.wrappers.Link;

public class ThreadLink {

	// Link to the thread on reddit
	private Link redditLink;
	
	// Our own internal mongodb representation of the thread
	private Document threadDoc;
	
	public ThreadLink(Link redditLink, Document threadDoc){
		this.redditLink = redditLink;
		this.threadDoc = threadDoc;
	}
	
//	Document threadDoc = new Document()
//	.append("id", threadID)
//	.append("title", threadTitle)
//	.append("subreddit", subreddit)
//	.append("text", threadText)
//	.append("url", threadLinkURL)
//	.append("author", threadAuthor)
//	.append("createDate", threadCreated)
//	.append("score", threadScore)
//	.append("numComments", numComments);	
	
	public String getID(){
		return threadDoc.getString("id");
	}
	
	public String getTitle(){
		return threadDoc.getString("title");
	}
	
	public String getSubreddit(){
		return threadDoc.getString("subreddit");		
	}
	
	public String getText(){
		return threadDoc.getString("text");
	}
	
	public String getURL(){
		return threadDoc.getString("url");
	}
	
	public String getAuthor(){
		return threadDoc.getString("author");
	}
			

	public Link getRedditLink() {
		return redditLink;
	}

	public Document getThreadDoc() {
		return threadDoc;
	}
	
	public String toString(){
		return threadDoc.toJson();
	}
	

}
