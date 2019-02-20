package ru.ifmo.web.service.exception;

import lombok.Getter;

import javax.xml.ws.WebFault;

@WebFault(faultBean = "ru.ifmo.web.service.exception.AstartesServiceFault")
public class AstartesServiceException extends Exception {
    @Getter
    private final AstartesServiceFault faultInfo;

    public AstartesServiceException(String message, AstartesServiceFault faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    public AstartesServiceException(String message, Throwable cause, AstartesServiceFault faultInfo) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }
}
