package com.liceu.forum.forum.repos;

import com.liceu.forum.forum.model.Reply;
import com.liceu.forum.forum.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepo extends JpaRepository<Reply, Long>  {
    List<Reply> findRepliesByTopic_Id(Long id);
    Reply findReplyBy_id(Long _id);
}
