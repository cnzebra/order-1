package com.mrwind.order.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.mrwind.order.entity.Comment;
import com.mrwind.order.repositories.CommentRepository;

@Service
public class CommentService {

	
	@Autowired
	CommentRepository commentRepository;
	
	public List<Comment> findAll(PageRequest pageRequest) {
		return commentRepository.findAll(pageRequest).getContent();
	}


	public Comment insertCommnet(Comment comment) {
		Comment res = commentRepository.save(comment);
		return res;
	}

}
