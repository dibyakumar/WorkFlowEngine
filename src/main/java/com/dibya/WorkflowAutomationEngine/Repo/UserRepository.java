package com.dibya.WorkflowAutomationEngine.Repo;

import com.dibya.WorkflowAutomationEngine.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByUserName(String username);
}
