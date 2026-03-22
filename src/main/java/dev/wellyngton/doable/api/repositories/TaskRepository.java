package dev.wellyngton.doable.api.repositories;

import dev.wellyngton.doable.api.models.Status;
import dev.wellyngton.doable.api.models.Task;
import dev.wellyngton.doable.api.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatusNotAndOwner(Status status, User owner);
    Optional<Task> findByIdAndOwner(Long id, User owner);
}
