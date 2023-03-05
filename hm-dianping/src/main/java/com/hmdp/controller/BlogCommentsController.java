package com.hmdp.controller;


import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Api(value = "博客评论接口", tags = {"博客评论接口"})
@RestController
@RequestMapping("/blog-comments")
public class BlogCommentsController {

}
