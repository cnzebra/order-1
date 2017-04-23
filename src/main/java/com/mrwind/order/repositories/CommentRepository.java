package com.mrwind.order.repositories;

import com.mrwind.order.entity.Express;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.mrwind.order.entity.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends PagingAndSortingRepository<Comment,String> {

    List<Comment> findAllByExpressNo(String expressNo);
}
