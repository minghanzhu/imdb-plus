package com.example.imdbplus.conroller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {
    @RequestMapping(value = "/home")
    public String homePage() {
        return "home";
    }

    @RequestMapping(value = "/user-option")
    public String userOptionPage() {
        return "user_option";
    }

    @RequestMapping(value = "/timeline-option")
    public String timelineOptionPage() {
        return "timeline_option";
    }

    @RequestMapping(value = "/user-get")
    public String userGetPage() { return "user_get"; }

    @RequestMapping(value = "/user-post")
    public String userPostPage() {
        return "user_post";
    }

    @RequestMapping(value = "/user-put")
    public String userPutPage() {
        return "user_put";
    }

    @RequestMapping(value = "/user-delete")
    public String userDeletePage() {
        return "user_delete";
    }

    @RequestMapping(value = "/timeline-post")
    public String timelinePostPage() {
        return "timeline_post";
    }

    @RequestMapping(value = "/timeline-get-by-user-id")
    public String timelineGETByUserIdPage() {
        return "timeline_get_user";
    }

    @RequestMapping(value = "/timeline-get-by-media-id")
    public String timelineGETByMediaIdPage() {
        return "timeline_get_media";
    }

    @RequestMapping(value = "/timeline-get-by-user-media-id")
    public String timelineGETByUserMediaIdPage() {
        return "timeline_get_search";
    }

    @RequestMapping(value = "/timeline-delete")
    public String timelineDeletePage() {
        return "timeline_delete";
    }

}
