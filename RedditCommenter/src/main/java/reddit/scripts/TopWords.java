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

public class TopWords {
	
	static boolean includeComments = true;
	
	public static void main(String[] args) throws FileNotFoundException{
		
		Set<String> stopWords = loadStopWords();
		
		MongoFacade mongo = MongoFacade.getInstance();
		Map<String, Map<String, Integer>> srcount = new HashMap<>();
		
		for(Document doc : mongo.threads.find()){
			
			String threadId = doc.getString("id");
			String title = doc.getString("title");
			String text = doc.getString("text");
			String subreddit = doc.getString("subreddit");
			
			Map<String, Integer> countMap;
			if(srcount.containsKey(subreddit)){
				countMap = srcount.get(subreddit);
			}
			else{
				System.out.println("Adding countmap for subreddit: " + subreddit);
				countMap = new HashMap<>();
				srcount.put(subreddit, countMap);
			}
			
			String fullText = (title + " " + text).replaceAll("[^A-Za-z0-9]+", " ").toLowerCase().replaceAll("\\s+", " ");
			if(includeComments){
				FindIterable<Document> commentDocs = mongo.comments.find(new Document().append("threadId", threadId));
				for(Document comment : commentDocs){
					String ctext = comment.getString("text");
					if(ctext != null){
						fullText += " " + ctext.replaceAll("[^A-Za-z0-9]+", " ").toLowerCase().replaceAll("\\s+", " ");
					}
				}
			}
		
			String[] words = fullText.split(" ");
			for(String word : words){
				if(!stopWords.contains(word) && word.length() > 2){
					if(countMap.containsKey(word)){
						countMap.put(word, countMap.get(word)+1);
					}
					else{
						countMap.put(word, 1);
					}
				}
			}
		}
		
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
	
	public static Set<String>  loadStopWords(){
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
