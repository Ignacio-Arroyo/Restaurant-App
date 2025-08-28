package restaurante.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import restaurante.backend.service.FileUploadService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    private final String uploadDir = "uploads";

    @PostMapping("/upload/{category}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE') or hasRole('COCINERO')")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @PathVariable String category) {
        
        try {
            String filePath = fileUploadService.uploadFile(file, category);
            Map<String, String> response = new HashMap<>();
            response.put("fileName", filePath);
            response.put("fileUrl", "/api/files/" + filePath);
            response.put("message", "File uploaded successfully");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to upload file");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/{category}/{filename:.+}")
    public ResponseEntity<Resource> getFile(
            @PathVariable String category,
            @PathVariable String filename) {
        
        try {
            Path filePath = Paths.get(uploadDir, category, filename);
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                // Try to determine file content type
                String contentType = "application/octet-stream";
                String fileName = resource.getFilename();
                if (fileName != null) {
                    if (fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg")) {
                        contentType = "image/jpeg";
                    } else if (fileName.toLowerCase().endsWith(".png")) {
                        contentType = "image/png";
                    } else if (fileName.toLowerCase().endsWith(".gif")) {
                        contentType = "image/gif";
                    } else if (fileName.toLowerCase().endsWith(".webp")) {
                        contentType = "image/webp";
                    }
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{category}/{filename:.+}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE') or hasRole('COCINERO')")
    public ResponseEntity<Map<String, String>> deleteFile(
            @PathVariable String category,
            @PathVariable String filename) {
        
        try {
            String filePath = category + "/" + filename;
            fileUploadService.deleteFile(filePath);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "File deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete file");
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
