package com.liceu.forum.forum.model;

import com.liceu.forum.forum.repos.ReplyRepo;
import com.liceu.forum.forum.services.ReplyService;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

@Entity
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String content;

    String title;
    String category;
    String createdAt;
    String updatedAt;

    String _id;

    int _replies;
    int views;

    @OneToMany(mappedBy = "topic",cascade = CascadeType.REMOVE)
    List<Reply> replies;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Categories categories;

    public void setReplies(List<Reply> replies) {
        this.replies = replies;
    }

    public Long getId() {
        return id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }


    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String get_id() {
        return _id;
    }

    public int get_replies() {
        return _replies;
    }

    public void set_replies(int _replies) {
        this._replies = _replies;
    }



    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Categories getCategories() {
        return categories;
    }

    public void setCategories(Categories categories) {
        this.categories = categories;
    }
}
