package com.gestaousuario.usuario.businnes.service;

import com.gestaousuario.usuario.infrastructure.clients.ViaCepClient;
import com.gestaousuario.usuario.infrastructure.clients.ViaCepDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ViaCepService {

    private final ViaCepClient client;

    public ViaCepDTO buscarDadosEndereco(String cep){

        return client.buscaDadosEndereco(processarCep(cep));
    }


    private String processarCep(String cep){
        String cepFormatado = cep.replace(" ","").
                                  replace("-","");

        if(!cepFormatado.matches("\\d+") || !Objects.equals(cepFormatado.length(), 8)){  // \\d+
            throw new IllegalArgumentException("Cep contém caracteres inválidos, favor verificar ");
        }
        return cepFormatado;
    }

}
