package org.example.sem_backend.modules.notify_module;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class NotificationController {
    @MessageMapping("/notify")
    @SendTo("/topic/notifications")
    @ResponseBody
    public String sendNotification(String name) {
        return "Hello, " + name + "!";
    }
}
