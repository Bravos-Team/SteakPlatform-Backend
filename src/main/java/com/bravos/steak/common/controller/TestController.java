package com.bravos.steak.common.controller;

import com.bravos.steak.dev.service.DownloadGameService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final DownloadGameService downloadGameService;

    public TestController(DownloadGameService downloadGameService) {
        this.downloadGameService = downloadGameService;
    }

    @GetMapping("/api/v1/user/auth/test")
    public String test(@RequestBody Request request) {
        return downloadGameService.downloadGame(request.url,request.ipAddress);
    }

    private static class Request {
        private String url;
        private String ipAddress;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }
    }

}
