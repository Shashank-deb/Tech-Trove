package com.shashank.electronic.store.repositories;

import com.shashank.electronic.store.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User,String> {


    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndPassword(String email, String password);

    List<User> findByNameContaining(String keywords);



}
