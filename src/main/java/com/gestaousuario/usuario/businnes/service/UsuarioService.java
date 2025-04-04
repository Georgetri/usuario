package com.gestaousuario.usuario.businnes.service;

import com.gestaousuario.usuario.businnes.converter.UsuarioConverter;
import com.gestaousuario.usuario.businnes.dto.EnderecoDTO;
import com.gestaousuario.usuario.businnes.dto.TelefoneDTO;
import com.gestaousuario.usuario.businnes.dto.UsuarioDTO;
import com.gestaousuario.usuario.infrastructure.entity.Endereco;
import com.gestaousuario.usuario.infrastructure.entity.Telefone;
import com.gestaousuario.usuario.infrastructure.entity.Usuario;
import com.gestaousuario.usuario.infrastructure.exceptions.ConflitException;
import com.gestaousuario.usuario.infrastructure.exceptions.ResourceNotFoundException;
import com.gestaousuario.usuario.infrastructure.repository.EnderecoRepository;
import com.gestaousuario.usuario.infrastructure.repository.TelefoneRepository;
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
    private final EnderecoRepository enderecoRepository;
    private final TelefoneRepository telefoneRepository;

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


    public UsuarioDTO buscarUsuarioPorEmail(String email){
        try {
            return usuarioConverter.paraUsuarioDTO(
                    usuarioRepository.findByEmail(email)
                            .orElseThrow(
                    () -> new ResourceNotFoundException("Email não encontrado "+email)
                            )
            );
        }catch (ResourceNotFoundException e){
            throw new ResourceNotFoundException("E-mail não encontrado " + email);
        }

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


    public EnderecoDTO atualizaEndereco(Long idEndereco, EnderecoDTO enderecoDTO){

        Endereco entity = enderecoRepository.findById(idEndereco).orElseThrow(()->
                new ResourceNotFoundException("Id não encontrado " + idEndereco));
        Endereco endereco = usuarioConverter.updateEndereco(enderecoDTO, entity);

        return usuarioConverter.paraEnderecoDTO(enderecoRepository.save(endereco));
    }


    public TelefoneDTO atualizaTelefone(Long idTelefone, TelefoneDTO dto){

        Telefone entity = telefoneRepository.findById(idTelefone).orElseThrow(()->
                new ResourceNotFoundException("ID não encontrado " + idTelefone));

        Telefone telefone = usuarioConverter.updateTelefone(dto, entity);

        return usuarioConverter.paraTelefoneDTO(telefoneRepository.save(telefone));
    }


    public EnderecoDTO cadastraEndereco(String token, EnderecoDTO dto){
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(()->
                new ResourceNotFoundException("E-mail não localizado " + email));

        Endereco endereco = usuarioConverter.paraEnderedoEntity(dto, usuario.getId());
        Endereco enderecoEntity = enderecoRepository.save(endereco);
        return usuarioConverter.paraEnderecoDTO(enderecoEntity);
    }


    public TelefoneDTO cadastraTelefone(String token, TelefoneDTO dto){
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(()->
                new ResourceNotFoundException("E-mail não localizado " + email));

        Telefone telefone = usuarioConverter.paraTelefoneEntity(dto, usuario.getId());
        return usuarioConverter.paraTelefoneDTO(
                telefoneRepository.save(telefone)
        );
    }


}
