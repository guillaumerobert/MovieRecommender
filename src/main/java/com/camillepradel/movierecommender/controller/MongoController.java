/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camillepradel.movierecommender.controller;

import com.camillepradel.movierecommender.model.Movie;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.util.LinkedList;
import java.util.List;
import org.bson.Document;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author sento
 */
public class MongoController {
    
    private MongoClient mongoClient;
    /*private MongoDatabase db;
    private MongoCollection movielens;*/
    private MongoDatabase db;
    private MongoCollection moviesCollection;
    
    public MongoController() {
        // Connection au serveur Mongo (écoute sur port 27017 par défaut, ne pas oublier de demarrer le serveur avant de déployer)
        mongoClient = new MongoClient( "localhost" , 27017 );
        db = mongoClient.getDatabase("MovieLens");
        moviesCollection = db.getCollection("movies");
        System.out.println("films dans la collection : " + moviesCollection.count());
    }
    
    public List<Movie> getMovies(
        @RequestParam(value = "user_id", required = false) Integer userId) {
                
        // TODO: Itérer dans la collection Mongo pour recuperer les films
        // Si le parametre user_id est fourni dans l'url , recuperer les films de cet utilisateur
        List<Movie> movies = new LinkedList<Movie>();
        MongoCursor<Document> cursor; 
        
        if (userId != null) {
           /* BasicDBObject searchQuery = new BasicDBObject();
            
            searchQuery.put("_id", userId);
            MongoCursor<Document> cursorUser = db.getCollection("users").find(searchQuery).iterator();
            Document user = cursorUser.next();*/
           
           /* TODO : iterer sur les movies id du user passé en param */
            cursor = moviesCollection.find().iterator();
            
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
}
