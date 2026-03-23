package com.abhishek.video_streamig_on_demand;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

// upload the video

// FFmpeg -> (Transcoding + Segmentation)

// Generate playlist -> chunks (2 seconds)

// spring boot will serve the file

// hls.js -> video player

@RestController
@RequestMapping("/videos")
public class VideoUploadController {

    private final Path storageLocation = Paths.get("videos/");

    @PostMapping("/upload")
    public ResponseEntity<String> handleUpload(@RequestParam("videoFile") MultipartFile file)
            throws IOException, InterruptedException {

        if (Files.notExists(storageLocation)) {
            Files.createDirectories(storageLocation);
        }

        Path inputFile = storageLocation
                .resolve(Objects.requireNonNull(file.getOriginalFilename()));

        Files.copy(file.getInputStream(), inputFile,
                StandardCopyOption.REPLACE_EXISTING);

        // output directory for HLS segments and playlist

        Path outputDir = storageLocation.resolve("hls/" + UUID.randomUUID());
        Files.createDirectories(outputDir);

        // FFmpeg command to transcode and segment the video into HLS format
        String ffmpegCmd = String.format(
                "ffmpeg -i %s -c:v libx264 -c:a aac -g 48 -keyint_min 48 -sc_threshold 0 -hls_time 2 -hls_list_size 0 -f hls %s/index.m3u8",
                inputFile.toAbsolutePath(),
                outputDir.toAbsolutePath()
        );

        // Execute the FFmpeg command
        ProcessBuilder pb= new ProcessBuilder("bash","-c", ffmpegCmd);
        pb.inheritIO().start().waitFor();


        return ResponseEntity.ok("Uploaded and transcoded to HLS: " + outputDir);
    }


}
