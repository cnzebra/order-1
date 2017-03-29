package com.mrwind.order.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.mrwind.order.entity.Comment;

@Repository
public interface CommentRepository extends PagingAndSortingRepository<Comment,String> {
}
