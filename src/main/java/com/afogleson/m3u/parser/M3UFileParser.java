package com.afogleson.m3u.parser;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class M3UFileParser {
    private static M3UFileParser _instance;
    private final static String EXTINF_TAG = "#EXTINF:";
    private final static String EXTINF_TVG_NAME = "tvg-name=\"";
    private final static String EXTINF_TVG_TYPE = "tvg-type=\"";
    private final static String EXTINF_TVG_ID = "tvg-id=\"";
    private final static String EXTINF_TVG_LOGO = "tvg-logo=\"";
    private final static String EXTINF_TVG_EPGURL = "tvg-epgurl=\"";
    private final static String EXTINF_GROUP_TITLE = "group-title=\"";
    private final static String EXTINF_RADIO = "radio=\"";
    private final static String EXTINF_TAGS = "tags=\"";
    private final static String MOVIE_TYPE = "movies";

    private int iStartYear = 2020;
    private int iEndYear = 2021;
    private ArrayList<M3U_Entry> _entries;
    private M3U_Entry _lastEntry;

    private M3UFileParser() {

    }

    public static synchronized M3UFileParser getInstance() {
        if(_instance == null) {
            _instance = new M3UFileParser();
        }
        return  _instance;
    }

    public int parse(boolean isFile, String urlFilepath, String outputPath, String startYear, String endYear) throws IOException {
        List<M3U_Entry> entries = null;
        if(isFile) {
            File m3uFile = new File(urlFilepath);
            if(m3uFile.exists()) {
                //check to see if the file is a file of m3u or... is it a file of urls
                //A url file will start with #URLS
                String content = Files.readString(Paths.get(m3uFile.getAbsolutePath()));
                if(content.toLowerCase(Locale.ROOT).startsWith("#urls")) {
                    //since we start with urls we need to pass each url into the parse method
                    //so first get all the lines of the content.
                    String[] lines = content.split("\\r?\\n");
                    int count = 0;
                    for(String line : lines) {
                        if(!line.toLowerCase().startsWith("#urls")) {
                            count = count + parse(false, line, outputPath, startYear, endYear);
                        }
                    }
                    return count;
                }
                else{
                    entries = parseFile(m3uFile);
                }
            }
        }
        else {
            entries = parseUrl(urlFilepath);
        }
        if(startYear ==null || startYear.isBlank()) {
            startYear = "" + iStartYear;
        }
        if(endYear ==null || endYear.isBlank()) {
            endYear = "" + iEndYear;
        }
        if(entries != null && entries.size() > 0) {
            int start = Integer.valueOf(startYear);
            int end = Integer.valueOf(endYear);
            return handleEntries(entries, start, end, outputPath);
        }
        return 0;
    }

    public int handleEntries(List<M3U_Entry> entries, int startYear, int endYear, String storageDir) {
        int processed = 0;
        //now lets decide what to do
        M3UFileParser parser = M3UFileParser.getInstance();
        //now that we have the entries we can go ahead and create the directories.
        for(int i=startYear; i<=endYear; i++) {
            String year = "" + i;
            for (M3U_Entry entry : entries) {
                M3U_Entry e = entry;
                try {
                    if(handleEntry(e, storageDir, year)) {
                        processed++;
                    };
                }
                catch(IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return processed;
    }

    private boolean handleEntry(M3U_Entry entry, String storageBase, String year) throws IOException {
        String entryName = sanitizeEntry(entry.getNameNoTvG());
        if (entryName.contains("(" + year + ")")) {
            File base = new File(storageBase);
            if(entry.getTvgType().equals(MOVIE_TYPE)) {
                //handle movies.
                return createMovieFile(base, entry, entryName);
            }
            else {
                //handle TV Shows
                return createTvFile(base, entry, entryName);
            }
        }
        return false;
    }

    private boolean createTvFile(File base, M3U_Entry entry, String movieName) throws IOException {
        String seriesName = sanitizeEntry(entry.getGroupTitle());
        File store = new File(base, seriesName);
        String seasonEpisode = movieName.substring(entry.getGroupTitle().length()).replaceAll(" ", "" ).trim().toUpperCase();
        //parse out the season
        int index = seasonEpisode.indexOf("E");
        String season  = seasonEpisode.substring(0, index);
        String episode = seasonEpisode.substring(index);
        File seasonDir = new File(store, season);
        seasonDir.mkdirs();
        String episodeName = seriesName + "." + season + episode + ".strm";
        File episodeFile = new File(seasonDir, episodeName);
        if(!episodeFile.exists()) {
            Files.write(Paths.get(episodeFile.getAbsolutePath()), entry.getUrl().getBytes());
            return true;
        }
        return false;
    }


    private boolean createMovieFile(File base, M3U_Entry entry, String movieName) throws IOException {
        File store = new File(base, sanitizeEntry(entry.getGroupTitle()));
        File dir = new File(store, movieName);
        dir.mkdirs();
        File writeFile = new File(dir, movieName + ".strm");
        if(!writeFile.exists()) {
            Files.write(Paths.get(writeFile.getAbsolutePath()), entry.getUrl().getBytes());
            return true;
        }
        return false;
    }

    private String sanitizeEntry(String name) {
        String saneEntry1 = name.trim().replaceAll(",", "");
        saneEntry1 = saneEntry1.replaceAll(" ", "_");
        String saneEntry2 = saneEntry1.replaceAll("[^a-zA-Z0-9-_()&+'\\.]", "");
        String saneEntry = saneEntry2.replaceAll("_", " ");
        return saneEntry;
    }

    private List<M3U_Entry> parseFile(File m3uFile) {
        try {
            return parse(m3uFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private List<M3U_Entry> parseUrl(String urlString) {
        try {
            InputStream is = new URL(urlString).openStream();
            return parse(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    // Parse m3u file by reading content from file by filepath
    private ArrayList<M3U_Entry> parse(String filepath) throws IOException {
        return parse(new FileInputStream(filepath));
    }

    // Parse m3u file by reading from inputstream
    private ArrayList<M3U_Entry> parse(InputStream inputStream) throws IOException {
        _entries = new ArrayList<>();
        BufferedReader br = null;
        String line;

        try {
            br = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = br.readLine()) != null) {
                try {
                    parseLine(line);
                } catch (Exception e) {
                    e.printStackTrace();
                    _lastEntry = null;
                }
            }
        } catch (IOException rethrow) {
            rethrow.printStackTrace();
            throw rethrow;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ignored) {
                }
            }
        }
        return _entries;
    }

    // Parse one line of m3u
    private void parseLine(String line) {

        line = line.trim();
        if(line.startsWith("#EXTM3U")) {
        }
        // EXTINF line
        else if (line.startsWith(EXTINF_TAG)) {
            _lastEntry = parseExtInf(line);
        }
        // URL line (no comment, no empty line(trimmed))
        else if (!line.isEmpty() && !line.startsWith("#")) {
            if (_lastEntry == null) {
                _lastEntry = new M3U_Entry();
            }
            _lastEntry.setUrl(line);
            _entries.add(_lastEntry);
            _lastEntry = null;
        }
        // No useable data -> reset last EXTINF for next entry
        else {
            _lastEntry = null;
        }
    }

    private M3U_Entry parseExtInf(String line) {
        M3U_Entry curEntry = new M3U_Entry();
        StringBuilder buf = new StringBuilder(20);
        if (line.length() < EXTINF_TAG.length() + 1) {
            return curEntry;
        }

        // Strip tag
        line = line.substring(EXTINF_TAG.length());

        // Read seconds (may end with comma or whitespace)
        while (line.length() > 0) {
            char c = line.charAt(0);
            if (Character.isDigit(c) || c == '-' || c == '+') {
                buf.append(c);
                line = line.substring(1);
            } else {
                break;
            }
        }
        if (buf.length() == 0 || line.isEmpty()) {
            return curEntry;
        }
        curEntry.setSeconds(Integer.valueOf(buf.toString()));

        // tvg tags
        while (!line.isEmpty() && !line.startsWith(",")) {
            line = line.trim();
            if (line.startsWith(EXTINF_TVG_NAME) && line.length() > EXTINF_TVG_NAME.length()) {
                line = line.substring(EXTINF_TVG_NAME.length());
                int i = line.indexOf("\"");
                curEntry.setTvgName(line.substring(0, i));
                line = line.substring(i + 1);
            }
            if (line.startsWith(EXTINF_TVG_LOGO) && line.length() > EXTINF_TVG_LOGO.length()) {
                line = line.substring(EXTINF_TVG_LOGO.length());
                int i = line.indexOf("\"");
                curEntry.setTvgLogo(line.substring(0, i));
                line = line.substring(i + 1);
            }
            if (line.startsWith(EXTINF_TVG_EPGURL) && line.length() > EXTINF_TVG_EPGURL.length()) {
                line = line.substring(EXTINF_TVG_EPGURL.length());
                int i = line.indexOf("\"");
                curEntry.setTvgEpgUrl(line.substring(0, i));
                line = line.substring(i + 1);
            }
            if (line.startsWith(EXTINF_RADIO) && line.length() > EXTINF_RADIO.length()) {
                line = line.substring(EXTINF_RADIO.length());
                int i = line.indexOf("\"");
                curEntry.setIsRadio(Boolean.parseBoolean(line.substring(0, i)));
                line = line.substring(i + 1);
            }
            if (line.startsWith(EXTINF_GROUP_TITLE) && line.length() > EXTINF_GROUP_TITLE.length()) {
                line = line.substring(EXTINF_GROUP_TITLE.length());
                int i = line.indexOf("\"");
                curEntry.setGroupTitle(line.substring(0, i));
                line = line.substring(i + 1);
            }
            if (line.startsWith(EXTINF_TVG_ID) && line.length() > EXTINF_TVG_ID.length()) {
                line = line.substring(EXTINF_TVG_ID.length());
                int i = line.indexOf("\"");
                curEntry.setTvgId(line.substring(0, i));
                line = line.substring(i + 1);
            }
            if (line.startsWith(EXTINF_TAGS) && line.length() > EXTINF_TAGS.length()) {
                line = line.substring(EXTINF_TAGS.length());
                int i = line.indexOf("\"");
                curEntry.setTags(line.substring(0, i).split(","));
                line = line.substring(i + 1);
            }
            if (line.startsWith(EXTINF_TVG_TYPE) && line.length() > EXTINF_TVG_TYPE.length()) {
                line = line.substring(EXTINF_TVG_TYPE.length());
                int i = line.indexOf("\"");
                curEntry.setTvgType(line.substring(0, i));
                line = line.substring(i + 1);
            }
        }

        // Name
        line = line.trim();
        if (line.length() > 1 && line.startsWith(",")) {
            line = line.substring(1);
            line = line.trim();
            if (!line.isEmpty()) {
                curEntry.setName(line);
            }
        }
        return curEntry;
    }

//    public static class Examples {
//        public static List<M3U_Entry> example() {
//            try {
//                SimpleM3UParser simpleM3UParser = new SimpleM3UParser();
//                File moviesFolder = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_MOVIES);
//                return simpleM3UParser.parse(new File(moviesFolder, "streams.m3u").getAbsolutePath());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return new ArrayList<>();
//        }
//
//        public static List<M3U_Entry> exampleWithLogoRewrite() {
//            List<M3U_Entry> playlist = new ArrayList<>();
//            try {
//                SimpleM3UParser simpleM3UParser = new SimpleM3UParser();
//                File moviesFolder = new File(new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_MOVIES), "liveStreams");
//                File logosFolder = new File(moviesFolder, "Senderlogos");
//                File streams = new File(moviesFolder, "streams.m3u");
//                for (M3U_Entry entry : simpleM3UParser.parse(streams.getAbsolutePath())) {
//                    if (entry.getTvgLogo() != null) {
//                        String logo = new File(logosFolder, entry.getTvgLogo()).getAbsolutePath();
//                        entry.setTvgLogo(logo);
//                    }
//                    playlist.add(entry);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return playlist;
//        }
//
//    }
}
