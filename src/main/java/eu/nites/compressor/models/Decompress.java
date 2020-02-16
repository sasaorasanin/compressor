package eu.nites.compressor.models;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Decompress {
    private MultipartFile file;
    private Float decodeNumber;
    private Map<Integer, Map<Character, Float>> probabilities = new HashMap<>();
    private String probabilitiesS;
    private Map<Integer, Map<Character, Float[]>> rangeValues = new HashMap<>();
    private String code;
    private String filename;

    public Decompress (MultipartFile file) throws IOException {
        this.file = file;
        this.fileToStringArray(this.file);
        this.makeFile();
    }

    public String getLink () {
        return this.filename;
    }

    private void makeFile () throws IOException {
        long time = new Date().getTime() / 1000;
        String filename = "decompress" + time + ".txt";
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("./src/main/resources/static/storage/" + filename), "utf-8"))) {
            writer.write( this.code + "#" + this.probabilities.toString());

            this.filename = filename;
        }
    }

    private void fileToStringArray (MultipartFile file) throws IOException {
        String[] fileAsStringArray = new String(file.getBytes(), "UTF-8").split("##");
        if (fileAsStringArray.length == 3) {
            this.code = fileAsStringArray[0];
            this.probabilitiesS = fileAsStringArray[1];
            this.decodeNumber = Float.parseFloat(fileAsStringArray[2]);
        } else {
            // throw IOError;
        }
    }
}
