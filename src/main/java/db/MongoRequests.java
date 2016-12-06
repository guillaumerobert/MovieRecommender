package db;

import com.camillepradel.movierecommender.model.Movie;
import com.camillepradel.movierecommender.model.Rating;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import java.util.HashMap;
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

    public void setRating(@RequestParam(value = "rating", required = true) Rating rating) {
        
        BasicDBObject query = new BasicDBObject();
        query.put( "_id", rating.getUserId() );

        BasicDBObject newRatedMovie = new BasicDBObject();
        newRatedMovie.put("movieid", rating.getMovieId());
        newRatedMovie.put("rating", rating.getScore());
        newRatedMovie.put("timestamp", System.currentTimeMillis());

        BasicDBObject update = new BasicDBObject();
        update.put("$push", new BasicDBObject("movies",newRatedMovie));

        usersCollection.updateOne(query, update);

    }       
        

    public List<Rating> ProcessRecommendation1(Integer user_id) {
        // On récupére la liste des ids de films notés par l'utilisateur cible 
        List<Integer> ratedMovies = new ArrayList<Integer>(); 
        for(Movie m : this.getMovies(user_id) ){
            ratedMovies.add(m.getId());
        }
  
        MongoCursor<Document> cursor;
        cursor =  usersCollection.find().iterator();
        
       int bestMatching = 0;
       int bestSimilarUser = 0;
        
        while (cursor.hasNext()) {
            int nbMatchedMovies = 0;
             
            Document o = cursor.next();
            Integer userSimId = (Integer) o.get("_id");
                        
            if(!userSimId.equals(user_id) ){
                List<Movie> userSimRatings = this.getMovies(userSimId);
                for(Movie m : userSimRatings){
                    if(ratedMovies.contains(m.getId())){
                        nbMatchedMovies++;
                    }
                }
                
                if( nbMatchedMovies >= bestMatching  ){
                    bestMatching = nbMatchedMovies;
                    bestSimilarUser = userSimId;
                }
            } 
        } 

        List<Rating> finalResult = new ArrayList<Rating>();
        List<Movie> ratingsBest = this.getMovies(bestSimilarUser);
        for(Movie m : ratingsBest ){
             if(!ratedMovies.contains(m.getId())){
                 finalResult.add(new Rating(m,0,0));
             }
         }
        
        return finalResult;
    }

    public List<Rating> ProcessRecommendation2(Integer user_id) {
         // On récupére la liste des ids de films notés par l'utilisateur cible 
        List<Integer> ratedMovies = new ArrayList<Integer>(); 
        for(Rating r : this.getMoviesRatings(user_id) ){
            ratedMovies.add(r.getMovieId());
        }
        
        List<Rating> ratingsReturn = new ArrayList<Rating>();
        
        // On va mapper le nombre de films en commum pour chaque utilisateur
        HashMap<Integer,Integer> ratingMap = new HashMap<Integer,Integer>();
        
        MongoCursor<Document> cursor =  usersCollection.find().iterator();
        
       //int bestMatching = 0;
        
        while (cursor.hasNext()) {
            int nbMatchedMovies = 0;
            int userSimId = (Integer)cursor.next().get("_id"); 
            
            if(userSimId != user_id ){
               List<Rating> userSimRatings = this.getMoviesRatings(userSimId);
               for(Rating r : userSimRatings){
                   if(ratedMovies.contains(r.getMovieId())){
                       nbMatchedMovies++;
                   }
               }
               ratingMap.put(userSimId, nbMatchedMovies); 
            } 
        }
        
        // TODO : Tri HashMap et retour des 5 premiers
        
        return null;
    }
    
}