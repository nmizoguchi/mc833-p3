 package client;

import compute.Request;
import model.RentalStore;
import java.io.Serializable;
import model.Movie;
import java.util.*;

public class MovieListByGenreRequest implements Request<MovieListResponse>, Serializable {

    private String genre;
    private List<String> projection;

    public MovieListByGenreRequest(String genre, List<String> projection) {
        this.genre = genre;
        this.projection = projection;
    }

    public MovieListResponse execute(RentalStore store) {
        
        MovieListResponse response = new MovieListResponse();
        
        // start timing
        long t_start = System.nanoTime();

        response.movieList = store.getMovies(this.genre, this.projection);

        // stop timing
        long t_end = System.nanoTime();
        response.processingTime = t_end-t_start;
        return response;
    }
}
