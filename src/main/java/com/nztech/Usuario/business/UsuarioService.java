package com.nztech.Usuario.business;

import com.nztech.Usuario.business.converter.UsuarioConverter;
import com.nztech.Usuario.business.dtos.EnderecoDTO;
import com.nztech.Usuario.business.dtos.TelefoneDTO;
import com.nztech.Usuario.business.dtos.UsuarioDTO;
import com.nztech.Usuario.infrastructure.entity.Endereco;
import com.nztech.Usuario.infrastructure.entity.Telefone;
import com.nztech.Usuario.infrastructure.entity.Usuario;
import com.nztech.Usuario.infrastructure.exceptions.ConflictException;
import com.nztech.Usuario.infrastructure.exceptions.ResourceNotFoundException;
import com.nztech.Usuario.infrastructure.repository.EnderecoRepository;
import com.nztech.Usuario.infrastructure.repository.TelefoneRepository;
import com.nztech.Usuario.infrastructure.repository.UsuarioRepository;
import com.nztech.Usuario.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EnderecoRepository enderecoRepository;
    private final TelefoneRepository telefoneRepository;

    public UsuarioDTO salvarUsuario(UsuarioDTO usuarioDTO) {
        try {
            emailExiste(usuarioDTO.getEmail());
            usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
            Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
            usuario = usuarioRepository.save(usuario);
            return usuarioConverter.paraUsuarioDTO(usuario);
        } catch (ConflictException e) {

            throw new ConflictException("Email ja cadastrado");
        }


    }

    public UsuarioDTO buscarPorEmail(String email) {
        try {
            return usuarioConverter.paraUsuarioDTO(
                    usuarioRepository.findByEmail(email).orElseThrow(
                            () -> new ResourceNotFoundException("Email nao encontrado"))
            );

        }
        catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Email nao encontrado " + email);
        }
    }

    public List<UsuarioDTO> buscarTodos() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.stream().map(usuarioConverter::paraUsuarioDTO).toList();
    }

    public void deletarPorEmail(String email) {
        usuarioRepository.deleteByEmail(email);
    }


    public void emailExiste(String email) {
        try {
            boolean existe =  verificaEmailExistente(email);

            if (existe) {
                throw new ConflictException("Email ja cadastrado" + email);
            }
        }catch (ConflictException e) {
            throw new ConflictException("Email ja cadastrado" + e.getCause());
        }
    }

    public boolean verificaEmailExistente(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public UsuarioDTO atualizarDadosUsuario(String token, UsuarioDTO usuarioDTO) {
       // buscar email do usuario atraves do token(tirar a obrigatoriedade de passar email)
        String email = jwtUtil.extrairEmailToken(token.substring(7));

        // criptografia de senha
        usuarioDTO.setSenha(usuarioDTO.getSenha() != null ? passwordEncoder.encode(usuarioDTO.getSenha()) : null);

       // buscar os dados do usuario no banco de dados
        Usuario usuarioEntity = usuarioRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("Email nao encontrado"));

        // Mesclamos ou passamos os dados que recebemos na requisicao DTO com os dados do banco de dados
        Usuario usuario = usuarioConverter.updateUsuario(usuarioDTO, usuarioEntity);

        // salvar os dados do usuario convertido e depois  pegar o retorno e converter para UsuarioDTO
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));

    }

    public EnderecoDTO atualizarEndereco(Long idEndereco, EnderecoDTO enderecoDTO) {
        Endereco enderecoEntity = enderecoRepository.findById(idEndereco).orElseThrow(
                () -> new ResourceNotFoundException("Id nao encontrado " + idEndereco));
        Endereco endereco = usuarioConverter.updateEndereco(enderecoDTO, enderecoEntity);
        return usuarioConverter.paraEnderecoDTO(enderecoRepository.save(endereco));
    }

    public TelefoneDTO atualizarTelefone(Long idTelefone, TelefoneDTO telefoneDTO) {
        Telefone telefoneEntity = telefoneRepository.findById(idTelefone).orElseThrow(
                () -> new ResourceNotFoundException("Id do Telefone nao encontrado " + idTelefone));
        Telefone telefone = usuarioConverter.updateTelefone(telefoneDTO, telefoneEntity);
        return usuarioConverter.paraTelefoneDTO(telefoneRepository.save(telefone));
    }

    public EnderecoDTO cadastroEndereco(String token,  EnderecoDTO enderecoDTO) {
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Email nao encontrado " + email));

        Endereco endereco = usuarioConverter.paraEnderecoEntity(enderecoDTO, usuario.getId());
        return usuarioConverter.paraEnderecoDTO(enderecoRepository.save(endereco));

    }

    public TelefoneDTO cadastroEndereco(String token, TelefoneDTO telefoneDTO) {
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Email nao encontrado"));

        Telefone telefoneEntity = usuarioConverter.paraTelefoneEntity(telefoneDTO, usuario.getId());
        return usuarioConverter.paraTelefoneDTO(telefoneRepository.save(telefoneEntity));
    }



}
