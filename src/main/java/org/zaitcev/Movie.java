package org.zaitcev;


import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

import java.util.Date;

public class Movie {
    private Long id;
    private String title;
    private String imageUrl;
    private Integer releaseDate;
    private String description;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Integer getReleaseDate() {
        return releaseDate;
    }

    public String getDescription() {
        return description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setReleaseDate(Integer releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Movie() {
    }

    public Movie(String title) {
        this.title = title;
    }

    public Movie(Long id, String title){
        this.id = id;
        this.title = title;
    }

    public Movie(Long id, String title, String imageUrl, Integer releaseDate, String description) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.releaseDate = releaseDate;
        this.description = description;
    }

    private static Movie from(Row row) {
        return new Movie(
                row.getLong("id"),
                row.getString("title"),
                row.getString("img"),
                row.getInteger("rdate"),
                row.getString("summary"));
    }

    public static Multi<Movie> findAll(MySQLPool client) {
        return client.query("SELECT id, title, img, rdate, summary FROM movies ORDER BY title ASC")
                .execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(Movie::from);
    }

    public Uni<Long> saveMovie(MySQLPool client) {
        return client
                .preparedQuery("INSERT INTO movies (title) VALUE (?)")
                .execute(Tuple.of(title))
                .onItem().transform(RowSet -> RowSet.iterator().next().getLong("id"));
    }
}
