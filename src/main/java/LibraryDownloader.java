import filklasser.FilToJson;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LibraryDownloader {
    /**
     * Laster inn json filen med alle emner og linker
     *
     * @return Array av filklasser.FilToJson objekter
     */
    public FilToJson[] loadJson() {
        JsonHandler handler = new JsonHandler("storage");
        FilToJson[] files = handler.load("eksamensOppgaverJson.json", FilToJson[].class);
        return files;
    }

    /**
     * Lager directories til alle emnene i param
     * Kaller "downloadFiles()"
     * @param emner
     */
    public void createDirectories(FilToJson[] emner) {
        for (int i = 0; i < emner.length; i++) {
            String pathname = "Emner/" + emner[i].getnavn();
            File folder = new File(pathname);
            if(!folder.exists())
                folder.mkdirs();
            downloadFiles(pathname, emner[i]);
        }
    }

    /**
     * Laster ned alle filene til ett spesifikt emne
     *
     * @param pathname
     * @param emne
     */
    public void downloadFiles(String pathname, FilToJson emne) {
        for (int i = 0; i < emne.getFilNavnUrl().length; i++) {
            try {
                Pattern pattern = Pattern.compile("(.+\\.[pdf|docx]+) - (https://mitt.uib.no/files.+)");
                Matcher matcher = pattern.matcher(emne.getFilNavnUrl()[i]);
                if(matcher.find()) {
                    String urli = matcher.group(2);
                    URL url = new URL(urli);
                    String[] realPath = pathname.split(" /");
                    File file = new File(realPath[0] + "/" + matcher.group(1).replace(",", " "));
                    if (!file.exists()) {
                        System.out.println();
                        System.out.println("Downloading: " + matcher.group(2) + " " + matcher.group(1) + " from " + emne.getEmnekode() + " ...");
                        FileUtils.copyURLToFile(url, file);
                        System.out.println("Done");
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
