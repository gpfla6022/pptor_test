package com.team2.pptor.controller;

import com.team2.pptor.domain.Article.Article;
import com.team2.pptor.domain.Article.ArticleModifyForm;
import com.team2.pptor.domain.Article.Content;
import com.team2.pptor.domain.Member.Member;
import com.team2.pptor.service.ArticleService;
import com.team2.pptor.domain.Article.ArticleSaveForm;
import com.team2.pptor.service.MemberService;
import com.team2.pptor.util.HtmlParser;
import com.team2.pptor.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UsrArticleController {

    private final ArticleService articleService;
    private final MemberService memberService;

    /*
    PPT 작성 페이지 이동
     */
    @GetMapping("usr/article/write")
    public String showWrite(Model model){

        model.addAttribute("articleSaveForm", new ArticleSaveForm());

        return "usr/article/write";
    }

    /*
    PPT 작성 메소드
     */
    @PostMapping("usr/article/write")
    public String doWrite(@Validated @ModelAttribute ArticleSaveForm articleSaveForm, BindingResult bindingResult, Principal principal){

        // 오류가 확인되어 바인딩 되었다면
        if ( bindingResult.hasErrors() ) {
            // 로그에 표기와 같이 표기
            log.info("ERRORS={}",bindingResult);
            return "redirect:/usr/article/write";
        }

        // Principal 객체를 이용하여 로그인한 회원의 아이디를 조회
        // 조회한 아이디로서 멤버객체를 불러오기
        Member member = memberService.findByLoginId(principal.getName());

        System.out.println("html : " + articleSaveForm.getHtml());
        System.out.println("markdown : " + articleSaveForm.getMarkdown());
        // 생성메소드를 통하여 게시글 객체 내부에 회원 객체 주입
        Article article = Article.createArticle(
                articleSaveForm.getTitle(),
                articleSaveForm.getMarkdown(),
                articleSaveForm.getHtml(),
                member
        );

        // 연관관계 메소드 호출
        article.setMember(member);

        articleService.save(article);

        return "redirect:/usr/article/view/" + article.getId();

    }

    /*
    PPT 수정 페이지 이동
    */
    @GetMapping("usr/article/modify/{id}")
    public String showModify(@PathVariable("id") int id, Model model, Principal principal){

        Article findArticle = articleService.findById(id);

        if ( !findArticle.getMember().getLoginId().equals(principal.getName())) {
            log.info("ERROR : 권한이 없는 시도를 하였습니다.");
            return "redirect:/";
        }

        ArticleModifyForm articleModifyForm = new ArticleModifyForm();

        articleModifyForm.setTitle(findArticle.getTitle());

        model.addAttribute("articleModifyForm", articleModifyForm);

        return "usr/article/modify";
    }

    /*
    PPT 수정
    */
    @PostMapping("usr/article/modify")
    public String doModify(@Validated @ModelAttribute ArticleModifyForm articleModifyForm, BindingResult bindingResult, Principal principal){

        // 수정가능여부 확인 필
        Article findArticle = articleService.findById(articleModifyForm.getId());

        if ( !findArticle.getMember().getLoginId().equals(principal.getName())) {
            log.info("ERROR : 권한이 없는 시도를 하였습니다.");
            return "redirect:/";
        }

        // 오류가 확인되어 바인딩 되었다면
        if ( bindingResult.hasErrors() ) {
            // 로그에 표기와 같이 표기
            log.info("ERRORS={}",bindingResult);
            return "usr/member/join";
        }

        Member member = memberService.findByLoginId(principal.getName());

         articleService.modify(articleModifyForm, member);

        return "redirect:/usr/article/detail/" + articleModifyForm.getId();
    }

    /*
    PPT 삭제
    */
    @GetMapping("usr/article/doDelete/{id}")
    public String doDelete(@PathVariable("id") int id){

        articleService.delete(id);

        return "redirect:/usr/article/list";
    }

    /*
    PPT 상세 페이지 이동
    */
    @GetMapping("usr/article/detail/{id}")
    public String showDetail(@PathVariable("id") int id, Model model){

        try {

            // 500 Page 로딩 (수정)
            // 404 Page가 의도된 페이지 (dnlwjtud)
            Article article = articleService.findById(id);
            model.addAttribute("article", article);

            return "usr/article/detail";

        } catch ( Exception e ) {
            log.info("ERROR : {}", e.getMessage());
            return "redirect:/error/404";
        }

    }

    /*
    PPT 목록 페이지 (수정)
    */
    @GetMapping("usr/article/list")
    public String showList(Model model){

        List<Article> articles = articleService.list();

        model.addAttribute("articles", articles );
        
        return "usr/article/list";
    }

    /*
    PPTO 뷰 모드
    */
    @GetMapping("usr/article/view/{id}")
    public String showViewMode(@PathVariable("id") int id, Model model) {

        Article findArticle = articleService.findById(id);

        HtmlParser parser = new HtmlParser();

        List<Content> parsedHtml = parser.getParsedHtml(findArticle.getHtml());

        model.addAttribute("articleDetail", parsedHtml);

        return "usr/article/view";

    }



}