package zerobase.finiance.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zerobase.finiance.model.Auth;
import zerobase.finiance.model.MemberEntity;
import zerobase.finiance.security.TokenProvider;
import zerobase.finiance.service.MemberServiceImpl;


@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberServiceImpl memberService;
    private final TokenProvider tokenProvider;

    @GetMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody Auth.SignUp request){
        //회원 가입을 위한 API
        MemberEntity register = memberService.register(request);
        return ResponseEntity.ok(register);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody Auth.SignIn request){
        //로그인 API
        //password 인증 -> token으로 반환
        MemberEntity member =
                memberService.authenticate(request);
        String token = tokenProvider.generateToken(member.getUsername(), member.getRoles());
        log.info("user login -> " + request.getUsername());
        return ResponseEntity.ok(token);
    }
}
