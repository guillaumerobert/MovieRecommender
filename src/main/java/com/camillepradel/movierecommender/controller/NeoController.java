/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camillepradel.movierecommender.controller;

import com.camillepradel.movierecommender.model.Genre;
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
public class NeoController {
    
    private Driver driver;
    
    public NeoController() {
        
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
    
    /**
     *
     * @param userId
     * @return
     */
    public List<Rating> getMoviesRatings(
        @RequestParam(value = "user_id") Integer userId) {
        
        List<Rating> ratings = new ArrayList<Rating>();
        
        Session session = driver.session();
        StatementResult result = null;
        
        if (userId != null) {
            result = session.run( "MATCH (u:User {id: " + userId + "})-[r]->(m:Movie) WHERE type(r) = 'RATED' RETURN m.id, m.title,r.note ORDER BY r.note DESC" );
        }
        
        while ( result.hasNext() )
        {
            Record record = result.next();
            int id = record.get("m.id").asInt();
            String titre = record.get("m.title").asString();
            int note = record.get("r.note").asInt();
            
            ratings.add(new Rating(new Movie(id, titre), userId, note));
        }

        session.close();
        driver.close();
        
        return ratings;
    }
    
}
