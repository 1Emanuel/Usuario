package com.nztech.Usuario.controllers;

import com.nztech.Usuario.business.UsuarioService;
import com.nztech.Usuario.business.dtos.UsuarioDTO;
import com.nztech.Usuario.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/usuario")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<UsuarioDTO> salvarUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        usuarioService.salvarUsuario(usuarioDTO);
        return ResponseEntity.ok(usuarioDTO);
    }

    @PostMapping("/login")
    public String login(@RequestBody UsuarioDTO usuarioDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(usuarioDTO.getEmail(),
                        usuarioDTO.getSenha())
        );
        return "Bearer " + jwtUtil.generateToken(authentication.getName());
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioDTO>> buscarTodos() {
        return ResponseEntity.ok(usuarioService.buscarTodos());
    }

    @GetMapping
    public ResponseEntity<UsuarioDTO> buscarPorEmail(@RequestParam("email") String email) {
        return ResponseEntity.ok(usuarioService.buscarPorEmail(email));
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deletarPorEmail(@PathVariable String email) {
        usuarioService.deletarPorEmail(email);
        return ResponseEntity.noContent().build();
    }
}
