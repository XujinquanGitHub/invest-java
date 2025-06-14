package com.example.invest.controller;

import com.example.invest.model.GuornStrategy;
import com.example.invest.service.GuornService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/guorn")
public class GuornController {

    @Resource
    private GuornService guornService;

    @GetMapping("/strategies")
    public ResponseEntity<List<GuornStrategy>> getAllStrategies() {
        List<GuornStrategy> strategies = guornService.getAllStrategies();
        return ResponseEntity.ok(strategies);
    }

    @GetMapping("/strategies/{id}")
    public ResponseEntity<GuornStrategy> getStrategyById(@PathVariable String id) {
        Optional<GuornStrategy> strategy = guornService.getStrategyById(id);
        return strategy.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/strategies")
    public ResponseEntity<GuornStrategy> createStrategy(@RequestBody GuornStrategy strategy) {
        Optional<GuornStrategy> created = guornService.createStrategy(strategy);
        return created.map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/strategies/{id}")
    public ResponseEntity<GuornStrategy> updateStrategy(@PathVariable String id, @RequestBody GuornStrategy strategy) {
        strategy.setId(id);
        Optional<GuornStrategy> updated = guornService.updateStrategy(strategy);
        return updated.map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/strategies/{id}")
    public ResponseEntity<Void> deleteStrategy(@PathVariable String id) {
        boolean deleted = guornService.deleteStrategy(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
} 