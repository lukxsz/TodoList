package br.com.dev.lukas.TodoList.task;


import br.com.dev.lukas.TodoList.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {

        var idUser = request.getAttribute("idUser");
        taskModel.setUserId((UUID)idUser);

        var currentDate = LocalDate.now();

        if(currentDate.isAfter(ChronoLocalDate.from(taskModel.getStartAt())) || currentDate.isAfter(ChronoLocalDate.from(taskModel.getEndAt()))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início / data de terminio deve ser " +
                    "maior que a data " +
                    "atual");
        }

        if(taskModel.getStartAt().isAfter(taskModel.getEndAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A data de inicio deve ser maior do que a data de terminio");
        }

        var task = this.taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request) {
        var idUser = request.getAttribute("idUser");
        var tasks = this.taskRepository.findByUserId((UUID)idUser);
        return tasks;
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, @PathVariable UUID  id, HttpServletRequest request) {

        var task = this.taskRepository.findById(id).orElse(null);

        if(task == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tareda não encontrada");
        }

        var idUser = request.getAttribute("idUser");

        if(!task.getUserId().equals(idUser)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário não tem permissão de alterar essa " +
                    "tarefa");
        }

        Utils.copyNonNullProperties(taskModel, task);
        var taskupdated = this.taskRepository.save(task);
        return ResponseEntity.status(HttpStatus.OK).body(taskupdated);


    }
}
