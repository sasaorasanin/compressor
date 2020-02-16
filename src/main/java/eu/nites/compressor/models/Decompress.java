package eu.nites.compressor.models;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Decompress {
    private MultipartFile file;
    private Float decodeNumber;
    private int charsCount = 0;
    private Map<Integer, Map<Character, Float>> probabilities = new HashMap<>();
    private Map<Integer, Map<Character, Float[]>> rangeValues = new HashMap<>();
    private Float[] range = new Float[]{(float) 0, (float) 1};
    private String code;
    private String filename;
    private String outputString = "";

    public Decompress (MultipartFile file) throws IOException {
        this.file = file;
        this.fileToStringArray(this.file);
        this.run();
        this.makeFile();
    }

    public boolean checkHash () throws IOException {
        return Hash.check(this.filename, this.code);
    }

    public String getLink () {
        return this.filename;
    }

    private void run () {
        for(int i = 0; i < this.charsCount; i++) {
            if (i == 0) {
                this.chooseRange();
            } else {
                this.makeBetweenRangeValues();
            }
        }
    }

    private void chooseRange () {
        for (int i = 0; i < this.rangeValues.size(); i++) {
            for (char probability:this.rangeValues.get(i).keySet()) {
                if (this.decodeNumber > this.rangeValues.get(i).get(probability)[0] && this.decodeNumber <= this.rangeValues.get(i).get(probability)[1]) { // umesto for-a this.rangeValues.get(i).get(character)
                    this.range = new Float[]{this.rangeValues.get(i).get(probability)[0], this.rangeValues.get(i).get(probability)[1]};
                    this.outputString += probability;
                    System.out.println(probability + "==" + this.rangeValues.get(i).get(probability)[0] +"-"+ this.rangeValues.get(i).get(probability)[1]);
                }
            }
        }
    }

    // Write new values between ranges and calculate it with probabilities, for range sort by value asc

    private void makeBetweenRangeValues () {
        this.rangeValues = new HashMap<>();
        Float prev = this.range[0];

        for (int i = 0; i < this.probabilities.size(); i++) {
            char pro_key = this.probabilities.get(i).keySet().toArray()[0].toString().charAt(0);
            Float probability = this.probabilities.get(i).get(pro_key);
            Float new_upper_limit = this.range[0] + ( ( this.range[1] - this.range[0] ) * probability );
            System.out.println("char(run): "+pro_key+" | char(probability): "+this.probabilities.get(i).keySet().toArray()[0]+" | prev: "+prev+" | new_upper_limit: "+new_upper_limit);

            Map<Character, Float[]> range = new HashMap<>();
            Float[] arr = new Float[] {prev, new_upper_limit};
            range.put(pro_key, arr);
            this.rangeValues.put(i, range);
            prev = new_upper_limit;
        }

        this.chooseRange();

    }

    private void makeFile () throws IOException {
        long time = new Date().getTime() / 1000;
        String filename = "decompress" + time + ".txt";
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("./src/main/resources/static/storage/" + filename), "utf-8"))) {
            writer.write(this.outputString);
            this.filename = filename;
        }
    }

    private void fileToStringArray (MultipartFile file) throws IOException {
        String[] fileAsStringArray = new String(file.getBytes(), "UTF-8").split("##");
        if (fileAsStringArray.length == 4) {
            this.code = fileAsStringArray[0];
            this.decodeNumber = Float.parseFloat(fileAsStringArray[1]);
            this.charsCount = Integer.parseInt(fileAsStringArray[2]);
            this.makeProbabilities(fileAsStringArray[3]);
        } else {
            // throw IOError;
        }
    }

    private void makeProbabilities (String map) {
        String[] firstMap = map.split("<=");
        for(int i = 0; i < firstMap.length; i++) {
            Float prev = (float) 0;
            if (i > 0) {
                for (char p:this.probabilities.get(i-1).keySet()) {
                    prev = this.probabilities.get(i-1).get(p);
                }
            }
            String[] o = firstMap[i].split("=>");
            int j = Integer.parseInt(o[0]);
            String[] k = o[1].split("<>");
            System.out.println(o[1]);
            Map<Character, Float> value = new HashMap<>();
            Map<Character, Float[]> range = new HashMap<>();
            System.out.println(k[0]);
            System.out.println(k[1]);
            value.put(k[0].charAt(0), Float.parseFloat(k[1]));
            Float[] arr = new Float[] {prev, Float.parseFloat(k[1])};
            range.put(k[0].charAt(0), arr);
            this.probabilities.put(j, value);
            this.rangeValues.put(i, range);
        }
    }

}
