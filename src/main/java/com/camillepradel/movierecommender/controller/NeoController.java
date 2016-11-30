/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camillepradel.movierecommender.controller;

import com.camillepradel.movierecommender.model.Movie;
import java.util.LinkedList;
import java.util.List;
import org.neo4j.driver.v1.*;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author sento
 */
public class NeoController {
    
    public NeoController() {
    }
    
    public List<Movie> getMovies(
        @RequestParam(value = "user_id", required = false) Integer userId) {
        
        List<Movie> movies = new LinkedList<Movie>();
        
        Driver driver = GraphDatabase.driver( "bolt://localhost", AuthTokens.basic( "neo4j", "root" ) );
        Session session = driver.session();
        StatementResult result = null;
        
        if (userId != null) {
            result = session.run( "MATCH (u:User {id:" + userId + "})-[r]->(m:Movie) WHERE type(r) = 'RATED' RETURN m.id, m.title" );
        } else {
            result = session.run( "MATCH (m:Movie) RETURN m.id, m.title" );
        }
        
        while ( result.hasNext() )
        {
            Record record = result.next();
            int id = record.get("m.id").asInt();
            String titre = record.get("m.title").asString();
            movies.add(new Movie(id, titre));
        }

        session.close();
        driver.close();
        
         return movies;
    }
    
}
