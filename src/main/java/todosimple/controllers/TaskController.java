package todosimple.controllers;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import todosimple.models.Task;
import todosimple.services.TaskService;
import todosimple.services.UserService;


@RestController
@RequestMapping("/task/")
@Validated
public class TaskController {
    
    @Autowired
    private  TaskService taskService;

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<Task> findById(@PathVariable Long id){
        Task task = this.taskService.findById(id);
        return ResponseEntity.ok().body(task);
    }

    @GetMapping("/user/{userid}")
    public ResponseEntity<List<Task>> findAllById(@PathVariable Long userid){
        this.userService.findById(userid);
        List<Task> objs = this.taskService.findAllById(userid);
        return ResponseEntity.ok().body(objs);
    }


    @PostMapping
    public ResponseEntity<Task> create(@RequestBody @Valid Task obj){
        this.taskService.create(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}").buildAndExpand(obj.getId()).toUri();
        return ResponseEntity.created(uri).body(obj);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> update(@RequestBody @Valid Task obj, @PathVariable Long id){
        obj.setId(id);
        this.taskService.update(obj);
        return ResponseEntity.ok().body(obj);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Task> delete(@PathVariable Long id){
        Task tsk = this.taskService.findById(id);
        this.taskService.delete(id);
        return ResponseEntity.ok().body(tsk);
    }


}
