import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

public class YelptoTweetAPI {

	private final JSONObject o;
	OAuthService yservice;
	Token yt;
	OAuthService tservice;
	Token tt;
	
	public YelptoTweetAPI(JSONObject o) {
		this.o = o;
		this.yservice = new ServiceBuilder().provider(TwoStepOAuth.class).apiKey((String) this.o.get("yelpconsumerkey"))
	            .apiSecret((String) this.o.get("yelpconsumersecret")).build();
		this.yt = new Token((String) this.o.get("yelptoken"), (String) this.o.get("yelptokensecret"));
	
		this.tservice = new ServiceBuilder().provider(TwoStepOAuth.class).apiKey((String) this.o.get("twitterapikey"))
	            .apiSecret((String) this.o.get("twitterapisecret")).build();
		this.tt = new Token((String) this.o.get("twittertoken"), (String) this.o.get("twittertokensecret"));
	}

	
	public String searchRestByCity(String city){
		
	    OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
	    
	    request.addQuerystringParameter("term", "restaurant");
	    request.addQuerystringParameter("location", city);
	    request.addQuerystringParameter("limit", "10");
	    
	    //send request & get response 
	    System.out.println("Querying " + request.getCompleteUrl() + " ...");
	    this.yservice.signRequest(this.yt, request);
	    Response response = request.send();
	    return response.getBody();
	}

	
	public String tweetRestCity(String restaurant, String city){
		 OAuthRequest request = new OAuthRequest(Verb.GET, 
				 "https://api.twitter.com/1.1/search/tweets.json");
		 request.addQuerystringParameter("q", restaurant+" "+ city);

		 this.tservice.signRequest(this.tt, request);
		 Response response = request.send();
		 return response.getBody();
	}
	
	ArrayList<Truple> query(String city){
	    JSONParser parser = new JSONParser();
	    JSONObject yresponse = null;
	    JSONObject tresponse = null;
	    
	    ArrayList<Truple> mainList = 
	    		new ArrayList<Truple>();
	    
		// search & get response
	   try {
		yresponse = (JSONObject) parser.parse(searchRestByCity( city));
	   } catch (ParseException e) {
		   // TODO Auto-generated catch block
		   System.out.println("parser failed");
		   e.printStackTrace();
	   }
		
	   //extract restaurants names & get tweets
	   JSONArray restaurants = (JSONArray) yresponse.get("businesses");
	   String name;
	   Double rating;
	   System.out.println("There are "+ restaurants.size() + "\n");
	   for (Object r : restaurants){
		   name = ((JSONObject)r).get("name").toString();
		   rating = (Double) ((JSONObject)r).get("rating");
		  
		   try {
			   tresponse = (JSONObject) parser.parse(tweetRestCity(name , city));
			   JSONArray tweets = (JSONArray) tresponse.get("statuses");
			   ArrayList<String> peopleSaying = new ArrayList<String>();
			   for (Object t : tweets){
				   //System.out.println(((ArrayList) t).get("text"));
				   //System.out.println(((JSONObject)t).get("text"));
				   peopleSaying.add((String) ((JSONObject)t).get("text"));
			   }
			   mainList.add( new Truple (name, rating, peopleSaying));
		   } catch (ParseException e) {
				   System.out.println("tweet parser failed");
				   e.printStackTrace();
		   }
		   
	   }
	   return mainList;
	}
	   	
}
