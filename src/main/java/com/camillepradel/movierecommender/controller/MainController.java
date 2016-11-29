package com.camillepradel.movierecommender.controller;

import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.camillepradel.movierecommender.model.Genre;
import com.camillepradel.movierecommender.model.Movie;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.Arrays;

@Controller
public class MainController {
	String message = "Welcome to Spring MVC!";
 
	@RequestMapping("/hello")
	public ModelAndView showMessage(
		@RequestParam(value = "name", required = false, defaultValue = "World") String name) {
		System.out.println("in controller");
 
		ModelAndView mv = new ModelAndView("helloworld");
		mv.addObject("message", message);
		mv.addObject("name", name);
		return mv;
	}

	@RequestMapping("/movies")
	public ModelAndView showMovies(
			@RequestParam(value = "user_id", required = false) Integer userId) {
		System.out.println("show Movies of user " + userId);
                
                // Connection au serveur Mongo (écoute sur port 27017 par défaut, ne pas oublier de demarrer le serveur avant de déployer)
                MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

                MongoDatabase db = mongoClient.getDatabase("MongoDB");
                System.out.println("Connecté a la DB Mongo");

                MongoCollection movielens = db.getCollection("MovieLens");
                System.out.println("Collection MovieLens selectionnée");
                
                System.out.println("films dans la collection : "+movielens.count());
               
                // TODO: Itérer dans la collection Mongo pour recuperer les films
                // Si le parametre user_id est fourni dans l'url , recuperer les films de cet utilisateur
		List<Movie> movies = new LinkedList<Movie>();
		Genre genre0 = new Genre(0, "genre0");
		Genre genre1 = new Genre(1, "genre1");
		Genre genre2 = new Genre(2, "genre2");
		movies.add(new Movie(0, "Titre 0", Arrays.asList(new Genre[] {genre0, genre1})));
		movies.add(new Movie(1, "Titre 1", Arrays.asList(new Genre[] {genre0, genre2})));
		movies.add(new Movie(2, "Titre 2", Arrays.asList(new Genre[] {genre1})));
		movies.add(new Movie(3, "Titre 3", Arrays.asList(new Genre[] {genre0, genre1, genre2})));

		ModelAndView mv = new ModelAndView("movies");
		mv.addObject("userId", userId);
		mv.addObject("movies", movies);
		return mv;       		
	}
}
