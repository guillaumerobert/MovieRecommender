/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import com.camillepradel.movierecommender.model.Movie;
import com.camillepradel.movierecommender.model.Rating;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.neo4j.driver.v1.*;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author sento
 */
public class NeoRequests implements DBInterface {
    
    private final Driver driver;
    private final AuthToken login;
    
    public NeoRequests() {
        
        login = AuthTokens.basic( "neo4j", "root" );
        driver = GraphDatabase.driver( "bolt://localhost", login );
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
        
        return ratings;
    }

    public Movie getMovieById(
        @RequestParam(value = "movie_id", required = true) int movieId) {
        
        Session session = driver.session();
        StatementResult result = session.run( "MATCH (m:Movie { id: " + movieId + " }) RETURN m.id, m.title" );
        
        Record record = result.next();
        int id = record.get("m.id").asInt();
        String titre = record.get("m.title").asString();
        
        session.close();
        
        return new Movie(id, titre);
    }

    public void setRating(@RequestParam(value = "rating", required = true) Rating rating) {
        
        Session session = driver.session();
        int time = (int) (new Date().getTime() / 1000);
        
        // on drop puis recrée la relation, qu elle existe ou non
        
        session.run( "MATCH (u:User {id:" + rating.getUserId() + "})-[r]->(m:Movie {id:" + rating.getMovieId() + "}) DELETE r" );
        session.run("MATCH (u:User {id:" + rating.getUserId() + "}), (m:Movie {id:" + rating.getMovieId() + "}) CREATE (u)-[:RATED{note:" + rating.getScore() + ", timestamp:" + time + "}]->(m)");
        
        session.close();
    }

    public List<Rating> ProcessRecommendation1(Integer user_id) {
        
        Session session = driver.session();
        List<Rating> ratings = new ArrayList<Rating>();
        
        StatementResult result = session.run("MATCH (target_user:User {id : " + user_id + "})-[:RATED]->(m:Movie)<-[:RATED]-(other_user:User)\n" +
                "WITH other_user, count(distinct m.title) AS num_common_movies, target_user\n" +
                "ORDER BY num_common_movies DESC\n" +
                "LIMIT 1\n" +
                "MATCH (other_user)-[rat_other_user:RATED]->(m2:Movie)\n" +
                "WHERE NOT ((target_user)-[:RATED]->(m2))\n" +
                "RETURN m2.id AS mov_id, m2.title AS rec_movie_title, rat_other_user.note AS rating, other_user.id AS watched_by\n" +
                "ORDER BY rat_other_user.note DESC");
        
        while ( result.hasNext() )
        {
            Record record = result.next();
            int idMovie = record.get("mov_id").asInt();
            String titre = record.get("rec_movie_title").asString();
            int note = record.get("rating").asInt();
            
            ratings.add(new Rating(new Movie(idMovie, titre), user_id, note));
        }    
        
        session.close();
        
        return ratings;
        
    }

    public List<Rating> ProcessRecommendation2(Integer user_id) {
        
        Session session = driver.session();
        List<Rating> ratings = new ArrayList<Rating>();
        
        StatementResult result = session.run("MATCH (target_user:User {id : " + user_id + "})-[:RATED]->(m:Movie)<-[:RATED]-(other_user:User)\n" +
                "WITH other_user, count(distinct m.title) AS num_common_movies, target_user\n" +
                "ORDER BY num_common_movies DESC\n" +
                "LIMIT 5\n" + // changement ici pour prendre les 5 users dans notre algo
                "MATCH (other_user)-[rat_other_user:RATED]->(m2:Movie)\n" +
                "WHERE NOT ((target_user)-[:RATED]->(m2))\n" +
                "RETURN m2.id AS mov_id, m2.title AS rec_movie_title, rat_other_user.note AS rating, other_user.id AS watched_by\n" +
                "ORDER BY rat_other_user.note DESC");
        
        while ( result.hasNext() )
        {
            Record record = result.next();
            int idMovie = record.get("mov_id").asInt();
            String titre = record.get("rec_movie_title").asString();
            int note = record.get("rating").asInt();
            
            ratings.add(new Rating(new Movie(idMovie, titre), user_id, note));
        }    
        
        session.close();
        
        return ratings;
        
    }

}
