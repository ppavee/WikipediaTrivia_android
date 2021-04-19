package com.example.wikitrivia;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.RemoteViews;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
//import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class TriviaService extends Service {
    String triviaDescription;
    Intent intent;

    public TriviaService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.intent = intent;

        RemoteViews views = new RemoteViews("com.example.wikitrivia", R.layout.wiki_widget);
        views.setTextViewText(R.id.tvTrivia, "Fetching new trivia...");
        AppWidgetManager.getInstance(getApplicationContext()).updateAppWidget(intent.getIntExtra("appWidgetId", 0), views);

        new GetTriviaAsync().execute();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public enum Months
    {
        January,
        February,
        March,
        April,
        May,
        June,
        July,
        August,
        September,
        October,
        November,
        December
    }

    public class GetTriviaAsync extends AsyncTask<Void, Void, String>
    {



        @Override
        protected String doInBackground(Void... voids)
        {
            try
            {
                Random random = new Random();
                int day = random.nextInt(27) + 1;
                String month = Months.values()[random.nextInt(12)].toString();
                int year = random.nextInt(17) + 2004;

               // URL url = new URL("https://en.wikipedia.org/wiki/Wikipedia:Recent_additions/"+ year + "/" + month + "#" + day + "_" + month + "_" + year);
               // HttpURLConnection conn = (HttpURLConnection) url.openConnection();
               // conn.setRequestMethod("GET");
                Document doc = Jsoup.connect("https://en.wikipedia.org/wiki/Wikipedia:Recent_additions/"+ year + "/" + month + "#" + day + "_" + month + "_" + year).get();
                ArrayList<String> liElements = new ArrayList<>();
                Elements liElems = doc.select("li");//.forEach(a -> liElements.add(a.toString()));
                for(Element elem : liElems)
                {
                    String text = elem.text();
                    if(text.startsWith("..."))
                        liElements.add(text);
                }

                /*BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                conn.disconnect();*/

                triviaDescription = liElements.get(random.nextInt(liElements.size()));
            }
            catch (Exception e)
            {
                
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s)
        {
            RemoteViews views = new RemoteViews("com.example.wikitrivia", R.layout.wiki_widget);
            views.setTextViewText(R.id.tvTrivia, triviaDescription);

            PendingIntent piSync = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.ivNewTrivia, piSync);

            AppWidgetManager.getInstance(getApplicationContext()).updateAppWidget(intent.getIntExtra("appWidgetId", 0), views);

        }
    }
}