package reddit.scripts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.bson.Document;

import reddit.mongo.MongoFacade;

public class LoadTorchText {
	
	private static final String fn = "samples_westworld.txt";
	
	public static void main(String[] args) throws IOException{
		
		MongoFacade mongo = MongoFacade.getInstance();
		BufferedReader r = new BufferedReader(new FileReader(fn));
		
		String line;
		int num = 1;
		while((line = r.readLine()) != null){
			try{
				Document doc = Document.parse(line);
				doc.append("posted", false);
				mongo.torch_threads.insertOne(doc);
			}
			catch(Exception e){
				System.out.println("Attemtping repair...");
				try{
					Document doc = Document.parse(line + "\"}");
					doc.append("posted", false);
					doc.append("fixed", true);
					mongo.torch_threads.insertOne(doc);
				}
				catch(Exception e2){
					System.out.println("Invalid JSON line: " + num);
				}
				
			}
			num++;
		}
		r.close();
	}

}
