package com.nztech.Usuario.business.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TelefoneDTO {
    private String numero;
    private String ddd;
}
