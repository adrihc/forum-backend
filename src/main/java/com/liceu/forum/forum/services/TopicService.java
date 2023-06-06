package com.liceu.forum.forum.services;

import com.liceu.forum.forum.model.*;
import com.liceu.forum.forum.repos.TopicRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class TopicService {
    @Autowired
    TopicRepo topicRepo;
    @Autowired
    CategoriesService categoriesService;
    @Autowired
    ReplyService replyService;
    public List<Topic> findAllTopics(Categories category){
        System.out.println(category.getId());
        List<Topic> topics = topicRepo.findTopicsByCategoriesId(category.getId());
        return topics;
    }

    public void save(Topic topic){
        topicRepo.save(topic);
        Topic topic1 = topicRepo.findTopicsByCategoryAndTitle(topic.getCategory(),topic.getTitle()).get(0);
        topic1.set_id(topic1.getId().toString());
        topicRepo.save(topic1);
    }

    public Topic topicBuilder(TopicBody body, User user,String slug) throws UnsupportedEncodingException {
        String encodedSlug = URLEncoder.encode(body.getCategory(), "UTF-8").replaceAll("\\+", "%20");
        Categories categories = categoriesService.findBySlug(slug);

        Topic topic = new Topic();
        topic.setUser(user);
        topic.setCategory(encodedSlug);
        topic.setTitle(body.getTitle());
        topic.setCreatedAt(Instant.now().toString());
        topic.setContent(body.getContent());
        topic.setCategories(categories);
        return topic;
    }

    public Topic findTopic(String _id){
        return topicRepo.findTopicBy_id(_id);
    }
    public void  deleteTopic(Topic topic){
        List<Reply> replies = replyService.findRepliesByTopicId(topic);
        for (Reply r: replies) {
            replyService.delete(r);
        }
        topicRepo.delete(topic);
    }
}
