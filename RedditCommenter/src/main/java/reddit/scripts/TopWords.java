package reddit.scripts;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.Document;

import com.mongodb.client.FindIterable;

import reddit.mongo.MongoFacade;

/**
 * Top words - Loops through every subreddit 
 * and keeps track of all the words used among
 * threads/comments. Outputs the top words used 
 * in each subreddit to a file
 * 
 * @author Phil Lopez - pgl5711@rit.edu
 *
 */
public class TopWords {
	
	static boolean includeComments = true;
	
	public static void main(String[] args) throws FileNotFoundException{
		
		Set<String> stopWords = loadStopWords();
		MongoFacade mongo = MongoFacade.getInstance();
		Map<String, Map<String, Integer>> srcount = new HashMap<>();
		HashMap<String, Integer> allMap = new HashMap<>();
		srcount.put("ALL_SUBREDDITS", allMap);
		
		// Loop through every thread doc
		for(Document doc : mongo.threads.find()){
			
			String threadId = doc.getString("id");
			String title = doc.getString("title");
			String text = doc.getString("text");
			String subreddit = doc.getString("subreddit");
			
			// create a map to keep track of word counts for this
			// subreddit if it doesn't exist, otherwise retrieve existing
			Map<String, Integer> countMap;
			if(srcount.containsKey(subreddit)){
				countMap = srcount.get(subreddit);
			}
			else{
				System.out.println("Adding countmap for subreddit: " + subreddit);
				countMap = new HashMap<>();
				srcount.put(subreddit, countMap);
			}
			
			// combine title/text and remove all non-numerical/alphabetical characters
			String fullText = (title + " " + text).replaceAll("[^A-Za-z0-9]+", " ").toLowerCase().replaceAll("\\s+", " ");
			
			// add words from comments on this thread 
			if(includeComments){
				FindIterable<Document> commentDocs = mongo.comments.find(new Document().append("threadId", threadId));
				for(Document comment : commentDocs){
					String ctext = comment.getString("text");
					if(ctext != null){
						fullText += " " + ctext.replaceAll("[^A-Za-z0-9]+", " ").toLowerCase().replaceAll("\\s+", " ");
					}
				}
			}
		
			// add all the words we found to the wordmap, if they aren't stop words
			String[] words = fullText.split(" ");
			for(String word : words){
				if(!stopWords.contains(word) && word.length() > 2){
					if(countMap.containsKey(word)){
						countMap.put(word, countMap.get(word)+1);
					}
					else{
						countMap.put(word, 1);
					}
					if(allMap.containsKey(word)){
						allMap.put(word, allMap.get(word)+1);
					}
					else{
						allMap.put(word, 1);
					}
				}
			}
		}
		
		// output the results to a file
		PrintWriter pw = new PrintWriter("topwords.txt");
		for(String sr : srcount.keySet()){
			final Map<String, Integer> countMap = srcount.get(sr);
			List<String> words = new ArrayList<>(countMap.keySet());
			Collections.sort(words, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					return countMap.get(o2).compareTo(countMap.get(o1));
				}
			});
			
			pw.println("-------------------------------------");
			pw.println("-- " + sr + " --");
			pw.println("-------------------------------------");

			for(int i = 0; i < 10; i++){
				pw.println("\t" + words.get(i) + " : " + countMap.get(words.get(i)));
			}
		}
		
		pw.flush();
		pw.close();
	}
	
	/**
	 * Loads in all of the stop words from a file
	 */
	private static Set<String>  loadStopWords(){
		Set<String> stopWords = new HashSet<>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader("stopwords.txt"));
			String line;
			while((line = reader.readLine()) != null){
				stopWords.add(line);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stopWords;
	}

}
