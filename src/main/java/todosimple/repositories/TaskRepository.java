package todosimple.repositories;

import java.util.List;

//import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import todosimple.models.Task;

public interface TaskRepository extends JpaRepository<Task, Long>{
    
    //padrao do spring
    //List<Task> findByUser_Id(Long Id);


    //retornas as tasks baseadas na query jpql que foi fornecida
    //@Query(value = "SELECT t FROM Task t WHERE t.user.id = :id")
    //List<Task> findByUser_IdCustom(@Param("id") Long id);

    //retornas as tasks baseadas na query sql que foi fornecida
    @Query(value = "SELECT * FROM task t where t.user_id = :id", nativeQuery = true)
    List<Task> findByUser_Id(@Param("id") Long id);
}
