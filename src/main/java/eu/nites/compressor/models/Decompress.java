package eu.nites.compressor.models;

import org.springframework.web.multipart.MultipartFile;

public class Decompress extends Compress {
    public Decompress (MultipartFile file) {
        super(file, "");
    }
}
