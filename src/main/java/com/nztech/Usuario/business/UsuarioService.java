package com.nztech.Usuario.business;

import com.nztech.Usuario.business.converter.UsuarioConverter;
import com.nztech.Usuario.business.dtos.UsuarioDTO;
import com.nztech.Usuario.infrastructure.entity.Usuario;
import com.nztech.Usuario.infrastructure.exceptions.ConflictException;
import com.nztech.Usuario.infrastructure.repository.UsuarioRepository;
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
         Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(
                 () -> new UsernameNotFoundException("email nao econtrado"));
         return usuarioConverter.paraUsuarioDTO(usuario);
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





}
