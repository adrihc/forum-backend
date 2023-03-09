package com.liceu.forum.forum.controller;

import com.liceu.forum.forum.model.*;
import com.liceu.forum.forum.services.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
public class TopicController {
    @Autowired
    CategoriesService categoriesService;
    @Autowired
    TopicService topicService;
    @Autowired
    TokenService tokenService;
    @Autowired
    UserService userService;
    @Autowired
    ReplyService replyService;

    @GetMapping("/categories/{slug}/topics")
    @CrossOrigin
    public List<Topic> getCategoryTopics(@PathVariable String slug) throws UnsupportedEncodingException {
        String encodedSlug = URLEncoder.encode(slug, "UTF-8").replaceAll("\\+", "%20");
        if (categoriesService.trySlug(encodedSlug)){
            Categories category = categoriesService.findBySlug(encodedSlug);
            List<Topic> topics = topicService.findAllTopics(category);
            return topics;
        } else {
            return null;
        }
    }

    @PostMapping("/topics")
    @CrossOrigin
    public ResponseEntity<Topic> addTopics(@RequestBody TopicBody body) throws UnsupportedEncodingException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization").replace("Bearer ","");
        String userString = tokenService.getUser(token);
        User user = userService.catchUserEmail(userString).get(0);
        Topic topic = topicService.topicBuilder(body,user, body.getCategory());
        topicService.save(topic);

        return ResponseEntity.ok().body(topic);
    }

    @GetMapping("/topics/{_id}")
    @CrossOrigin
    public ResponseEntity<Map<String,Object>> getTopic(@PathVariable("_id")String _id){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization").replace("Bearer ","");
        String userString = tokenService.getUser(token);
        User user = userService.catchUserEmail(userString).get(0);
        Topic topic = topicService.findTopic(_id);
        List<Reply> replies = replyService.findRepliesByTopicId(topic);
        Map<String, Object> map = new HashMap<>();
        map.put("category",topic.getCategory());
        map.put("content",topic.getContent());
        map.put("user", topic.getUser());
        map.put("createdAt", topic.getCreatedAt());
        map.put("replies",replies);
        map.put("_id",topic.get_id());
        map.put("title",topic.getTitle());
        map.put("permissions", userService.createPermissions(user));
        return ResponseEntity.ok().body(map);
    }

    @PutMapping("/topics/{_id}")
    @CrossOrigin
    public ResponseEntity<Topic> updateTopic(@PathVariable("_id")String _id, @RequestBody TopicBody body){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization").replace("Bearer ","");
        String userString = tokenService.getUser(token);
        User user = userService.catchUserEmail(userString).get(0);
        Topic topic = topicService.findTopic(_id);
        if (user.getRole().equals("admin")||topic.getUser()==user){
            topic.setContent(body.getContent());
            topic.setTitle(body.getTitle());
            topic.setUpdatedAt(Instant.now().toString());
            topicService.save(topic);
            return ResponseEntity.ok().body(topic);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping("/topics/{_id}")
    @CrossOrigin
    public ResponseEntity<Void> deleteTopic(@PathVariable("_id")String _id){
        Topic topic = topicService.findTopic(_id);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization").replace("Bearer ","");
        String userString = tokenService.getUser(token);
        User user = userService.catchUserEmail(userString).get(0);
        if (user.getRole().equals("admin")|| user == topic.getUser()){
            topicService.deleteTopic(topic);
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @PostMapping("/topics/{_id}/replies")
    @CrossOrigin
    public ResponseEntity<Topic> postReplies(@PathVariable("_id")String _id, @RequestBody ReplyBody body){
        Topic topic = topicService.findTopic(_id);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization").replace("Bearer ","");
        String userString = tokenService.getUser(token);
        User user = userService.catchUserEmail(userString).get(0);
        if (user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        replyService.createReply(user,topic, body.getContent());
        return ResponseEntity.ok().body(topic);
    }

    @PutMapping("/topics/{_id}/replies/{_idReply}")
    @CrossOrigin
    public ResponseEntity<Topic> updateReplies(@PathVariable("_id")String _id,@PathVariable("_idReply")String _idReply, @RequestBody ReplyBody body){
        Topic topic = topicService.findTopic(_id);
        Reply reply = replyService.findReplyBiId(Long.parseLong(_idReply));

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization").replace("Bearer ","");
        String userString = tokenService.getUser(token);
        User user = userService.catchUserEmail(userString).get(0);
        if (user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else if (user.getRole().equals("admin")|| user == reply.getUser()){
            replyService.update(reply,body);
            return ResponseEntity.ok().body(topic);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping("/topics/{_id}/replies/{_idReply}")
    @CrossOrigin
    public ResponseEntity<Void> updateReplies(@PathVariable("_id")String _id,@PathVariable("_idReply")String _idReply){
        Reply reply = replyService.findReplyBiId(Long.parseLong(_idReply));

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization").replace("Bearer ","");
        String userString = tokenService.getUser(token);
        User user = userService.catchUserEmail(userString).get(0);

        if (user.getRole().equals("admin")|| user == reply.getUser()){
            replyService.delete(reply);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
