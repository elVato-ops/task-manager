package taskmanager.task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task>
{
    Page<Task> findByProjectId(Long id, Pageable pageable);

    @Query("SELECT status AS status, COUNT(*) AS count FROM Task GROUP BY status")
    List<TaskStatusCount> countByStatus();
}