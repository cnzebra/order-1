package com.mrwind.order.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.mrwind.order.entity.Comment;
import com.mrwind.order.entity.Express;
import com.mrwind.order.repositories.CommentRepository;
import com.mrwind.order.repositories.ExpressRepository;

@Service
public class CommentService {

	
	@Autowired
	CommentRepository commentRepository;
	
	@Autowired
	ExpressRepository expressRepository;
	
	public List<Comment> findAll(PageRequest pageRequest) {
		return commentRepository.findAll(pageRequest).getContent();
	}

	public List<Comment> findAllByExpressNo(String expressNo){
		return commentRepository.findAllByExpressNo(expressNo);
	}


	public Comment insertCommnet(Comment comment) {
		String expressNo = comment.getExpressNo();
		Express express = expressRepository.findFirstByExpressNo(expressNo);
		if(express==null)return null;
		comment.setLines(express.getLines());
		comment.setShop(express.getShop());
		Comment res = commentRepository.save(comment);
		return res;
	}

}
