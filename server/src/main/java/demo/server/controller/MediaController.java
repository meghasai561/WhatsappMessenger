package demo.server.controller;

import demo.server.model.Attachment;
import demo.server.repo.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
public class MediaController {
    private final AttachmentRepository attRepo;

    @PostMapping("/upload")
    public Attachment upload(@RequestParam("file") MultipartFile file) throws IOException {
        String key = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Paths.get("uploads").resolve(key);
        Files.createDirectories(path.getParent());
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        Attachment att = Attachment.builder().objectKey(key).mimeType(file.getContentType()).build();
        return attRepo.save(att);
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> download(@PathVariable Long id) throws IOException {
        Attachment att = attRepo.findById(id).orElseThrow();
        Path path = Paths.get("uploads").resolve(att.getObjectKey());
        byte[] data = Files.readAllBytes(path);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(att.getMimeType()))
                .body(data);
    }
}
