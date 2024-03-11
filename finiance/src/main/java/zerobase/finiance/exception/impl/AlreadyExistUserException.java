package zerobase.finiance.exception.impl;

import org.springframework.http.HttpStatus;
import zerobase.finiance.exception.AbstractException;

public class AlreadyExistUserException extends AbstractException {
    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "이미 존제하는 사용자 입니다";
    }
}
