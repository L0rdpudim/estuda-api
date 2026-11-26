package br.com.estuda.apoioestudos.common;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.estuda.apoioestudos.usuario.service.CredenciaisInvalidasException;
import br.com.estuda.apoioestudos.usuario.service.EmailJaCadastradoException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<Map<String, String>> emailJaCadastrado(
        EmailJaCadastradoException exception
    ) {
        return resposta(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<Map<String, String>> credenciaisInvalidas(
        CredenciaisInvalidasException exception
    ) {
        return resposta(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> requisicaoInvalida(
        MethodArgumentNotValidException exception
    ) {
        String mensagem = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .findFirst()
            .map(erro -> erro.getDefaultMessage())
            .orElse("Os dados enviados são inválidos.");
        return resposta(HttpStatus.BAD_REQUEST, mensagem);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> argumentoInvalido(
        IllegalArgumentException exception
    ) {
        return resposta(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    private ResponseEntity<Map<String, String>> resposta(
        HttpStatus status,
        String mensagem
    ) {
        return ResponseEntity.status(status).body(Map.of("mensagem", mensagem));
    }
}
