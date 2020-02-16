package eu.nites.compressor.controllers;

import eu.nites.compressor.models.Compress;
import eu.nites.compressor.models.Decompress;
import eu.nites.compressor.models.Hash;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class CompressorController {
    @RequestMapping(value = "/compress-file", method = RequestMethod.POST)
    public String compress(@RequestParam(name = "file", required = true) MultipartFile file, Model model) throws IOException {
        String crc = Hash.make(file);
        Compress compress = new Compress(file, crc);
        model.addAttribute("link", compress.getLink());
        return "download";
    }

    @RequestMapping(value = "/decompress-file", method = RequestMethod.POST)
    public String decompress(@RequestParam(name = "file", required = true) MultipartFile file, Model model) throws IOException {
        Decompress decompress = new Decompress(file);
        if (decompress.checkHash()) {
            model.addAttribute("link", decompress.getLink());
            return "download";
        }
        model.addAttribute("message", "File was not decompressed correctly, it may be broken.");
        return "error";
    }
}