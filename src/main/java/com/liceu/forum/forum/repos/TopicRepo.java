package com.liceu.forum.forum.repos;


import com.liceu.forum.forum.model.Categories;
import com.liceu.forum.forum.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TopicRepo extends JpaRepository<Topic, Long> {
    List<Topic> findTopicsByCategoriesId(Long id);
    List<Topic> findTopicsByCategoryAndTitle(String category, String title);
    Topic findTopicBy_id(String _id);
}
