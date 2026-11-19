
package br.com.estuda.apoioestudos.services;

import org.springframework.stereotype.Service;

import br.com.estuda.apoioestudos.dtos.StatusResponse;

@Service
public class StatusService {

    public StatusResponse getStatus() {
        return new StatusResponse(
            "online",
            "API funcionando corretamente"
        );
    }
}