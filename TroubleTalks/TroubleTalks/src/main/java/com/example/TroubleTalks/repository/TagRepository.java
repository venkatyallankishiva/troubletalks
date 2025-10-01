package com.example.TroubleTalks.repository;

import com.example.TroubleTalks.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Set<Tag> findByNameIn(Set<String> names);
}