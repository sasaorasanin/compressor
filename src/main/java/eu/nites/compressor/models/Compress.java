package eu.nites.compressor.models;

import org.springframework.web.multipart.MultipartFile;

public class Compress {
    private MultipartFile file;
    private String code;

    public Compress (MultipartFile file, String code) {
        this.file = file;
        this.code = code;
    }

    public String getLink () {
        return this.code;
    }
}
