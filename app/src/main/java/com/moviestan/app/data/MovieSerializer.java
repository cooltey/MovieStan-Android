package com.moviestan.app.data;


import com.moviestan.app.util.JSONGetter;
import com.moviestan.app.util.LogFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;


public class MovieSerializer implements Serializable{
    public String poster_path;
    public String adult;
    public String overview;
    public String release_date;
    public ArrayList<Integer> genre_ids = new ArrayList<>();
    public String id;
    public String original_title;
    public String original_language;
    public String title;
    public String backdrop_path;
    public String popularity;
    public String vote_count;
    public String video;
    public String vote_average;

//    poster_path: "/45Y1G5FEgttPAwjTYic6czC9xCn.jpg",
//    adult: false,
//    overview: "In the near future, a weary Logan cares for an ailing Professor X in a hide out on the Mexican border. But Logan's attempts to hide from the world and his legacy are up-ended when a young mutant arrives, being pursued by dark forces.",
//    release_date: "2017-02-28",
//    genre_ids: [
//            28,
//            18,
//            878
//            ],
//    id: 263115,
//    original_title: "Logan",
//    original_language: "en",
//    title: "Logan",
//    backdrop_path: "/5pAGnkFYSsFJ99ZxDIYnhQbQFXs.jpg",
//    popularity: 172.393189,
//    vote_count: 1234,
//    video: false,
//    vote_average: 7.7
    public static MovieSerializer fromJSON(String json) {
        MovieSerializer data = null;
        try {
            JSONObject userObject = new JSONObject(json);
            data = new MovieSerializer();
            data.poster_path 		= "http://image.tmdb.org/t/p/w500/" + JSONGetter.get(userObject, "poster_path");
            data.adult 		        = JSONGetter.get(userObject, "adult");
            data.overview 		    = JSONGetter.get(userObject, "overview");
            data.release_date 		= JSONGetter.get(userObject, "release_date");

            // get json array
            JSONArray ids     = new JSONArray(JSONGetter.get(userObject, "genre_ids"));
            for(int i = 0 ; i < ids.length(); i++){
                data.genre_ids.add(ids.getInt(i));
            }

            data.id 		        = JSONGetter.get(userObject, "id");
            data.original_title 	= JSONGetter.get(userObject, "original_title");
            data.original_language 	= JSONGetter.get(userObject, "original_language");
            data.title 		        = JSONGetter.get(userObject, "title");
            data.backdrop_path 		= JSONGetter.get(userObject, "backdrop_path");
            data.popularity 		= JSONGetter.get(userObject, "popularity");
            data.vote_count 		= JSONGetter.get(userObject, "vote_count");
            data.video 		        = JSONGetter.get(userObject, "video");
            data.vote_average 		= JSONGetter.get(userObject, "vote_average");

        } catch (Exception e) {
            LogFactory.set("MovieSerializer Error", e);
        }
        return data;
    }
}
