package eu.nites.compressor.controllers;

import eu.nites.compressor.models.Compress;
import eu.nites.compressor.models.Decompress;
import eu.nites.compressor.models.Hash;
import eu.nites.compressor.models.nayuki.ArithmeticCompress;
import eu.nites.compressor.models.nayuki.ArithmeticDecompress;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Controller
public class CompressorController {
    @RequestMapping(value = "/compress-file", method = RequestMethod.POST)
    public String compress(@RequestParam(name = "file", required = true) MultipartFile file, Model model) throws IOException {
        String crc = Hash.make(file);
        Compress compress = new Compress(file, crc);
        //model.addAttribute("link", compress.getLink());
        return "redirect:/download?file=" + compress.getLink();
    }

    @RequestMapping("/download")
    public String compress(@RequestParam(name = "file", required = true) String file, Model model) throws IOException {
        model.addAttribute("link", file);
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

    @RequestMapping(value = "/arithmetic-compress", method = RequestMethod.POST)
    public String Acompress(@RequestParam(name = "file", required = true) MultipartFile file, Model model) throws IOException {
        //String crc = Hash.make(file);
        //Path filepath = Paths.get("./src/main/resources/static/storage/", file.getOriginalFilename();
        File convFile = new File(System.getProperty("java.io.tmpdir")+"/"+file.getOriginalFilename());
        file.transferTo(convFile);
        ArithmeticCompress compress = new ArithmeticCompress(convFile);
        //model.addAttribute("link", compress.getLink());
        return "redirect:/download?file=" + compress.getLink();
    }

    @RequestMapping(value = "/arithmetic-decompress", method = RequestMethod.POST)
    public String Adecompress(@RequestParam(name = "file", required = true) MultipartFile file, Model model) throws IOException {
        //String crc = Hash.make(file);
        //Path filepath = Paths.get("./src/main/resources/static/storage/", file.getOriginalFilename();
        File convFile = new File(System.getProperty("java.io.tmpdir")+"/"+file.getOriginalFilename());
        file.transferTo(convFile);
        ArithmeticDecompress compress = new ArithmeticDecompress(convFile);
        //model.addAttribute("link", compress.getLink());
        return "redirect:/download?file=" + compress.getLink();
    }
}