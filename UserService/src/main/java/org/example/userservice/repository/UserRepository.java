package org.example.userservice.repository;

import org.example.userservice.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    User findByPhoneNo(String phoneNo);
}
