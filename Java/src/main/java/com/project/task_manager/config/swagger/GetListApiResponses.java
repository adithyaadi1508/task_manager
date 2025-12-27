package com.project.task_manager.config.swagger;

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
                description = "List retrieved successfully"
        )
})
public @interface GetListApiResponses {
}
