package filklasser;

public class FilToJson {
    private String navn;
    private String Emnekode;
    private String[] FilNavnUrl;


    public FilToJson(String emnekode, String[] filNavnUrl, String navn) {
        this.navn = navn;
        this.Emnekode = emnekode;
        this.FilNavnUrl = filNavnUrl;
    }

    public String getEmnekode() {
        return Emnekode;
    }

    public String getnavn() {
        return navn;
    }

    public String[] getFilNavnUrl() {
        return FilNavnUrl;
    }

    public void setEmnekode(String emnekode) {
        Emnekode = emnekode;
    }

    public void setFilNavnUrl(String[] filNavnUrl) {
        FilNavnUrl = filNavnUrl;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }
}

