package dev.wellyngton.doable.api.repositories;

import dev.wellyngton.doable.api.models.Status;
import dev.wellyngton.doable.api.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatusNot(Status status);
}
