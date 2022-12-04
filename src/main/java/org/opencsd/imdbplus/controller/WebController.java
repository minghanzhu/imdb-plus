package org.opencsd.imdbplus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {

    @GetMapping(value = "/home")
    public String homePage() {
        return "home";
    }

    @GetMapping(value = "/user-option")
    public String userOptionPage() {
        return "user_option";
    }

    @GetMapping(value = "/timeline-option")
    public String timelineOptionPage() {
        return "timeline_option";
    }

    @GetMapping(value = "/user-get")
    public String userGetPage() { return "user_get"; }

    @GetMapping(value = "/user-post")
    public String userPostPage() {
        return "user_post";
    }

    @GetMapping(value = "/user-delete")
    public String userDeletePage() {
        return "user_delete";
    }

    @GetMapping(value = "/timeline-post")
    public String timelinePostPage() {
        return "timeline_post";
    }

    @GetMapping(value = "/timeline-get-by-user-id")
    public String timelineGETByUserIdPage() {
        return "timeline_get_user";
    }

    @GetMapping(value = "/timeline-get-by-media-id")
    public String timelineGETByMediaIdPage() {
        return "timeline_get_media";
    }

    @GetMapping(value = "/timeline-get-by-user-media-id")
    public String timelineGETByUserMediaIdPage() {
        return "timeline_get_search";
    }

    @GetMapping(value = "/timeline-delete")
    public String timelineDeletePage() {
        return "timeline_delete";
    }

}
