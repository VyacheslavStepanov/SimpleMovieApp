package com.example.simplemovieapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirstFragment extends Fragment {
    MovieApiInterface apiInterface;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.movies);

        apiInterface = ApiClient.getClient().create(MovieApiInterface.class);
        apiInterface.getTopRatedMovies("46aeec1ac063fd0f85be8ba021e32593", "ru")
                .enqueue(new Callback<MovieResponse>() {
                             @Override
                             public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                                 MoviesAdapter adapter = new MoviesAdapter(response.body().results);
                                 recyclerView.setAdapter(adapter);
                                 recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
                                 MovieDatabase db = Room.databaseBuilder(recyclerView.getContext(),
                                         MovieDatabase.class, "database-name")
                                         .allowMainThreadQueries()
                                         .build();

                                 List<MovieDb> dbMovies = new ArrayList<>();

                                 Movie firstMovie = response.body().results.get(1);
                                 dbMovies.add(new MovieDb(0, firstMovie.title, firstMovie.overview));
                                 db.movieDao().insertAll(dbMovies);

                                 // Прорвеим что успешно сохранили данные
                                 Log.d("TEST_DB", db.movieDao().getAll().get(0).movieName);
                             }

                             @Override
                             public void onFailure(Call<MovieResponse> call, Throwable t) {
                                 Log.d("TEST", t.toString());
                             }
                         });

    }
}