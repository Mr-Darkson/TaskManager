package com.example.taskmanagment.adapters.in.rest.controllers;

import com.example.taskmanagment.adapters.in.rest.exceptions.PermissionDeniedException;
import com.example.taskmanagment.adapters.in.rest.exceptions.TaskNotFoundException;
import com.example.taskmanagment.adapters.in.rest.exceptions.UserNotFoundException;
import com.example.taskmanagment.application.domain.dto.TaskUpdateDTO;
import com.example.taskmanagment.application.domain.dto.TaskViewDTO;
import com.example.taskmanagment.application.domain.enums.PriorityLevel;
import com.example.taskmanagment.application.domain.enums.TaskStatus;
import com.example.taskmanagment.application.ports.in.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Task Controller", description = "Эндпоинты связанны с сущностью 'Task'")
@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;


    @Operation(
            summary = "Создать новую задачу",
            description = """
                    Получает DTO для создания задачи, проводит валидацию и сохраняет в базу данных.
                    ДОСТУП: Любой пользователь
                    """,
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Задача создана", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "401", description = "Ошибка аутентификации", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "403", description = "Не авторизован", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "500", description = "Ошибка на стороне сервера", content = @Content(schema = @Schema(implementation = String.class)))
            }
    )
    @PostMapping
    public ResponseEntity<String> createTask(@RequestBody @Valid TaskUpdateDTO task) {
        taskService.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body("Task created");
    }


    @Operation(
            summary = "Получить задачу по ID (UUID)",
            description = """
                    Получает ID задачи. Если она существует в базе данных, то возвращает DTO публичных данных этой задачи.
                    ДОСТУП: Любой пользователь
                    """,
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "taskId", description = "UUID задачи", required = true, schema = @Schema(implementation = String.class))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ответ получен ", content = @Content(schema = @Schema(implementation = TaskViewDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Ошибка аутентификации", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "403", description = "Не авторизован", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "404", description = "Задача не найдена", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "500", description = "Ошибка на стороне сервера", content = @Content(schema = @Schema(implementation = String.class)))
            }
    )
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskViewDTO> getTask(@PathVariable("taskId") String taskId) throws TaskNotFoundException {
        TaskViewDTO taskViewDTO = taskService.getTaskById(taskId);
        return ResponseEntity.status(HttpStatus.OK).body(taskViewDTO);
    }

    @Operation(
            summary = "Изменить задачу",
            description = """
                    Получает DTO для обновления задачи и её ID, проводит валидацию и сохраняет в базу данных.
                    ДОСТУП: Только автор задачи
                    """,
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "taskId", description = "UUID задачи", required = true, schema = @Schema(implementation = String.class))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ответ получен ", content = @Content(schema = @Schema(implementation = TaskViewDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "401", description = "Ошибка аутентификации", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "403", description = "Отказано в доступе", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "404", description = "Не найдена задача", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "500", description = "Ошибка на стороне сервера", content = @Content(schema = @Schema(implementation = String.class)))
            }
    )
    @PatchMapping("/{taskId}")
    public ResponseEntity<TaskViewDTO> updateTask(@PathVariable("taskId") String taskId,
                                                  @RequestBody @Valid TaskUpdateDTO task) throws TaskNotFoundException, UserNotFoundException, PermissionDeniedException {
        TaskViewDTO response = taskService.updateTask(taskId, task);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Удалить задачу",
            description = """
                    Получает ID задачи, и если она существует, а запрос на удаление отправляет автор, тогда сущность стирается из базы.
                    ДОСТУП: Только автор задачи
                    """,
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "taskId", description = "UUID задачи", required = true, schema = @Schema(implementation = String.class))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешно удалено"),
                    @ApiResponse(responseCode = "401", description = "Ошибка аутентификации", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "403", description = "Отказано в доступе"),
                    @ApiResponse(responseCode = "404", description = "Не найдена задача"),
                    @ApiResponse(responseCode = "500", description = "Ошибка на стороне сервера")}
    )
    @DeleteMapping("/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable("taskId") String taskId) throws TaskNotFoundException {
        taskService.deleteTask(taskId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Task deleted");
    }

    @Operation(
            summary = "Обновить статус задачи",
            description = """
                    Получает ID задачи и параметр status. Если задача с таким ID существует, параметр передан правильно, то поле status у сущности обновляется и сохраняется в базу.
                    ДОСТУП: Автор и исполнитель задачи
                    """,
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "taskId", description = "UUID задачи", required = true, schema = @Schema(implementation = String.class)),
                    @Parameter(name = "status", description = "Возможный статус задачи {WAITING, IN_PROGRESS, COMPLETED}", required = true, schema = @Schema(implementation = String.class))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Задача изменена", content = @Content(schema = @Schema(implementation = TaskViewDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Ошибка аутентификации", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "403", description = "Отказано в доступе", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "404", description = "Не найдена задача", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "500", description = "Ошибка на стороне сервера", content = @Content(schema = @Schema(implementation = String.class)))
            }
    )
    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskViewDTO> changeStatus(@PathVariable("taskId") String taskId, @RequestParam("status") TaskStatus status) throws TaskNotFoundException, UserNotFoundException, PermissionDeniedException {
        TaskViewDTO response = taskService.changeStatus(taskId, status);
        return ResponseEntity.status((HttpStatus.OK)).body(response);
    }


    @Operation(
            summary = "Обновить исполнителя задачи",
            description = """
                    Получает ID задачи и параметр assignee. Если задача с таким ID существует, параметр передан правильно, то поле assignee у сущности обновляется и сохраняется в базу.
                    ДОСТУП: Только автор задачи
                    """,
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "taskId", description = "UUID задачи", required = true, schema = @Schema(implementation = String.class)),
                    @Parameter(name = "assignee", description = "Email нового исполнителя, например {test@mail.ru}", required = true, schema = @Schema(implementation = String.class)),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Задача изменена", content = @Content(schema = @Schema(implementation = TaskViewDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Ошибка аутентификации", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "403", description = "Отказано в доступе", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "404", description = "Не найдена задача", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "500", description = "Ошибка на стороне сервера", content = @Content(schema = @Schema(implementation = String.class)))}

    )
    @PatchMapping("/{taskId}/assignee")
    public ResponseEntity<TaskViewDTO> changeAssignee(@PathVariable("taskId") String taskId,
                                                      @RequestParam("assignee") String assigneeEmail) throws TaskNotFoundException, UserNotFoundException, PermissionDeniedException {
        TaskViewDTO response = taskService.changeAssignee(taskId, assigneeEmail);
        return ResponseEntity.status((HttpStatus.OK)).body(response);
    }


    @Operation(
            summary = "Получить задачи созданные пользователем, по его ID",
            description = """
                    Получает ID пользователя и параметры status, priority, page, size. Если пользователь с таким ID существует, параметры передан правильно, то возвращает список задач согласно фильтрам и пагинации.
                    ДОСТУП: Любой пользователь
                    """,
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "taskId", description = "UUID задачи", required = true, schema = @Schema(implementation = String.class)),
                    @Parameter(name = "status", description = "Возможный статус задачи {WAITING, IN_PROGRESS, COMPLETED}", required = true, schema = @Schema(implementation = String.class)),
                    @Parameter(name = "priority", description = "Возможный приоритет задачи {LOW, MEDIUM, HIGH}", required = true, schema = @Schema(implementation = String.class)),
                    @Parameter(name = "page", description = "Текущая страница, которую нужно показать. По умолчанию: 0", required = false, schema = @Schema(implementation = String.class)),
                    @Parameter(name = "size", description = "Размер одной страницы. По умолчанию: 10", required = false, schema = @Schema(implementation = String.class)),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Задача изменена", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaskViewDTO.class)))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "401", description = "Ошибка аутентификации", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "403", description = "Отказано в доступе", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "404", description = "Не найдены задачи или автор", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "500", description = "Ошибка на стороне сервера", content = @Content(schema = @Schema(implementation = String.class)))}

    )
    @GetMapping("/{userId}/created")
    public ResponseEntity<List<TaskViewDTO>> getTasksByAuthor(@PathVariable("userId") String userId,
                                                      @RequestParam(name = "status", required = false) TaskStatus status,
                                                      @RequestParam(name = "priority", required = false) PriorityLevel priority,
                                                      @RequestParam(name = "page",defaultValue = "0") String page,
                                                      @RequestParam(name = "size",defaultValue = "10") String size) throws TaskNotFoundException, UserNotFoundException  {
        List<TaskViewDTO> tasks = taskService.getTaskByAuthor(userId, status, priority, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }


    @Operation(
            summary = "Получить задачи исполняемые пользователем, по его ID",
            description = """
                    Получает ID пользователя и параметры status, priority, page, size. Если пользователь с таким ID существует, параметры передан правильно, то возвращает список задач согласно фильтрам и пагинации.
                    ДОСТУП: Любой пользователь
                    """,
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "taskId", description = "UUID задачи", required = true, schema = @Schema(implementation = String.class)),
                    @Parameter(name = "status", description = "Возможный статус задачи {WAITING, IN_PROGRESS, COMPLETED}", required = true, schema = @Schema(implementation = String.class)),
                    @Parameter(name = "priority", description = "Возможный приоритет задачи {LOW, MEDIUM, HIGH}", required = true, schema = @Schema(implementation = String.class)),
                    @Parameter(name = "page", description = "Текущая страница, которую нужно показать. По умолчанию: 0", required = false, schema = @Schema(implementation = String.class)),
                    @Parameter(name = "size", description = "Размер одной страницы. По умолчанию: 10", required = false, schema = @Schema(implementation = String.class)),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Задача изменена", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaskViewDTO.class)))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "401", description = "Ошибка аутентификации", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "403", description = "Отказано в доступе", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "404", description = "Не найдены задачи или автор", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "500", description = "Ошибка на стороне сервера", content = @Content(schema = @Schema(implementation = String.class)))}

    )
    @GetMapping("/{userId}/perform")
    public ResponseEntity<List<TaskViewDTO>> getTasksByAssignee(@PathVariable("userId") String userId,
                                                      @RequestParam(name = "status", required = false) TaskStatus status,
                                                      @RequestParam(name = "priority", required = false) PriorityLevel priority,
                                                      @RequestParam(name = "page", defaultValue = "0") String page,
                                                      @RequestParam(name = "size", defaultValue = "10") String size) throws TaskNotFoundException, UserNotFoundException  {
        List<TaskViewDTO> tasks = taskService.getTaskByAssignee(userId, status, priority, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }





}
