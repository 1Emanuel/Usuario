package com.nztech.Usuario.business;

import com.nztech.Usuario.business.converter.UsuarioConverter;
import com.nztech.Usuario.business.dtos.UsuarioDTO;
import com.nztech.Usuario.infrastructure.entity.Usuario;
import com.nztech.Usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;

    public UsuarioDTO salvarUsuario(UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        usuario = usuarioRepository.save(usuario);
        return usuarioConverter.paraUsuarioDTO(usuario);

    }



}
