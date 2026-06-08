package com.algaworks.algasensors.device.management.api.config.web;

import com.algaworks.algasensors.device.management.api.client.SensorMonitoringClient;
import com.algaworks.algasensors.device.management.api.client.SensorMonitoringClientBadGatewayException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.nio.channels.ClosedChannelException;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
            SocketTimeoutException.class,
            ConnectException.class,
            ClosedChannelException.class
    })
    public ProblemDetail handle(IOException e) {
        var detail = ProblemDetail.forStatus(HttpStatus.GATEWAY_TIMEOUT);
        detail.setTitle("Gateway timeout");
        detail.setDetail(e.getMessage());
        detail.setType(URI.create("/errors/gateway-timeout"));
        return detail;
    }

    @ExceptionHandler(SensorMonitoringClientBadGatewayException.class)
    public ProblemDetail handle(SensorMonitoringClientBadGatewayException e) {
        var detail = ProblemDetail.forStatus(HttpStatus.BAD_GATEWAY);
        detail.setTitle("Bad gateway");
        detail.setDetail(e.getMessage());
        detail.setType(URI.create("/errors/bad-gateway"));
        return detail;
    }

}
