package todosimple.services;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import todosimple.models.Task;
import todosimple.models.User;
import todosimple.repositories.TaskRepository;

public class Taskservices {
    
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    public Task findById(Long id){
        Optional<Task> task = this.taskRepository.findById(id);
        return task.orElseThrow(() -> new RuntimeException(
            " Task not found! Id: " + id + "Type: " + Task.class.getName()
            ));
     }

    @Transactional
    public Task create(Task obj){
        User user = this.userService.findById(obj.getUser().getId());

        obj.setId(null);
        obj.setUser(user);
        obj = this.taskRepository.save(obj);
        return obj;
    }

    @Transactional
    public Task update(Task obj){
        Task newObj = findById(obj.getId());
        newObj.setDescription(obj.getDescription());
        return this.taskRepository.save(newObj);
    }

    public Task delete(Long id){
        Task deletedTask = findById(id);

        this.taskRepository.deleteById(id);
        return deletedTask;
    }

}
