package eu.nites.compressor.models;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;

public class Hash {

    public static String make(MultipartFile file) throws IOException {
        InputStream inputStreamn = file.getInputStream();
        CRC32 crc = new CRC32();
        int cnt;
        while ((cnt = inputStreamn.read()) != -1) {
            crc.update(cnt);
        }
        return Long.toHexString(crc.getValue());
    }

}
