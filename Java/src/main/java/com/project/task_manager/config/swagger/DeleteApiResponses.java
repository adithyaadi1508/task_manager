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
                responseCode = "200",
                description = "Resource deleted successfully"
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Resource not found",
                content = @Content(schema = @Schema(implementation = MessageResponse.class))
        )
})
public @interface DeleteApiResponses {
}

