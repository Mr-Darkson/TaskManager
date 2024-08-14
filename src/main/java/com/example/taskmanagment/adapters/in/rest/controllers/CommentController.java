package com.example.taskmanagment.adapters.in.rest.controllers;

import com.example.taskmanagment.application.domain.dto.CommentUpdateDTO;
import com.example.taskmanagment.application.domain.dto.CommentViewDTO;
import com.example.taskmanagment.application.domain.dto.TaskViewDTO;
import com.example.taskmanagment.application.domain.enums.PriorityLevel;
import com.example.taskmanagment.application.domain.enums.TaskStatus;
import com.example.taskmanagment.application.ports.in.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Comment Controller", description = "Эндпоинты связанны с сущностью 'Comment'")
@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;


    @Operation(
            summary = "Создать комментарий для задачи с ID",
            description = """
                    Получает ID задачи и комментарий. Если задача с таким ID существует, комментарий проходит валидацию, то сущность сохраняется в базу.
                    ДОСТУП: Любой пользователь
                    """,
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Комментарий создан", content = @Content(schema = @Schema(implementation = TaskViewDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "401", description = "Ошибка аутентификации", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "500", description = "Ошибка на стороне сервера", content = @Content(schema = @Schema(implementation = String.class)))}

    )
    @PostMapping("/{taskId}")
    public ResponseEntity<String> createComment(@PathVariable("taskId") String taskId ,@Valid @RequestBody CommentUpdateDTO commentDto){
        commentService.createComment(commentDto, taskId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Task created");
    }

    @Operation(
            summary = "Удалить комментарий с ID",
            description = """
                    Получает ID комментария. Если комментарий с таким ID существует, то удаляется из базы.
                    ДОСТУП: Автор задачи или владелец комментария
                    """,
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Комментарий удалён", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "401", description = "Ошибка аутентификации", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "403", description = "Отказано в доступе", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "404", description = "Не найден комментарий", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "500", description = "Ошибка на стороне сервера", content = @Content(schema = @Schema(implementation = String.class)))}

    )
    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable("commentId") String commentId){
        commentService.delete(commentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Task deleted");
    }


    @Operation(
            summary = "Получить комментарии к задаче, по её ID",
            description = """
                    Получает ID задачи и параметры. Если задача с таким ID существует, то возвращает комментарии к ней.
                    ДОСТУП: Любой пользователь
                    """,
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "page", description = "Текущая страница, которую нужно показать. По умолчанию: 0", required = false, schema = @Schema(implementation = String.class)),
                    @Parameter(name = "size", description = "Размер одной страницы. По умолчанию: 10", required = false, schema = @Schema(implementation = String.class)),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Комментарии получены", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CommentViewDTO.class)))),
                    @ApiResponse(responseCode = "401", description = "Ошибка аутентификации", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "404", description = "Не найдена задача или комментарии", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "500", description = "Ошибка на стороне сервера", content = @Content(schema = @Schema(implementation = String.class)))}

    )
    @GetMapping("/{taskId}")
    public ResponseEntity<List<CommentViewDTO>> getCommentsFromTask(@PathVariable("taskId") String taskId,
                                                                    @RequestParam(name = "page", defaultValue = "0") String page,
                                                                    @RequestParam(name = "size", defaultValue = "10") String size){
        List<CommentViewDTO> response = commentService.getCommentsByTask(taskId, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }



}
