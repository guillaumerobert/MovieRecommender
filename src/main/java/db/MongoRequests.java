package db;

import com.camillepradel.movierecommender.model.Movie;
import com.camillepradel.movierecommender.model.Rating;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author sento
 */
public class MongoRequests implements DBInterface {
    
    private MongoClient mongoClient;
    /*private MongoDatabase db;
    private MongoCollection movielens;*/
    private MongoDatabase db;
    private MongoCollection moviesCollection,usersCollection;
    
    public MongoRequests() {
        // Connection au serveur Mongo (écoute sur port 27017 par défaut, ne pas oublier de demarrer le serveur avant de déployer)
        mongoClient = new MongoClient( "localhost" , 27017 );
        db = mongoClient.getDatabase("MovieLens");
        moviesCollection = db.getCollection("movies");
        usersCollection = db.getCollection("users");
        System.out.println("films dans la collection : " + moviesCollection.count());
    }
    
    public List<Movie> getMovies(
        @RequestParam(value = "user_id", required = false) Integer userId) {
                List<Movie> movies = new LinkedList<Movie>();
        MongoCursor<Document> cursor; 
        
        if (userId != null) {
            
                Bson userFilter = Filters.eq("_id",userId);
                cursor =  usersCollection.find(userFilter).iterator();
                Document user = cursor.next();
                
                List<Document> userMovies = (List<Document>) user.get("movies");
                
                System.out.println("films dans la collection : " + userMovies.toString());
                
                List userMovieIds = new ArrayList<Integer>();
                
                for(Document d : userMovies ){
                    userMovieIds.add(d.get("movieid"));     
                }
                
                Bson movieFilter = Filters.in("_id",userMovieIds);
                cursor =  moviesCollection.find(movieFilter).iterator();
                
  
        } else {
            cursor = moviesCollection.find().iterator();
        }
        
        while (cursor.hasNext()) {
            Document o = cursor.next();
            Integer id = (Integer) o.get("_id") ; 
            String title = (String) o.get("title") ; 
            movies.add(new Movie(id, title));
        }
        return movies;
    }
    
    
     /**
     *
     * @param userId
     * @return
     */
    public List<Rating> getMoviesRatings(
        @RequestParam(value = "user_id") Integer userId) {
        
        if (userId != null) {
            MongoCursor<Document> cursor,cursorUser; 
            List<Rating> ratings = new ArrayList<Rating>();
           
            // Tous les films
            cursor = moviesCollection.find().iterator();

            // Films de l'user
            Bson userFilter = Filters.eq("_id",userId);
            cursorUser =  usersCollection.find(userFilter).iterator();
            Document user = cursorUser.next();
            List<Document> userMovies = (List<Document>) user.get("movies");

            while (cursor.hasNext()) {
                Document o = cursor.next();
                Integer idFilm = (Integer) o.get("_id") ; 
                String title = (String) o.get("title") ; 
                Integer note = 0;
                
                for(Document d : userMovies ){
                    if(d.get("movieid").equals(idFilm)){
                        note = (Integer) d.get("rating");
                    }  
                }
                ratings.add(new Rating(new Movie(idFilm, title), userId, note));
               // movies.add(new Movie(id, title));
            }
            return ratings;
        }else{
            return null;
        }       
    }
    
    public Movie getMovieById(@RequestParam(value = "movie_id", required = true) int movieId){
        MongoCursor<Document> cursor;
        Bson movieFilter = Filters.eq("_id",movieId);
        cursor =  moviesCollection.find(movieFilter).iterator();
        Document movie = cursor.next();
        String title = movie.getString("title");
        return new Movie(movieId,title);  
    }

    public void setRating(Rating rating) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Rating> ProcessRecommendation1(Integer user_id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Rating> ProcessRecommendation2(Integer user_id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}