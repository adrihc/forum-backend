package com.liceu.forum.forum.services;

import com.liceu.forum.forum.model.Reply;
import com.liceu.forum.forum.model.ReplyBody;
import com.liceu.forum.forum.model.Topic;
import com.liceu.forum.forum.model.User;
import com.liceu.forum.forum.repos.ReplyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ReplyService {

    @Autowired
    ReplyRepo replyRepo;

    public void createReply(User user, Topic topic, String content) {
        Reply reply = new Reply();
        reply.setContent(content);
        reply.setUser(user);
        reply.setCreatedAt(Instant.now().toString());
        reply.setTopic(topic);
        replyRepo.save(reply);
    }



    public List<Reply> findRepliesByTopicId(Topic topic){
        return replyRepo.findRepliesByTopic_Id(topic.getId());
    }
    public Reply findReplyBiId(Long id){
        return replyRepo.findReplyBy_id(id);
    }
    public void update(Reply reply, ReplyBody body){
        reply.setContent(body.getContent());
        reply.setUpdatedAt(Instant.now().toString());
        replyRepo.save(reply);
    }
    public void delete(Reply reply){
        replyRepo.delete(reply);
    }
}
