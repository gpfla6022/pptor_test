package com.team2.pptor.domain.Board;

import com.team2.pptor.domain.Member.Member;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BoardSaveForm {

    @NotBlank
    private String name;

}
