package com.project.task_manager.config.swagger;

import com.project.task_manager.dto.response.MessageResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Invalid or missing JWT token",
                content = @Content(schema = @Schema(implementation = MessageResponse.class))
        )
})
@SecurityRequirement(name = "bearerAuth")
public @interface StandardApiResponses {
}
