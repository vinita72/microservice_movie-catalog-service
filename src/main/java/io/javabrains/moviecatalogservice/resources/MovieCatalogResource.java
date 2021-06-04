package io.javabrains.moviecatalogservice.resources;

import com.netflix.discovery.DiscoveryClient;
import io.javabrains.moviecatalogservice.models.CatalogItem;
import io.javabrains.moviecatalogservice.models.Movie;
import io.javabrains.moviecatalogservice.models.Rating;
import io.javabrains.moviecatalogservice.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    @Autowired
    private RestTemplate restTemplate;

     /*  @Autowired
     private DiscoveryClient discoveryClient; */

//    @Autowired
  //  private WebClient.Builder webClientBuilder;

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId){

        // we can use this web builder to call the api's as rest templete. Need to import springwebflux in pom.xml
        // WebClient.Builder builder  = WebClient.builder();

        // RestTemplate restTemplate = new RestTemplate();

        //get all the rated movie id
        UserRating userrating = restTemplate.getForObject("http://ratings-data-service/ratingsdata/user/" +userId, UserRating.class);

                /*Arrays.asList(
                new Rating("1234",4),
                new Rating("5678",3)
        );*/

        return userrating.getRatings().stream().map(rating -> {

            //for each movie id , ca    ll movie info service and get details
            Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);

            //put them all together
            return new CatalogItem(movie.getName(), movie.getDescription(), rating.getRating());
        })
             .collect(Collectors.toList());
    }
}
     /*
            Alternative WebClient way
            Movie movie = webClientBuilder.build().get().uri("http://localhost:8082/movies/"+ rating.getMovieId())
            .retrieve().bodyToMono(Movie.class).block();
            */