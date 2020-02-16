package eu.nites.compressor.models;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Compress {
    private MultipartFile file;
    private String fileAsString;
    private Map<Character, Integer> stringAsChars;
    private char[] chars;
    private Map<Integer, Map<Character, Float>> probabilities = new HashMap<>();
    private Map<Integer, Map<Character, Float[]>> rangeValues = new HashMap<>();
    private Float[] range = new Float[]{(float) 0, (float) 1};
    private String code;
    private String filename;

    public Compress (MultipartFile file, String code) throws IOException {
        this.file = file;
        this.code = code;
        this.fileToString(this.file);
        this.stringToChars(this.fileAsString);
        this.makeProbabilities();
        this.run();
        this.makeFile();
    }

    public String getLink () {
        return this.filename;
    }

    private void chooseRange (char character) {
        for (int i = 0; i < this.rangeValues.size(); i++) {
            System.out.println(this.rangeValues.toString());
            for (char probability:this.rangeValues.get(i).keySet()) {
                if (character == probability) { // umesto for-a this.rangeValues.get(i).get(character)
                    this.range = new Float[]{this.rangeValues.get(i).get(probability)[0], this.rangeValues.get(i).get(probability)[1]};
                    System.out.println(character + "==" + this.rangeValues.get(i).get(probability)[0] +"-"+ this.rangeValues.get(i).get(probability)[1]);
                }
            }
        }
    }

    // Write new values between ranges and calculate it with probabilities, for range sort by value asc

    private void makeBetweenRangeValues (char c) {
        this.rangeValues = new HashMap<>();
        Float prev = this.range[0];

        for (int i = 0; i < this.probabilities.size(); i++) {
            char pro_key = this.probabilities.get(i).keySet().toArray()[0].toString().charAt(0);
            Float probability = this.probabilities.get(i).get(pro_key);
            Float new_upper_limit = this.range[0] + ( ( this.range[1] - this.range[0] ) * probability );
            System.out.println("char(run): "+c+" | char(probability): "+this.probabilities.get(i).keySet().toArray()[0]+" | prev: "+prev+" | new_upper_limit: "+new_upper_limit);

            Map<Character, Float[]> range = new HashMap<>();
            Float[] arr = new Float[] {prev, new_upper_limit};
            range.put(pro_key, arr);
            this.rangeValues.put(i, range);
            prev = new_upper_limit;
        }

        this.chooseRange(c);

    }

    private void run () {
        for(int i = 0; i < this.chars.length; i++) {
            System.out.println(this.chars[i] + " @");
            if (i == 0) {
                this.chooseRange(this.chars[i]);
            } else {
                this.makeBetweenRangeValues(this.chars[i]);
            }
        }
    }

    private void makeProbabilities () {
        int i = 0;
        for(char c : this.stringAsChars.keySet()) {
            Float prev = (float) 0;
            if (i > 0) {
                for (char p:this.probabilities.get(i-1).keySet()) {
                    prev = this.probabilities.get(i-1).get(p);
                }
            }
            Map<Character, Float> value = new HashMap<>();
            Map<Character, Float[]> range = new HashMap<>();
            Float after = ((float) this.stringAsChars.get(c) / this.chars.length) + prev;
            value.put(c, after);
            Float[] arr = new Float[] {prev, after};
            range.put(c, arr);
            this.probabilities.put(i, value);
            this.rangeValues.put(i, range);
            i++;
        }
    }

    private void makeFile () throws IOException {
        long time = new Date().getTime() / 1000;
        String filename = "compress" + time + ".txt";
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("./src/main/resources/static/storage/" + filename), "utf-8"))) {
            Random r = new Random();
            float random = this.range[0] + r.nextFloat() * (this.range[1] - this.range[0]);
            writer.write( this.code + "##" + random + "##" + this.chars.length + "##");
            for(int i = 0; i < this.probabilities.size(); i++) {
                char pro_key = this.probabilities.get(i).keySet().toArray()[0].toString().charAt(0);
                Float probability = this.probabilities.get(i).get(pro_key);
                writer.write(i + "=>" + pro_key + "<>" + probability + "<=");
            }

            this.filename = filename;
        }
    }

    private void fileToString (MultipartFile file) throws IOException {
        this.fileAsString = new String(file.getBytes(), "UTF-8");
        ByteArrayInputStream stream = new   ByteArrayInputStream(file.getBytes());
        //this.fileAsString = stream.toString();
        //String content = Files.readString(file, StandardCharsets.US_ASCII);
        System.out.println(this.fileAsString);
    }

    private void stringToChars (String string) {
        this.chars = string.toCharArray();

        this.stringAsChars = new HashMap<>();
        for(char c : this.chars)
        {
            if(this.stringAsChars.containsKey(c)) {
                int counter = this.stringAsChars.get(c);
                this.stringAsChars.put(c, ++counter);
            } else {
                this.stringAsChars.put(c, 1);
            }
        }
    }
}
