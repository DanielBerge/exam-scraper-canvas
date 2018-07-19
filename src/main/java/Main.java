import com.google.gson.*;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static final String ACCESS_TOKEN = "4sBneJ6YpxhRoZI4OcLjJ0bFi3QCgBH3WsAm3uFvZSNX18gyj2QAiWLLDmsUMyvZ";

    public static void main(String[] args) {
        //addFolderNamesToFile();
        //saveFileToJson();
        //saveLinksToFile();
    }

    /**
     * Legger til alle mappenavn i en .txt
     */
    public static void addFolderNamesToFile() {
        List<Course> emner = hentEmner();
        for(Course emne: emner) {
            EmneFilHenter emneFilHenter = new EmneFilHenter(ACCESS_TOKEN, emne.getId());
            emneFilHenter.foldersToFile(emneFilHenter.hentFolders());
        }
    }

    /**
     * Legger til alle emner med nedlastingslinker til eksamensoppgaver i en Json fil
     */
    public static void saveFileToJson() {
        List<Course> courses = hentEmner();
        List<FilToJson> filToJsons = new ArrayList<FilToJson>();
        for(Course course:courses) {
            String emnekode = course.getCourse_code();
            String navn = course.getName();

            List<String> links = getDonwloadLinks(course.getId());
            String[] linker = new String[links.size()];
            for (int i = 0; i <links.size() ; i++) {
                linker[i] = links.get(i);
            }
            FilToJson filToJson = new FilToJson(emnekode, linker, navn);
            if(!(linker.length == 0)) {
                filToJsons.add(filToJson);
                System.out.println(course.getCourse_code() + ": " + linker.length);
            }
        }
        JsonHandler jsonHandler = new JsonHandler("storage");
        jsonHandler.save("eksamensOppgaverJson.json", filToJsons);
    }

    /**
     * Legger til emne og linker til eksamensoppgaver i en .txt
     */
    public static void saveLinksToFile() {
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter("downloadLinks.txt", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        List<Course> courses = hentEmner();
        for(Course course:courses) {
            List<String> links = getDonwloadLinks(course.getId());
            for(String link: links) {
                printWriter.println(course.getCourse_code() + ": " + link);
            }
        }
        printWriter.close();
    }

    /**
     * Henter nedlastingslinker av eksamensoppgaver
     *
     * @param course_id
     * @return
     */
    public static List<String> getDonwloadLinks(String course_id) {
        EmneFilHenter filHenter = new EmneFilHenter(ACCESS_TOKEN, course_id);
        List<String> filurls = filHenter.hentEksamensPdfer();
        return filurls;
    }

    /**
     * Henter alle emner på UIB canvas
     *
     * @return liste av Courses
     */
    public static List<Course> hentEmner() {
        CourseHenter henter = new CourseHenter(ACCESS_TOKEN);
        List<Course> courses = henter.getCourses();
        henter.close();
        return courses;
    }

    /**
     * Henter info fra JSON obj i URL
     *
     * @param urli
     * @param tClass Java Class pojo
     * @param <T>
     * @return Object
     */
    public static <T> List<T> hentArray(String urli, Class<T> tClass) {
        try {
            URL url = new URL(urli);
            URLConnection request = url.openConnection();
            request.connect();
            //  Convert to a JSON object to print data
            JsonParser jp = new JsonParser(); //from gson
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
            GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
            Gson gson = gsonBuilder.create();
            JsonArray rootobj = root.getAsJsonArray();

            List<T> objList = new ArrayList<T>();
            for (int i = 0; i <rootobj.size() ; i++) {
                T obj = gson.fromJson(rootobj.get(i).getAsJsonObject(), tClass);
                objList.add(obj);
            }
            return objList;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("ingen tilgang: " + urli);
        }
        return null;
    }
}
