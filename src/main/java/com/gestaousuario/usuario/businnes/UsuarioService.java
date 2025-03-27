package com.gestaousuario.usuario.businnes;

import com.gestaousuario.usuario.businnes.converter.UsuarioConverter;
import com.gestaousuario.usuario.businnes.dto.UsuarioDTO;
import com.gestaousuario.usuario.infrastructure.entity.Usuario;
import com.gestaousuario.usuario.infrastructure.exceptions.ConflitException;
import com.gestaousuario.usuario.infrastructure.exceptions.ResourceNotFoundException;
import com.gestaousuario.usuario.infrastructure.repository.UsuarioRepository;
import com.gestaousuario.usuario.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO){
        emailExiste(usuarioDTO.getEmail());
        usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));
    }


    public void emailExiste(String email){
        try {
            boolean existe = verificaEmailExistente(email);
            if(existe){
                throw new ConflitException("Email já cadastrado "+email);
            }

        } catch (ConflitException e) {
            throw new ConflitException("E-mail já cadastrado "+e.getCause());
        }

    }

    public boolean verificaEmailExistente(String email){

        return usuarioRepository.existsByEmail(email);
    }


    public Usuario buscarUsuarioPorEmail(String email){
        return usuarioRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Email não encontrado "+email));
    }

    public void deletaUsuarioPorEmail(String email){

        usuarioRepository.deleteByEmail(email);
    }

    public UsuarioDTO atualizaDadosUsuario(String token, UsuarioDTO usuarioDTO){

       // Busca o email através do token(tirar a obrigatoriedade de passar o email)
       String email = jwtUtil.extrairEmailToken(token.substring(7));

       // Criptografia de senha
       usuarioDTO.setSenha(usuarioDTO.getSenha() != null ? passwordEncoder.encode(usuarioDTO.getSenha()) : null );

       //Busca os dados do usuario no banco de dados
       Usuario usuarioEntity = usuarioRepository.findByEmail(email).orElseThrow(()->
               new ResourceNotFoundException("Email não localizado"));
       //Mesclou  os dados recebidos na requisicao DTO com os dados do banco de dados
       Usuario usuario = usuarioConverter.updateUsuario(usuarioDTO,usuarioEntity);

       //Salva os dados do usuario convertido e pega o retorno e converte para UsuarioDTO
       return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));

    }


}
