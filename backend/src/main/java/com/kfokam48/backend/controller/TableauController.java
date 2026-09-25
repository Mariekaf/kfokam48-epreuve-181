package com.kfokam48.backend.controller;

import com.kfokam48.backend.dto.TableauEtudiantResponse;
import com.kfokam48.backend.service.TableauService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tableau")
public class TableauController {

    private final TableauService tableauService;

    public TableauController(TableauService tableauService) {
        this.tableauService = tableauService;
    }

    @GetMapping
    public ResponseEntity<List<TableauEtudiantResponse>> consulterTableau(
            @RequestParam Long promotionId
    ) {

        return ResponseEntity.ok(
                tableauService.consulterTableau(promotionId)
        );
    }
}