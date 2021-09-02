package com.team2.pptor.controller;

import com.team2.pptor.domain.Board.BoardSaveForm;
import com.team2.pptor.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    /*
    게시판 생성
     */
    @GetMapping("usr/board/add")
    public String showAdd(Model model){

        model.addAttribute("boardSaveForm", new BoardSaveForm());

        return "usr/board/add";
    }

    /*
    게시판 글 작성
     */
    @GetMapping("usr/board/write")
    public String showWrite(){


        return "usr/board/write";
    }

    @GetMapping("usr/board/write")
    public String doWrite(){


        return "redirect:/";
    }

    /*
    공지사항
     */
    @GetMapping("usr/board/notice")
    public String showNotice(){


        return "usr/board/notice";
    }

    /*
    FAQ
     */
    @GetMapping("usr/board/faq")
    public String showFaq(){


        return "usr/board/faq";
    }




}


