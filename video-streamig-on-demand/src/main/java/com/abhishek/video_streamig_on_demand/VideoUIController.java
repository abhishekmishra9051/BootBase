package com.abhishek.video_streamig_on_demand;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class VideoUIController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/player")
    public String player(@RequestParam(required = false) String hls,
                         @RequestParam(required = false) String file,
                         Model model) {
        model.addAttribute("hlsUrl", hls);
        model.addAttribute("fileName", file);
        return "player";
    }
}
