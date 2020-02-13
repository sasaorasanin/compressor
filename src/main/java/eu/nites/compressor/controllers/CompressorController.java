package eu.nites.compressor.controllers;

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
    @RequestMapping("/sasa")
    public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "home";
    }

    @RequestMapping(value = "/compress-file", method = RequestMethod.POST)
    public String compress(@RequestParam(name = "file", required = true) MultipartFile file, Model model) throws IOException {
        String crc = Hash.make(file);
        model.addAttribute("link", crc);
        return "download";
    }

    @RequestMapping(value = "/decompress-file", method = RequestMethod.POST)
    public String decompress(@RequestParam(name = "file", required = true) MultipartFile file, Model model) throws IOException {
        String crc = Hash.make(file);


        model.addAttribute("link", crc);
        return "download";
    }


}