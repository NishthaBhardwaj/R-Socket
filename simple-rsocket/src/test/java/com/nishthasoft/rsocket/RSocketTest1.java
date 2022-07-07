package com.nishthasoft.rsocket;

import com.nishthasoft.rsocket.dto.ChartResponseDto;
import com.nishthasoft.rsocket.dto.RequestDto;
import com.nishthasoft.rsocket.dto.ResponseDto;
import com.nishthasoft.rsocket.util.ObjectUtil;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RSocketTest1 {

    private RSocket rSocket;

    @BeforeAll
    public void setup(){
        this.rSocket = RSocketConnector.create()
                .connect(TcpClientTransport.create("localhost",6565))
                .block();

    }

    @Test
    public void fireAndForgetString(){
        Payload payload = DefaultPayload.create("Hello world!!");

        Mono<Void> mono = rSocket.fireAndForget(payload);

        StepVerifier.create(mono).
                verifyComplete();



    }

    @Test
    public void fireAndForget(){
        Payload payload = ObjectUtil.toPayload(new RequestDto(10));

        Mono<Void> mono = rSocket.fireAndForget(payload);

        StepVerifier.create(mono).
                verifyComplete();



    }


    @Test
    public void requestAndResponse() {
        Payload payload = ObjectUtil.toPayload(new RequestDto(7));

        Mono<ResponseDto> responseDtoMono = rSocket.requestResponse(payload)
                .map(p -> ObjectUtil.toObject(p, ResponseDto.class))
                .doOnNext(System.out::println);

        StepVerifier.create(responseDtoMono)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void requestStream(){
            Payload payload = ObjectUtil.toPayload(new RequestDto(7));

        Flux<ResponseDto> responseDtoFlux = rSocket.requestStream(payload)
                .map(p -> ObjectUtil.toObject(p, ResponseDto.class))
                .doOnNext(System.out::println)
                .take(5);

        StepVerifier.create(responseDtoFlux)
                .expectNextCount(5)
                .verifyComplete();

    }

    @Test
    public void requestChannel(){
        Flux<Payload> payLoadFlux = Flux.range(-10, 30)
                .delayElements(Duration.ofMillis(500))
                .map(i -> new RequestDto(i))
                .map(reqObjet -> ObjectUtil.toPayload(reqObjet));


        Flux<ChartResponseDto> chartFlux = this.rSocket.requestChannel(payLoadFlux)
                .map(p -> ObjectUtil.toObject(p, ChartResponseDto.class))
                        .doOnNext(chart -> System.out.println(chart));


        StepVerifier.create(chartFlux)
                .expectNextCount(30)
                .verifyComplete();

    }
}
