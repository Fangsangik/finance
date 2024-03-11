package zerobase.finiance.service;

import org.springframework.stereotype.Service;
import zerobase.finiance.model.Auth;
import zerobase.finiance.model.MemberEntity;

@Service
public interface MemberService {

    MemberEntity register(Auth.SignUp member);
    MemberEntity authenticate(Auth.SignIn member);
}
