
package br.com.estuda.apoioestudos.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import br.com.estuda.apoioestudos.services.StatusService;
import br.com.estuda.apoioestudos.dtos.StatusResponse;

@RestController
@RequestMapping("/api/v1")
public class StatusController {

    private final StatusService statusService;

    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }

    @GetMapping("/status")
    public ResponseEntity<StatusResponse> getStatus() {
        return ResponseEntity.ok(statusService.getStatus());
    }
}