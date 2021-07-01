package org.zaitcev;


import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("movies")
public class MovieResource {

    @Inject
    MySQLPool client;

    @PostConstruct
    void config() {
        initdb();
    }

    @GET
    public Multi<Movie> get(){
        return Movie.findAll(client);
    }

    @POST
    public Uni<Response> create(Movie movie) {
        return movie.saveMovie(client)
                .onItem().transform(id -> URI.create("/fruits/" + id))
                .onItem().transform(uri -> Response.created(uri).build());
    }

    private void initdb() {
        client.query("DROP TABLE IF EXISTS movies").execute()
                .flatMap(m-> client.query("CREATE TABLE movies (id SERIAL PRIMARY KEY, "+
                        "title TEXT NOT NULL, img TEXT, rdate SMALLINT, summary TEXT)").execute())
                .flatMap(m-> client.query("INSERT INTO movies (title, img, rdate, summary) " +
                        "VALUE('The Avengers', 'https://static.tvtropes.org/pmwiki/pub/images/100420.jpg' , 2012, 'A thing')").execute())
                .flatMap(m-> client.query("INSERT INTO movies (title, img, rdate, summary) " +
                        "VALUE('Star Wars', 'a', 1977, 'Cool')").execute())
                .flatMap(m-> client.query("INSERT INTO movies (title, img, rdate, summary) " +
                        "VALUE('The Lord of the Rings', 'b', 2001, 'Frodo')").execute())
                .await().indefinitely();
    }

}
