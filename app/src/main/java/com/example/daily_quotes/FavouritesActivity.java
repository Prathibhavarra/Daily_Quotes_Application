package com.example.daily_quotes;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavouritesActivity extends AppCompatActivity {

    private ListView favoritesListView;
    private List<String> favoriteQuotes;
    private ArrayAdapter<String> adapter; // Add this line

    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_FAVORITES = "favoriteQuotes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        favoritesListView = findViewById(R.id.favorites_list_view);
        favoriteQuotes = loadFavoriteQuotes();

        String quote = getIntent().getStringExtra("quote");
        if (quote != null) {
            favoriteQuotes.add(quote);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, favoriteQuotes); // Modify this line
        favoritesListView.setAdapter(adapter);

        favoritesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                removeQuote(position);
            }
        });

        saveFavoriteQuotes();
    }

    private List<String> loadFavoriteQuotes() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        Set<String> quoteSet = sharedPreferences.getStringSet(KEY_FAVORITES, new HashSet<>());
        return new ArrayList<>(quoteSet);
    }

    private void removeQuote(int position) {
        favoriteQuotes.remove(position);
        adapter.notifyDataSetChanged();
        saveFavoriteQuotes();
        Toast.makeText(this, "Quote removed from favorites", Toast.LENGTH_SHORT).show();
    }

    private void saveFavoriteQuotes() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> quoteSet = new HashSet<>(favoriteQuotes);
        editor.putStringSet(KEY_FAVORITES, quoteSet);
        editor.apply();
    }
}
