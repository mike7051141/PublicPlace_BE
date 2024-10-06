package com.springboot.publicplace.service;

import com.springboot.publicplace.entity.Message;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface GPTService {
    List<Message> getChatResponses(String prompt, HttpSession session);
}
