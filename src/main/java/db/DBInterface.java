/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import com.camillepradel.movierecommender.model.Movie;
import com.camillepradel.movierecommender.model.Rating;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author sento
 */
public interface DBInterface {
    
     public List<Movie> getMovies(@RequestParam(value = "user_id", required = false) Integer userId);
     public List<Rating> getMoviesRatings(@RequestParam(value = "user_id") Integer userId);
     public Movie getMovieById(@RequestParam(value = "movie_id", required = true) int movieId);
     public void setRating(@RequestParam(value = "rating", required = true) Rating rating);
}
