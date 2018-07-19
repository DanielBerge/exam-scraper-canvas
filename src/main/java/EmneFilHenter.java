
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmneFilHenter {
    public String ACCESS_TOKEN;
    public String course_id;

    public EmneFilHenter(String ACCESS_TOKEN, String course_id) {
        this.ACCESS_TOKEN = ACCESS_TOKEN;
        this.course_id = course_id;
    }

    /**
     * Skriver ut en liste med alle folder navn til emnet
     * Også de som ikke har eksamensoppgaver, altså alle
     *
     * @param folders
     */
    public void foldersToFile(List<Folder> folders) {
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(new FileWriter("foldernames.txt", true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(folders == null) {
            System.out.println("Ingen folders");
            printWriter.close();
            return;
        }
        for (Folder folder : folders) {
            printWriter.println(folder.getName());
        }
        printWriter.close();
    }

    /**
     * Henter alle eksamensoppgaver til ett emne
     *
     * @return Liste med linker til alle filer
     */
    public List<String> hentEksamensPdfer() {
        List<String> urlList = new ArrayList<String>();
        try {
            List<Folder> folders = hentFolders();
            Pattern pattern = Pattern.compile(".+(ksam|xam|KSAM|XAM).*");
            for (Folder folder : folders) {
                Matcher matcher = pattern.matcher(folder.getFull_name());
                if (matcher.find()) {
                    for (String urli : hentFilerFraFolder(folder.getFiles_url())) {
                        urlList.add(urli);
                    }
                }
            }
        } catch (NullPointerException e) {
            System.out.println("Ingen tilgang");
        }
        return urlList;
    }

    /**
     * Henter alle filer fra en folderURL
     *
     * @param url url til folder
     * @return liste av URL linker til filer
     */
    public List<String> hentFilerFraFolder(String url) {
        List<String> urlList = new ArrayList<String>();
        url += "?per_page=100000&access_token=" + ACCESS_TOKEN;
        List<Fil> filer = Main.hentArray(url, Fil.class);
        for(Fil fil: filer) {
            if(fil.getFilename().contains(".pdf") || fil.getFilename().contains(".docx"))
                urlList.add(fil.getDisplay_name() + " - " + fil.getUrl());
        }
        return urlList;
    }

    /**
     * Henter alle mapper til et emne
     *
     * @return liste med strenger av mapper
     */
    public List<Folder> hentFolders() {
        List<Folder> folders = new ArrayList<Folder>();
        try {
            String url = "https://mitt.uib.no/api/v1/courses/" + course_id + "/folders?per_page=100000&access_token=" + ACCESS_TOKEN;
            folders = Main.hentArray(url, Folder.class);
        } catch (Exception e) {
            System.out.println("Ikke tilgang");
        }
        return folders;
    }

//    public void hentFiler() {
//        String url = "https://mitt.uib.no/api/v1/courses/" + course_id + "/files/folder/Eksamensoppgaver?access_token=" + ACCESS_TOKEN;
//        List<Fil> filer = hentArray(url, Fil.class);
//        for(Fil fil: filer) {
//            System.out.println(fil.getFilename());
//        }
//    }


}
