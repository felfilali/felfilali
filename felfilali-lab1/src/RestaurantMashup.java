import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class RestaurantMashup {

        static String filepath = "keys.json";

        public static void main(String[] args) {

                ArrayList<Truple> restaurantMap = new ArrayList<Truple>();
                JSONParser parser = new JSONParser();
                String city;


                try {
                        JSONObject o = (JSONObject) parser.parse( new FileReader(filepath));
                        city = (String) o .get("city");
                        System.out.println(city);
                        YelptoTweetAPI yap = new YelptoTweetAPI(o);
                        restaurantMap = yap.query(city);

                } catch (IOException e) {
                        System.out.println("IO");
                } catch (ParseException e) {
                        System.out.println("Parse");
                }

                for (Truple key : restaurantMap){
                        System.out.println(key.name);
                        System.out.println("   Rating : " + key.rating);
                        System.out.println("   What peaople are saying:");
                        for (String s : key.tweets)
                                System.out.println("       " + s);
                        if (key.tweets.isEmpty())
                                System.out.println("       No tweets for this restaurant :/");

                }

        }
}
