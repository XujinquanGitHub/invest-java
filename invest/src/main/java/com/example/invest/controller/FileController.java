package com.example.invest.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @GetMapping("/read/{fileName}")
    public ResponseEntity<String> readFile(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get("D:\\Projects\\data", fileName);
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }
            
            String content = Files.lines(filePath).collect(Collectors.joining("\n"));
            return ResponseEntity.ok(content);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("读取文件失败: " + e.getMessage());
        }
    }
} 