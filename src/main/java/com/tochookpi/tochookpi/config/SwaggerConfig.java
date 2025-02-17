package com.tochookpi.tochookpi.config;

import com.tochookpi.tochookpi.enums.ErrorCode;
import com.tochookpi.tochookpi.exception.ApiErrorResponses;
import com.tochookpi.tochookpi.exception.ErrorResponse;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SwaggerConfig implements OperationCustomizer {
    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        ApiErrorResponses annotation = handlerMethod.getMethodAnnotation(ApiErrorResponses.class);

        // @ApiErrorResponses 애노테이션이 있는지 확인
        if(annotation != null) {
            String requestPath = getRequestPath(handlerMethod).orElse("/unknown");

            // HTTP 상태 코드별로 ErrorCode를 그룹화
            Map<HttpStatus, List<ErrorCode>> httpStatusGroup = Arrays.stream(annotation.value())
                    .collect(Collectors.groupingBy(ErrorCode::getHttpStatus));

            httpStatusGroup.forEach((httpStatus, errorCodes) -> {
                Content content = new Content();
                MediaType mediaType = new MediaType();

                // 같은 HTTP 상태 코드에서 여러 개의 예제 등록
                errorCodes.forEach(errorCode -> {
                    ErrorResponse errorResponse = new ErrorResponse(
                            errorCode.getHttpStatus(),
                            errorCode.getCode(),
                            errorCode.getMessage(),
                            requestPath
                    );
                    mediaType.addExamples(errorCode.getCode(), new Example()
                            .description(errorCode.getMessage())
                            .value(errorResponse));
                });

                content.addMediaType("application/json", mediaType);

                // HTTP 상태 코드에 대응하는 ApiResponse 추가
                ApiResponse apiResponse = new ApiResponse()
                        .description(httpStatus.getReasonPhrase())
                        .content(content);

                operation.getResponses().addApiResponse(String.valueOf(httpStatus.value()), apiResponse);
            });
        }

        return operation;
    }

    private Optional<String> getRequestPath(HandlerMethod handlerMethod) {
        // 클래스에 정의된 @RequestMapping의 경로
        String classPath = Optional.ofNullable(handlerMethod.getBeanType().getAnnotation(RequestMapping.class))
                .map(mapping -> mapping.value().length > 0 ? mapping.value()[0] : "")
                .orElse("");

        // 메서드에 정의된 @RequestMapping, @GetMapping, @PostMapping, ...의 경로
        String methodPath = Optional.ofNullable(handlerMethod.getMethodAnnotation(RequestMapping.class))
                .map(mapping -> mapping.value().length > 0 ? mapping.value()[0] : "")
                .orElseGet(() -> {
                    if (handlerMethod.hasMethodAnnotation(GetMapping.class)) {
                        return Arrays.stream(handlerMethod.getMethodAnnotation(GetMapping.class).value()).findFirst().orElse("");
                    }
                    if (handlerMethod.hasMethodAnnotation(PostMapping.class)) {
                        return Arrays.stream(handlerMethod.getMethodAnnotation(PostMapping.class).value()).findFirst().orElse("");
                    }
                    if (handlerMethod.hasMethodAnnotation(PutMapping.class)) {
                        return Arrays.stream(handlerMethod.getMethodAnnotation(PutMapping.class).value()).findFirst().orElse("");
                    }
                    if (handlerMethod.hasMethodAnnotation(DeleteMapping.class)) {
                        return Arrays.stream(handlerMethod.getMethodAnnotation(DeleteMapping.class).value()).findFirst().orElse("");
                    }
                    return "";
                });

        // 클래스 경로 + 메서드 경로 리턴
        String fullPath = classPath + methodPath;
        return fullPath.isEmpty() ? Optional.empty() : Optional.of(fullPath);
    }
}
