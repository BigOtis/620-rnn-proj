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

	public Link getRedditLink() {
		return redditLink;
	}

	public void setRedditLink(Link redditLink) {
		this.redditLink = redditLink;
	}

	public Document getThreadDoc() {
		return threadDoc;
	}

	public void setThreadDoc(Document threadDoc) {
		this.threadDoc = threadDoc;
	}
	
}
