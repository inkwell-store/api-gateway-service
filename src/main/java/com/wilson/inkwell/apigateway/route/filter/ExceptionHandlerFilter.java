package com.wilson.inkwell.apigateway.route.filter;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import com.wilson.inkwell.apigateway.enums.HttpErrorEnum;
import com.wilson.inkwell.apigateway.exception.ExceptionResponseBody;
import com.wilson.inkwell.apigateway.exception.HttpCustomException;

import lombok.RequiredArgsConstructor;

/**
 * Filter that catches and process exceptions thrown during the function handling.
 */
@Component
@RequiredArgsConstructor  
public class ExceptionHandlerFilter implements  HandlerFilterFunction<ServerResponse, ServerResponse>{

    @Override
    public ServerResponse filter(ServerRequest request, HandlerFunction<ServerResponse> next) throws Exception {

        try {
            return next.handle(request);            
        } catch (Exception e) {

            // check the type of exception
            if(e instanceof HttpCustomException) {

                HttpCustomException exception = (HttpCustomException) e;
                HttpErrorEnum errorType = exception.getErrorType();
                int status = 0;

                switch (errorType) {
                    case ACCESS_TOKEN_INVALID: status = HttpStatus.UNAUTHORIZED.value(); break;
                    case INVALID_PARAMETER_VALUE: status = HttpStatus.BAD_REQUEST.value(); break;
                    case SERVICE_UNAVAILABLE: status = HttpStatus.BAD_GATEWAY.value(); break;

                    default: status = HttpStatus.INTERNAL_SERVER_ERROR.value(); break;
                }

                // if it's a mapped by me return the appropriate error
                return ServerResponse.status(status).body(
                    new ExceptionResponseBody(
                        status,
                        exception.getErrorType(),
                        exception.getMessage(),
                        Instant.now()
                    )
                );

            } else {

                // if not thrown an internal, unmapped error exception
                return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ExceptionResponseBody(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        HttpErrorEnum.UNMAPPED_ERROR,
                        "An unknown error happened",
                        Instant.now()
                    )
                );
            }

        }

    }
    
}
