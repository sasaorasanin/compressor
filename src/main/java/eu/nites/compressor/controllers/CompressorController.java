package eu.nites.compressor.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;

@Controller
public class CompressorController {
    @RequestMapping("/sasa")
    //@ResponseBody
    public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "home";
    }

    @RequestMapping(value = "/compress-file", method = RequestMethod.POST)
    //@ResponseBody
    public String compress(@RequestParam(name = "file", required = true) MultipartFile file, Model model) throws IOException {
        long crc = checksumInputStream(file);
        model.addAttribute("link", Long.toHexString(crc));
        return "download";
    }

//    @RequestMapping("/decompress-file")
//    //@ResponseBody
//    public String decompress(@RequestParam(name="name", required=false, defaultValue="World") String name) {
//        return "home";
//    }

    public long checksumInputStream(MultipartFile file) throws IOException {
        InputStream inputStreamn = file.getInputStream();
        CRC32 crc = new CRC32();
        int cnt;
        while ((cnt = inputStreamn.read()) != -1) {
            crc.update(cnt);
        }
        return crc.getValue();
    }
}