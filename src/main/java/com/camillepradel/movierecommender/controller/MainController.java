package com.camillepradel.movierecommender.controller;

import db.NeoRequests;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.camillepradel.movierecommender.model.Genre;
import com.camillepradel.movierecommender.model.Movie;
import com.camillepradel.movierecommender.model.Rating;
import db.MongoRequests;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MainController {
	String message = "Welcome to Spring MVC!";
        NeoRequests dbCtrl = new NeoRequests();
        //MongoMethods dbCtrl = new MongoMethods();
 
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
               
                List<Movie> movies = dbCtrl.getMovies(userId);
                
		/*Genre genre0 = new Genre(0, "genre0");
		Genre genre1 = new Genre(1, "genre1");
		Genre genre2 = new Genre(2, "genre2");
		movies.add(new Movie(0, "Titre 0", Arrays.asList(new Genre[] {genre0, genre1})));
		movies.add(new Movie(1, "Titre 1", Arrays.asList(new Genre[] {genre0, genre2})));
		movies.add(new Movie(2, "Titre 2", Arrays.asList(new Genre[] {genre1})));
		movies.add(new Movie(3, "Titre 3", Arrays.asList(new Genre[] {genre0, genre1, genre2})));*/

		ModelAndView mv = new ModelAndView("movies");
		mv.addObject("userId", userId);
		mv.addObject("movies", movies);
		return mv;       		
	}
        
        @RequestMapping(value = "/moviesratings", method = RequestMethod.GET)
	public ModelAndView showMoviesRatings(
			@RequestParam(value = "user_id", defaultValue = "1") Integer userId) {
		System.out.println("show movies ratings by user " + userId);
                
                List<Rating> ratings = dbCtrl.getMoviesRatings(userId);
		ModelAndView mv = new ModelAndView("moviesratings");
		mv.addObject("userId", userId);
		mv.addObject("ratings", ratings);
		return mv;	
	}
        
        @RequestMapping(value = "/moviesratings", method = RequestMethod.POST)
        public void saveOrUpdateRating(
			@RequestParam(value = "user_id") Integer userId, Integer movieId, Integer note) {
                System.out.println("update : Note " + userId + " - Movie " + movieId + " - Note " + note);
                
                Movie m = dbCtrl.getMovieById(movieId);
                if (m != null) {
                    Rating r = new Rating(m, userId, note);
                    dbCtrl.setRating(r);
                }
                
            
        }
}
