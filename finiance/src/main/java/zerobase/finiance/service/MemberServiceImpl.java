package zerobase.finiance.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import zerobase.finiance.exception.impl.AlreadyExistUserException;
import zerobase.finiance.model.Auth;
import zerobase.finiance.model.MemberEntity;
import zerobase.finiance.repository.MemberRepository;

@Service
@Slf4j
@AllArgsConstructor
public class MemberServiceImpl implements UserDetailsService, MemberService{

    @Autowired
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder; //그냥 password가 아닌 인코딩된 password

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("couldn't find user -> " + username));
    }


    @Override
    public MemberEntity register(Auth.SignUp member) {
        boolean exists = memberRepository.existsByUsername(member.getUsername());
        if (exists){
            throw new AlreadyExistUserException();
        }

        member.setPassword(passwordEncoder.encode(member.getPassword()));
        MemberEntity save = memberRepository.save(member.toEntity());
        return save;
    }

    @Override
    public MemberEntity authenticate(Auth.SignIn member) {
        MemberEntity userId = memberRepository.findByUsername(member.getUsername())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 Id"));

        if (passwordEncoder.matches(member.getPassword(), userId.getPassword())){
            throw new RuntimeException("비밀번호 일치 하지 않음");
        }

        return userId;
    }
}
