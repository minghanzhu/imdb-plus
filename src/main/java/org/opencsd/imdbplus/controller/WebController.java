package org.opencsd.imdbplus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping(value = "/home")
    public String homePage() {
        return "home";
    }

    @GetMapping(value = "/client-option")
    public String clientOptionPage() {
        return "client_option";
    }

    @GetMapping(value = "/timeline-option")
    public String timelineOptionPage() {
        return "timeline_option";
    }

    @GetMapping(value = "/client-get")
    public String clientGetPage() { return "client_get"; }

    @GetMapping(value = "/client-post")
    public String clientPostPage() {
        return "client_post";
    }

    @GetMapping(value = "/client-delete")
    public String clientDeletePage() {
        return "client_delete";
    }

    @GetMapping(value = "/timeline-post")
    public String timelinePostPage() {
        return "timeline_post";
    }

    @GetMapping(value = "/timeline-get-by-client-id")
    public String timelineGETByClientIdPage() {
        return "timeline_get_client";
    }

    @GetMapping(value = "/timeline-get-by-media-id")
    public String timelineGETByMediaIdPage() {
        return "timeline_get_media";
    }

    @GetMapping(value = "/timeline-get-by-client-media-id")
    public String timelineGETByClientMediaIdPage() {
        return "timeline_get_search";
    }

    @GetMapping(value = "/timeline-delete")
    public String timelineDeletePage() {
        return "timeline_delete";
    }

    @GetMapping(value = "/analysis-option")
    public String ayalysisOptionPage() {
        return "analysis_option";
    }

    @GetMapping(value = "/get-profile")
    public String profilePage() {
        return "profile";
    }

}

