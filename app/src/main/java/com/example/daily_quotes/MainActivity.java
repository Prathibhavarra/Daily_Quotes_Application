package com.example.daily_quotes;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView quoteTextView;
    private Button nextButton;
    private Button favoriteButton;
    private Button viewFavoriteQuotesButton;
    private Button shareButton;
    private Button reminderButton;
    //private Button favoritesButton;

    private String[] quotes = {
            "The greatest glory in living lies not in never falling, but in rising every time we fall. - Nelson Mandela",
            "Success is not final, failure is not fatal: It is the courage to continue that counts. - Winston Churchill",
            "The only way to do great work is to love what you do. - Steve Jobs",
            "Believe you can and you're halfway there. - Theodore Roosevelt",
            "The future belongs to those who believe in the beauty of their dreams.- Eleanor Roosevelt",
            "The best time to plant a tree was 20 years ago. The second best time is now. - Chinese Proverb",
            "In the middle of every difficulty lies opportunity. - Albert Einstein",
            "Don't watch the clock; do what it does. Keep going. - Sam Levenson",
            "The only limit to our realization of tomorrow will be our doubts of today.- Franklin D. Roosevelt",
            "Your time is limited, don't waste it living someone else's life. - Steve Jobs",
            "The harder I work, the luckier I get. - Samuel Goldwyn",
            "Happiness is not something ready-made. It comes from your own actions. - Dalai Lama",
            "Success is walking from failure to failure with no loss of enthusiasm. - Winston Churchill",
            "Dream big and dare to fail.- Norman Vaughan",
            "It does not matter how slowly you go as long as you do not stop. - Confucius",
            "The only person you should try to be better than is the person you were yesterday. - Unknown",
            "Believe in yourself and all that you are. Know that there is something inside you that is greater than any obstacle. - Christian D. Larson",
            "The secret of getting ahead is getting started. - Mark Twain",
            "Don't be afraid to give up the good to go for the great. - John D. Rockefeller",
            "The future depends on what you do today. - Mahatma Gandhi."
    };

    private int currentQuoteIndex = 0;
    private List<String> favoriteQuotes = new ArrayList<>();

    private static final int PERMISSION_REQUEST_NOTIFICATION_POLICY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        quoteTextView = findViewById(R.id.quote_text_view);
        nextButton = findViewById(R.id.next_button);
        favoriteButton = findViewById(R.id.favorite_button);
        viewFavoriteQuotesButton = findViewById(R.id.view_favorite_quotes_button);
        shareButton = findViewById(R.id.share_button);
        reminderButton = findViewById(R.id.reminder_button);
        //favoritesButton = findViewById(R.id.favorites_button);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextQuote();
            }
        });

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavorite();
                showCurrentQuote();
             //   openFavoritesActivity();

            }
        });

        viewFavoriteQuotesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewFavoriteQuotesButton();
            }
        });


        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareQuote();
            }
        });

        reminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestNotificationPolicyPermission();
            }
        });


    }

    private void showCurrentQuote() {
        String currentQuote = quotes[currentQuoteIndex];
        quoteTextView.setText(currentQuote);

        // Check if the current quote is a favorite and update the button text accordingly
       /* if (favoriteQuotes.contains(currentQuote)) {
            favoriteButton.setText(getString(R.string.remove_favorite));
        } else {
            favoriteButton.setText(getString(R.string.add_favorite));
        }*/
    }

    private void showNextQuote() {
        currentQuoteIndex = (currentQuoteIndex + 1) % quotes.length;
        showCurrentQuote();
    }

    private void toggleFavorite() {
        String currentQuote = quotes[currentQuoteIndex];

        if (favoriteQuotes.contains(currentQuote)) {
            //favoriteQuotes.remove(currentQuote);
            Toast.makeText(this, "This quote is already added", Toast.LENGTH_SHORT).show();
           //favoriteButton.setText(getString(R.string.add_favorite));
        } else {
            favoriteQuotes.add(currentQuote);
            Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
            //favoriteButton.setText(getString(R.string.remove_favorite));
            addToFavoritesActivity(currentQuote);
        }
    }

    private void shareQuote() {
        String currentQuote = quotes[currentQuoteIndex];
        String shareText = "Check out this quote:\n\n" + currentQuote;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Share Quote"));
    }

    private void requestNotificationPolicyPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY}, PERMISSION_REQUEST_NOTIFICATION_POLICY);
            return; // Return early to avoid setting the alarm without the necessary permission
        }

        setReminder();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_NOTIFICATION_POLICY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, set the reminder
                setReminder();
            } else {
                // Permission is denied, show a message to the user or handle it accordingly
                Toast.makeText(this, "Permission not granted. Unable to set reminder.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setReminder() {
        // Set a reminder for the current quote
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, QuoteReminderReceiver.class);
        intent.putExtra("quote", quotes[currentQuoteIndex]);

        //if that particular time is already set ,then how to handle
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get the desired reminder time from the user
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // Set the reminder for the chosen time
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);

                // Check if the reminder time is in the past, if so, set it for the next day
                if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                }

                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                Toast.makeText(MainActivity.this, "Reminder set for " + hourOfDay + ":" + minute, Toast.LENGTH_SHORT).show();
            }
        }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), false);

        timePickerDialog.show();
    }


    private void addToFavoritesActivity(String quote) {
        Intent intent = new Intent(MainActivity.this, FavouritesActivity.class);
        intent.putExtra("quote", quote);
        startActivity(intent);
    }
    // Inside the MainActivity class

    public void ViewFavoriteQuotesButton() {
        // Start the FavouritesActivity to view favorite quotes
        Intent intent = new Intent(MainActivity.this, FavouritesActivity.class);
        startActivity(intent);
    }


    /*private void openFavoritesActivity() {
        Intent intent = new Intent(MainActivity.this, FavouritesActivity.class);
        startActivity(intent);
    }*/
}
