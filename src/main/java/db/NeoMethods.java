/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import com.camillepradel.movierecommender.model.Movie;
import com.camillepradel.movierecommender.model.Rating;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.neo4j.driver.v1.*;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author sento
 */
public class NeoMethods implements DBInterface {
    
    private Driver driver;
    
    public NeoMethods() {
        
        driver = GraphDatabase.driver( "bolt://localhost", AuthTokens.basic( "neo4j", "root" ) );
    }
    
    public List<Movie> getMovies(
        @RequestParam(value = "user_id", required = false) Integer userId) {
        
        List<Movie> movies = new LinkedList<Movie>();        
        Session session = driver.session();
        StatementResult result = null;
        
        if (userId != null) {
            result = session.run( "MATCH (u:User {id:" + userId + "})-[r]->(m:Movie) WHERE type(r) = 'RATED' RETURN m.id, m.title" );
        } else {
            result = session.run( "MATCH (m:Movie)-[r]->(g:Genre) WHERE type(r) = 'CATEGORIZED_AS' RETURN m.id,m.title, collect(g.name) ORDER BY m.id" );
        }
        
        while ( result.hasNext() )
        {
            Record record = result.next();
            int id = record.get("m.id").asInt();
            String titre = record.get("m.title").asString();
            /*List<Object> genres = record.get("m.genres").asList();
            
            while (genres.iterator().hasNext()) {
                genres.iterator().next();
            }*/
            
            movies.add(new Movie(id, titre));
        }

        session.close();
        driver.close();
        
         return movies;
    }
    
    public List<Rating> getMoviesRatings(
        @RequestParam(value = "user_id") Integer userId) {
        
        List<Rating> ratings = new ArrayList<Rating>();
        
        Session session = driver.session();
        StatementResult r1 = null, r2 = null;
        
        r1 = session.run( "MATCH (m:Movie) WITH m.id as id, m.title as title RETURN id, title ORDER BY title" );
        
        while ( r1.hasNext() )
        {
            Record record1 = r1.next();
            int idMovieGlobal = record1.get("id").asInt();
            String titre = record1.get("title").asString();
            Boolean find = false;
            
            int idMovieUser = 0, note = 0;
            
            if (userId != null) {
                r2 = session.run( "MATCH (u:User {id: " + userId + "})-[r]->(m:Movie) WITH m.id as id, r.note as note WHERE type(r) = 'RATED' RETURN id, note" );
            }
            
            while ( r2.hasNext() && !find)
            {
                Record record2 = r2.next();
                idMovieUser = record2.get("id").asInt();
                
                if (idMovieGlobal == idMovieUser) {
                    note = record2.get("note").asInt();
                    find = true;
                }
            }
            
            if (find) {
                ratings.add(new Rating(new Movie(idMovieUser, titre), userId, note));
            } else {
                ratings.add(new Rating(new Movie(idMovieGlobal, titre), userId));
            }
        }

        session.close();
        driver.close();
        
        return ratings;
    }

    public Movie getMovieById(
        @RequestParam(value = "movie_id", required = true) int movieId) {
        
        Session session = driver.session();
        StatementResult result = session.run( "MATCH (m:Movie { id: " + movieId + " }) RETURN m.id, m.title" );
        
        Record record = result.next();
        int id = record.get("m.id").asInt();
        String titre = record.get("m.title").asString();
        
        return new Movie(id, titre);
    }

}
