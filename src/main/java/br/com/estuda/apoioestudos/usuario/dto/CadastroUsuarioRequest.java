package br.com.estuda.apoioestudos.usuario.dto;

import br.com.estuda.apoioestudos.usuario.entity.TipoPerfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CadastroUsuarioRequest(
    @NotBlank @Size(max = 150) String nome,
    @NotBlank @Email @Size(max = 255) String email,
    @NotBlank @Size(min = 8, max = 72) String senha,
    @NotNull TipoPerfil tipoPerfil
) {
}
