package demo.server.controller;

import demo.server.model.Attachment;
import demo.server.repo.AttachmentRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

@RestController
@RequestMapping("/media")
public class MediaController {
    private final AttachmentRepo repo;
    @Value("${app.upload.dir:/tmp/uploads}")
    private String uploadDir;

    public MediaController(AttachmentRepo repo){ this.repo = repo; }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Attachment upload(@RequestPart("file") MultipartFile file) throws IOException {
        Files.createDirectories(Path.of(uploadDir));
        String key = Instant.now().toEpochMilli() + "_" + StringUtils.cleanPath(file.getOriginalFilename());
        Path dest = Path.of(uploadDir, key);
        Files.copy(file.getInputStream(), dest);
        var att = Attachment.builder()
                .bucket("local")
                .objectKey(key)
                .mimeType(file.getContentType())
                .sizeBytes(file.getSize())
                .createdAt(Instant.now())
                .build();
        return repo.save(att);
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> get(@PathVariable Long id) throws IOException {
        var att = repo.findById(id).orElseThrow();
        Path p = Path.of(uploadDir, att.getObjectKey());
        byte[] bytes = Files.readAllBytes(p);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename="+att.getObjectKey())
                .contentType(MediaType.parseMediaType(att.getMimeType()==null?"application/octet-stream":att.getMimeType()))
                .body(bytes);
    }
}
