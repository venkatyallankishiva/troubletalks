package com.example.TroubleTalks.service;

import com.example.TroubleTalks.entity.Tag;
import com.example.TroubleTalks.repository.TagRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }


    @Transactional
    public Set<Tag> findOrCreateTags(String tagNamesString) {
        if (tagNamesString == null || tagNamesString.trim().isEmpty()) {
            return new HashSet<>();
        }

        Set<String> requestedTagNames = Arrays.stream(tagNamesString.split(","))
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        if (requestedTagNames.isEmpty()) {
            return new HashSet<>();
        }

        Set<Tag> existingTags = tagRepository.findByNameIn(requestedTagNames);
        Set<String> existingTagNames = existingTags.stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());

        Set<Tag> newTags = requestedTagNames.stream()
                .filter(name -> !existingTagNames.contains(name))
                .map(Tag::new)
                .collect(Collectors.toSet());

        tagRepository.saveAll(newTags);

        existingTags.addAll(newTags);
        return existingTags;
    }

    public Set<Tag> findAllTags() {
        return new HashSet<>(tagRepository.findAll());
    }
}