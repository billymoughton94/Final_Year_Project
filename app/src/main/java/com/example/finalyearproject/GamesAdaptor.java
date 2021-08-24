package com.example.finalyearproject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/*
Class derived from the following sources:

[32]"How to Filter a RecyclerView with SearchView - Android Studio Tutorial",
Youtube, 2018. [Online].
Available: https://youtu.be/sJ-Z9G0SDhc. [Accessed: 03- May- 2021].

[33]"RecyclerView + JSON Parsing - Part 3 - CREATING THE ADAPTER - Android Studio Tutorial",
Youtube.com, 2017. [Online].
Available: https://www.youtube.com/watch?v=mMzT4fSHU-8&list=PLrnPJCHvNZuBCiCxN8JPFI57Zhr5SusRL. [Accessed: 03- May- 2021].
 */

// class used for displaying video game titles in the GamesBrowser Activity
public class GamesAdaptor extends RecyclerView.Adapter<GamesAdaptor.GamesViewHolder> implements Filterable {

    private Context ctx;
    private List<Game> gamesList;
    private List<Game> fullGamesList;
    private User user;

    public GamesAdaptor(Context ctx, List<Game> gamesList, User user) {
        this.ctx = ctx;
        this.gamesList = gamesList;
        fullGamesList = new ArrayList<>(gamesList);
        this.user = user;
    }

    @NonNull
    @Override
    public GamesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.game_list_layout, parent, false);
        return new GamesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GamesViewHolder holder, int position) {
        Game game = gamesList.get(position);
        Glide.with(ctx)
            .load(game.getBox_art())
            .into(holder.gameImage);
    }

    @Override
    public int getItemCount() {
        return gamesList.size();
    }

    @Override
    public Filter getFilter() {
        return gameFilter;
    }

    private Filter gameFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Game> filteredGames = new ArrayList<>();

            if(charSequence == null || charSequence.length() == 0){
                filteredGames.addAll(fullGamesList);
            }
            else{
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for(Game game : fullGamesList){
                    if(game.getName().toLowerCase().contains(filterPattern)){
                        filteredGames.add(game);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredGames;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            gamesList.clear();
            gamesList.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    // FOR IMPLEMENTING THE ACTUAL VIEWS
    class GamesViewHolder extends ViewHolder implements View.OnClickListener {
        ImageView gameImage;

        public GamesViewHolder(@NonNull View itemView) {
            super(itemView);
            gameImage = itemView.findViewById(R.id.gameImageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Game game = gamesList.get(getAdapterPosition());
            Intent intent = new Intent(view.getContext(), GameDetails.class);
            intent.putExtra("game", game);
            intent.putExtra("user", user);

            Log.d("USER CHECK", user.getUsername());

            ctx.startActivity(intent);
        }
    }
}
