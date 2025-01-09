package com.spring.back.controller.admin;

import com.spring.back.model.User;
import com.spring.back.model.Result;
import com.spring.back.model.PageRequest;
import com.spring.back.model.PageResult;
import com.spring.back.service.UserService;
import com.spring.back.annotation.RequireAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequireAdmin
public class AdminUserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public Result<PageResult<User>> findPage(PageRequest pageRequest) {
        return userService.findPage(pageRequest);
    }
    
    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable Integer id, @RequestParam Boolean status) {
        return userService.updateStatus(id, status);
    }
    
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Integer id) {
        return userService.delete(id);
    }
} 