package com.gestaousuario.usuario.businnes;

import com.gestaousuario.usuario.businnes.converter.UsuarioConverter;
import com.gestaousuario.usuario.businnes.dto.UsuarioDTO;
import com.gestaousuario.usuario.infrastructure.entity.Usuario;
import com.gestaousuario.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;

    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO){
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        usuario = usuarioRepository.save(usuario);
        return usuarioConverter.paraUsuarioDTO(usuario);
     // return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));
    }

}
