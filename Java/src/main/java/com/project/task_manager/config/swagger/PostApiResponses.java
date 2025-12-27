package com.project.task_manager.config.swagger;

import com.project.task_manager.dto.response.MessageResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@StandardApiResponses
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "201",
                description = "Resource created successfully"
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Invalid request data",
                content = @Content(schema = @Schema(implementation = MessageResponse.class))
        )
})
public @interface PostApiResponses {
}

