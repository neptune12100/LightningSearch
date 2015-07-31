package fwslash.torrentsearch;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class SearchEngine {

    private static final Pattern SERIAL_PATTERN = Pattern.compile("(.*)\\|(.*)");
    private static final Pattern NEW_LINE = Pattern.compile("\\r\\n");
    private static final String SITES_FILE_NAME = "sites.txt";
    private String name, uriFormat;


    public SearchEngine() {
        this("", "");
    }

    public SearchEngine(String name, String uriFormat) {
        this.name = name;
        this.uriFormat = uriFormat;
    }

    /**
     * serial is a String of the form "Google|http://www.google.com/search?q=%s" where %s gets replaced with the query string
     *
     * @param serial Serialized form
     */

    public SearchEngine(CharSequence serial) {

        Matcher matcher = SERIAL_PATTERN.matcher(serial);
        if (matcher.matches()) {
            name = matcher.group(1);
            uriFormat = matcher.group(2);
        } else {
            Log.d("Invalid search engine!", String.format("\"%s\"", serial.toString()));
            name = "!!!Invalid!!!";
            uriFormat = "!!!Invalid!!!";
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUriFormat() {
        return uriFormat;
    }

    public void setUriFormat(String uriFormat) {
        this.uriFormat = uriFormat;
    }

    @Override
    public String toString() {
        return name;
    }

    public Uri getSearchUri(String query) {
        return Uri.parse(String.format(uriFormat, Uri.encode(query)));
    }

    public String serialize() {
        return String.format("%s|%s", name, uriFormat);
    }

    public static SearchEngine[] getDefaultSites(Context context) {
        CharSequence[] serialSites = context.getResources().getTextArray(R.array.sites);
        SearchEngine[] sites = new SearchEngine[serialSites.length];
        for (int i = 0; i < sites.length; i++) {
            sites[i] = new SearchEngine(serialSites[i]);
        }
        return sites;
    }

    public static boolean saveSites(SearchEngine[] sites, Context context) {
        File sitesFile = getSitesFile(context);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(sitesFile, false));
            for (SearchEngine site : sites) {
                writer.write(site.serialize());
                writer.newLine();
            }
            writer.close();
            return true;
        } catch (IOException e) {
            // I guess we're screwed.
            e.printStackTrace();
            return false;
        }
    }

    private static File getSitesFile(Context context) {
        return new File(context.getExternalFilesDir(null), SITES_FILE_NAME);
    }

    public static SearchEngine[] getSites(Context context) {
        /*
         * spends hour making custom search engines a thing
         * builds
         * fails
         * panics
         * realizes problem
         * adds semicolon
         */

        try {
            File sitesFile = getSitesFile(context);
            if (sitesFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(sitesFile));
                ArrayList<SearchEngine> sites = new ArrayList<>();
                String serialSite;
                do {
                    serialSite = reader.readLine();
                    if (serialSite != null) {
                        sites.add(new SearchEngine(serialSite));
                    }
                } while (serialSite != null);
                if (sites.isEmpty()) {
                    return getDefaultSites(context);
                }
                return sites.toArray(new SearchEngine[sites.size()]);
            } else {
                SearchEngine[] sites = getDefaultSites(context);
                saveSites(sites, context);
                return sites;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getDefaultSites(context);
    }
}
